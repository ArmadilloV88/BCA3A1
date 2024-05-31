using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging; // Add this namespace
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.ERD;
using PROGPOEPART2ST10091991.Models.Farmer;
using PROGPOEPART2ST10091991.Models.Product;
using System.Linq;

namespace PROGPOEPART2ST10091991.Controllers
{
    [Route("EmployeeDashboard")]
    public class EmployeeDashboardController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<EmployeeDashboardController> _logger; // Add logger field
        private static int GlobalID = 0;
        public EmployeeDashboardController(AppDbContext context, ILogger<EmployeeDashboardController> logger) // Inject logger
        {
            _context = context;
            _logger = logger;
        }
        [Route("Index")]
        public IActionResult Index(int userId)
        {
            GlobalID = userId;
            _logger.LogInformation($"Index action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
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


        [HttpGet]
        [Route("EmployeeDashboard/Products")]
        public IActionResult Products()
        {
            var viewModel = new ProductSearchViewModel
            {
                Results = new List<PROGPOEPART2ST10091991.Models.ERD.Product>()
            };

            return View(viewModel);
        }

        [HttpPost]
        [Route("EmployeeDashboard/Products/Search")]
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

        [HttpPost]
        [Route("FavoriteProduct")]
        public async Task<IActionResult> FavoriteProduct(int productId)
        {
            int userId = GlobalID;

            _logger.LogInformation($"FavoriteProduct action called with userId: {userId} and productId: {productId}");

            var employee = await _context.Employee.FirstOrDefaultAsync(e => e.UserID == userId);
            if (employee == null)
            {
                _logger.LogError($"Employee not found for userId: {userId}");
                return Json(new { success = false, message = "Employee not found." });
            }

            int employeeId = employee.EmployeeID;
            _logger.LogInformation($"Employee found with employeeId: {employeeId}");

            var productExists = await _context.Products.AnyAsync(p => p.ProductID == productId);
            if (!productExists)
            {
                _logger.LogError($"Product not found with productId: {productId}");
                return Json(new { success = false, message = "Product not found." });
            }

            var existingFavorite = await _context.EmployeeProducts
                                                 .FirstOrDefaultAsync(ep => ep.EmployeeID == employeeId && ep.ProductID == productId);
            if (existingFavorite != null)
            {
                _logger.LogWarning($"Product already favorited. EmployeeId: {employeeId}, ProductId: {productId}");
                return Json(new { success = false, message = "Product already favorited." });
            }

            var employeeProduct = new EmployeeProduct
            {
                EmployeeID = employeeId,
                ProductID = productId
            };

            _context.EmployeeProducts.Add(employeeProduct);
            await _context.SaveChangesAsync();

            _logger.LogInformation($"Product favorited successfully. EmployeeId: {employeeId}, ProductId: {productId}");

            return Json(new { success = true, message = "Product favorited successfully." });
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

        [Route("Farmers")]
        public IActionResult Farmers()
        {
            _logger.LogInformation("Farmers action called"); // Log action
            // Retrieve list of farmers with details from the database
            var farmers = _context.Farmers
                                .Join(_context.Users, f => f.UserID, u => u.UserID, (f, u) => new { Farmer = f, User = u })
                                .Select(farmer => new FarmerViewModel // Assuming you have a ViewModel to display farmer details
                                {
                                    UserID = farmer.User.UserID,
                                    Name = farmer.User.Name,
                                    Surname = farmer.User.Surname,
                                    // Add other properties as needed
                                })
                                .ToList();

            return View(farmers); // Pass farmers to the view
        }

        [HttpPost]
        [Route("FollowFarmer")]
        public async Task<IActionResult> FollowFarmer(int userId)
        {
            try
            {
                _logger.LogInformation($"\x1b[34mAttempting to follow farmer. UserID: {userId}\x1b[0m");

                // Use the GlobalID to find the correct Employee ID
                var employee = await _context.Employee.FirstOrDefaultAsync(e => e.UserID == GlobalID);

                if (employee == null)
                {
                    _logger.LogWarning($"\x1b[34mEmployee not found for GlobalID: {GlobalID}\x1b[0m");
                    return Json(new { success = false, message = "Employee not found." });
                }

                _logger.LogInformation($"\x1b[34mFound employee: {employee.EmployeeID}\x1b[0m");

                // Find the farmer based on the provided userId
                var farmer = await _context.Farmers.FirstOrDefaultAsync(f => f.UserID == userId);

                if (farmer == null)
                {
                    _logger.LogWarning($"\x1b[34mFarmer not found for userID: {userId}\x1b[0m");
                    return Json(new { success = false, message = "Farmer not found." });
                }

                _logger.LogInformation($"\x1b[34mFound farmer: {farmer.FarmerID}\x1b[0m");

                var favorite = new Favorite
                {
                    EmployeeID = employee.EmployeeID,
                    FarmerID = farmer.FarmerID
                };

                _context.Favorites.Add(favorite);
                await _context.SaveChangesAsync();

                _logger.LogInformation($"\x1b[34mSuccessfully followed farmer. UserID: {userId}\x1b[0m");

                return Json(new { success = true, message = "Farmer followed successfully." });
            }
            catch (Exception ex)
            {
                _logger.LogError($"\x1b[34mError following farmer: {ex.Message}\x1b[0m");
                return Json(new { success = false, message = "Database error. Please check the Employees and Farmers tables." });
            }
        }

        [HttpGet]
        [Route("GetFarmerDetails")]
        public IActionResult GetFarmerDetails(int userId)
        {
            var farmer = _context.Farmers
                            .Include(f => f.User) // Ensure the User navigation property is included
                            .FirstOrDefault(f => f.UserID == userId);

            if (farmer != null)
            {
                var farmerDetails = new
                {
                    name = farmer.User.Name,
                    surname = farmer.User.Surname,
                    products = _context.Products
                                    .Where(p => p.FarmerID == farmer.FarmerID)
                                    .Select(p => new { productName = p.ProductName, productDescription = p.ProductDescription })
                                    .ToList()
                };

                return Json(farmerDetails);
            }

            return Json(null);
        }
    }
}