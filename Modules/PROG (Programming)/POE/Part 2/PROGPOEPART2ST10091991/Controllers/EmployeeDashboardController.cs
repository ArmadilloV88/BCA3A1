using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging; // Add this namespace
using PROGPOEPART2ST10091991.Data;
using System.Linq;

namespace PROGPOEPART2ST10091991.Controllers
{
    [Route("EmployeeDashboard")]
    public class EmployeeDashboardController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<EmployeeDashboardController> _logger; // Add logger field

        public EmployeeDashboardController(AppDbContext context, ILogger<EmployeeDashboardController> logger) // Inject logger
        {
            _context = context;
            _logger = logger;
        }

        [Route("Index")]
        public IActionResult Index(int userId)
        {
            _logger.LogInformation($"Index action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
            }
            return View();
        }

        [Route("MyProfile")]
        public IActionResult MyProfile(int userId)
        {
            _logger.LogInformation($"MyProfile action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
            }
            return View();
        }

        [Route("Products")]
        public IActionResult Products(int userId)
        {
            _logger.LogInformation($"Products action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
            }
            return View();
        }

        [Route("Highlights")]
        public IActionResult Highlights(int userId)
        {
            _logger.LogInformation($"Highlights action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
            }
            return View();
        }

        [Route("Farmers")]
        public IActionResult Farmers(int userId)
        {
            _logger.LogInformation($"Farmers action called with userId: {userId}"); // Log userId
            var user = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (user != null)
            {
                var username = user.Username;
                ViewBag.Username = username;
                _logger.LogInformation($"Username retrieved: {username}"); // Log username
            }
            return View();
        }
    }
}
