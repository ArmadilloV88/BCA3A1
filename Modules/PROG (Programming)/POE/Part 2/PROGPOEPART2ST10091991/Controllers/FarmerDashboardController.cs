using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.ERD;
using Microsoft.Extensions.Logging;
using System;
using System.Linq;
using PROGPOEPART2ST10091991.Models.Product;

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
        public IActionResult MyProfile()
        {
            var userId = GlobalID;
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                var name = user.Name;
                var surname = user.Surname;
                var age = user.Age;
                var email = user.Email;
                var gender = user.Gender;

                ViewBag.Username = username;
                ViewBag.Name = name;
                ViewBag.Surname = surname;
                ViewBag.Age = age;
                ViewBag.Email = email;
                ViewBag.Gender = gender;
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
