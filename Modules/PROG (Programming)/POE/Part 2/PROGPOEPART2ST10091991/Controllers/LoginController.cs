using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.Login;
using System.Linq;
using Microsoft.Extensions.Logging;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class LoginController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<LoginController> _logger;

        public LoginController(AppDbContext context, ILogger<LoginController> logger)
        {
            _context = context;
            _logger = logger;
        }

        [HttpGet]
        public IActionResult Index()
        {
            _logger.LogInformation("Login Index action called");
            return View("Login");
        }

        [HttpPost]
        public IActionResult Index(LoginViewModel model)
        {
            _logger.LogInformation("Login POST action called");
            if (ModelState.IsValid)
            {
                var user = _context.Users.FirstOrDefault(u => u.Username == model.Username && u.Password == model.Password);
                if (user != null)
                {
                    _logger.LogInformation($"User {model.Username} authenticated");
                    return RedirectToAction("Redirect", "Dashboard", new { userId = user.UserID });
                }
                else
                {
                    ModelState.AddModelError("", "Invalid username or password.");
                }
            }

            return View("Login", model);
        }
    }
}
