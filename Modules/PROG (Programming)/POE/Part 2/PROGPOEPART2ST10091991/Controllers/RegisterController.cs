using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.Register;
using System.Linq;
using Microsoft.Extensions.Logging;
using PROGPOEPART2ST10091991.Models.ERD;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class RegisterController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<RegisterController> _logger;

        public RegisterController(AppDbContext context, ILogger<RegisterController> logger)
        {
            _context = context;
            _logger = logger;
        }

        [HttpGet]
        public IActionResult Index()
        {
            _logger.LogInformation("Register Index action called");
            return View("Register");
        }

        [HttpPost]
        public IActionResult Index(RegisterViewModel model)
        {
            _logger.LogInformation("Register POST action called");
            if (ModelState.IsValid)
            {
                var existingUser = _context.Users.FirstOrDefault(u => u.Username == model.Username);
                if (existingUser != null)
                {
                    ModelState.AddModelError("", "Username already exists. Please choose a different username.");
                    return View("Register", model);
                }

                var newUser = new User
                {
                    Username = model.Username,
                    Password = model.Password,
                };

                _context.Users.Add(newUser);
                _context.SaveChanges();

                _logger.LogInformation($"New user registered: {model.Username}");
                return RedirectToAction("Index", "Login");
            }

            return View("Register", model);
        }
    }
}