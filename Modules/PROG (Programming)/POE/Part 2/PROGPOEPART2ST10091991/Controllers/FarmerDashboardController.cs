using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.ERD;
using Microsoft.Extensions.Logging;
using System;
using System.Linq;
using PROGPOEPART2ST10091991.Models.Product;
using Microsoft.EntityFrameworkCore;

namespace PROGPOEPART2ST10091991.Controllers
{
    [Route("FarmerDashboard")]
    public class FarmerDashboardController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<FarmerDashboardController> _logger;
        private static int GlobalID = 0;
        public FarmerDashboardController(AppDbContext context, ILogger<FarmerDashboardController> logger)
        {
            _context = context;
            _logger = logger;
        }

        [Route("Index")]
        public IActionResult Index(int userId)
        {
            GlobalID = userId;
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
            }

            // Retrieve and filter news feed entries
            var currentDate = DateTime.Now;
            var newsFeedEntries = _context.NewsFeeds
                                          .Where(n => n.NewsFeedDateExp >= currentDate)
                                          .OrderByDescending(n => n.NewsFeedDate)
                                          .ToList();
            ViewBag.NewsFeedEntries = newsFeedEntries;

            return View();
        }

        [Route("MyProfile")]
        public async Task<IActionResult> MyProfile(int userId)
        {
            userId = GlobalID;
            _logger.LogInformation($"MyProfile action called with userId: {userId}"); // Log userId
            var user = await _context.Users.FirstOrDefaultAsync(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                var accountType = user.TagID == 1 ? "Employee" : (user.TagID == 2 ? "Farmer" : "Unknown");
                ViewBag.Username = username;
                ViewBag.AccountType = accountType;
                ViewBag.Name = user.Name;
                ViewBag.Surname = user.Surname;
                ViewBag.Age = user.Age;
                ViewBag.Email = user.Email;
                ViewBag.Gender = user.Gender;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
                _logger.LogInformation($"Account type: {accountType}"); // Log account type

                // Retrieve employee ID using UserID
                var employeeId = await _context.Employee.Where(e => e.UserID == userId)
                                                         .Select(e => e.EmployeeID)
                                                         .FirstOrDefaultAsync();

                if (employeeId != 0)
                {
                    // Retrieve favorite farmers using the employee ID
                    var favoriteFarmers = await _context.Favorites.Where(f => f.EmployeeID == employeeId)
                                                                  .Select(f => f.FarmerID)
                                                                  .ToListAsync();

                    // Retrieve farmer names and surnames using FarmerIDs
                    var farmerNames = await _context.Users
                        .Where(u => favoriteFarmers.Contains(u.UserID))
                        .Select(u => new { u.Name, u.Surname })
                        .ToListAsync();

                    ViewBag.FavoriteFarmers = farmerNames;
                    _logger.LogInformation($"Favorite farmers retrieved: {string.Join(", ", farmerNames.Select(f => f.Name + " " + f.Surname))}"); // Log favorite farmers

                    // Retrieve products of the favorite farmers
                    var farmerProducts = await _context.Products
                        .Where(p => favoriteFarmers.Contains(p.FarmerID))
                        .Select(p => new { p.ProductID, p.ProductName })
                        .ToListAsync();

                    ViewBag.FarmerProducts = farmerProducts;
                    _logger.LogInformation($"Farmer products retrieved: {string.Join(", ", farmerProducts.Select(p => p.ProductName))}"); // Log farmer products
                }
                else
                {
                    _logger.LogInformation($"No employee found for UserID: {userId}");
                }
            }
            else
            {
                _logger.LogInformation($"User not found for UserID: {userId}");
            }

            return View();
        }
        [Route("MyProducts")]
        public IActionResult MyProducts()
        {
            var userId = GlobalID;
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;

                var farmer = _context.Farmers.FirstOrDefault(f => f.UserID == userId);
                if (farmer != null)
                {
                    var products = _context.Products.Where(p => p.FarmerID == farmer.FarmerID).ToList();
                    return View(products);
                }
            }
            return View();
        }

        [HttpGet]
        [Route("FarmerDashboard/Products")]
        public IActionResult Products()
        {
            var viewModel = new ProductSearchViewModel
            {
                Results = new List<PROGPOEPART2ST10091991.Models.ERD.Product>()
            };

            return View(viewModel);
        }

        [HttpPost]
        [Route("FarmerDashboard/Products/Search")]
        public IActionResult Search(ProductSearchViewModel model)
        {
            var query = _context.Products.AsQueryable();

            if (!string.IsNullOrEmpty(model.SearchTerm))
            {
                query = query.Where(p => p.ProductName.Contains(model.SearchTerm));
            }

            if (model.AlphabetFilter.HasValue)
            {
                query = query.Where(p => p.ProductName.StartsWith(model.AlphabetFilter.Value.ToString()));
            }

            if (model.StartDate.HasValue)
            {
                query = query.Where(p => p.ProductDate >= model.StartDate.Value);
            }

            if (model.EndDate.HasValue)
            {
                query = query.Where(p => p.ProductDate <= model.EndDate.Value);
            }

            if (model.FarmerID.HasValue)
            {
                query = query.Where(p => p.FarmerID == model.FarmerID.Value);
            }

            model.Results = query.ToList();

            return View("Products", model);
        }

        [Route("Highlights")]
        public IActionResult Highlights()
        {
            var userId = GlobalID;
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
            }

            var newsFeedEntries = _context.NewsFeeds.OrderByDescending(n => n.NewsFeedDate).ToList();
            ViewBag.NewsFeedEntries = newsFeedEntries;

            return View();
        }

        [HttpGet]
        [Route("CreateProduct")]
        public IActionResult CreateProduct()
        {
            int userId = GlobalID;
            ViewBag.UserID = userId;
            return View();
        }

        [HttpPost]
        [Route("CreateProduct")]
        public IActionResult CreateProduct(Product product)
        {
            int userId = GlobalID;
            if (ModelState.IsValid)
            {
                var farmer = _context.Farmers.FirstOrDefault(f => f.UserID == userId);
                if (farmer != null)
                {
                    product.FarmerID = farmer.FarmerID;
                    product.ProductDate = DateTime.Now;
                    _context.Products.Add(product);
                    _context.SaveChanges();

                    // Add new entry to NewsFeed
                    var newsFeed = new NewsFeed
                    {
                        NewsFeedName = $"New product added: {product.ProductName}",
                        NewsFeedDate = DateTime.Now,
                        NewsFeedDateExp = DateTime.Now.AddDays(30) // Example expiration date
                    };
                    _context.NewsFeeds.Add(newsFeed);
                    _context.SaveChanges();

                    _logger.LogInformation($"\u001b[33mNew product created successfully. Product ID: {product.ProductID}, Farmer ID: {farmer.FarmerID}\u001b[0m");
                    return RedirectToAction("MyProducts");
                }
            }
            else
            {
                _logger.LogWarning($"\u001b[33mProduct creation failed. Invalid model state.\u001b[0m");
            }

            ViewBag.UserID = userId;
            return View(product);
        }
    }
}
