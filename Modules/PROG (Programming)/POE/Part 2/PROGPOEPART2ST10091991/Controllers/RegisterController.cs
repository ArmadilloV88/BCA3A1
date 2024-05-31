using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.Register;
using System.Linq;
using Microsoft.Extensions.Logging;
using PROGPOEPART2ST10091991.Models.ERD;
using System.Text.RegularExpressions;

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

            if (!ModelState.IsValid)
            {
                return View("Register", model);
            }

            if (!IsValidPassword(model.Password))
            {
                ModelState.AddModelError("Password", "Password must contain at least one uppercase letter, one lowercase letter, one special character, and one numeric digit.");
                return View("Register", model);
            }

            var existingUser = _context.Users.FirstOrDefault(u => u.Username == model.Username);
            if (existingUser != null)
            {
                ModelState.AddModelError("", "Username already exists. Please choose a different username.");
                return View("Register", model);
            }

            // Automatically assign the Farmer Tag (TagID = 2)
            const int FarmerTagId = 2;
            var farmerTag = _context.Tag.FirstOrDefault(t => t.TagID == FarmerTagId);
            if (farmerTag == null)
            {
                ModelState.AddModelError("", "The Farmer tag is not available. Please contact support.");
                return View("Register", model);
            }

            var newUser = new User
            {
                Username = model.Username,
                Password = model.Password,
                Name = model.Name,
                Surname = model.Surname,
                Age = model.Age,
                Email = model.Email,
                Gender = model.Gender,
                TagID = FarmerTagId
            };

            try
            {
                _context.Users.Add(newUser);
                _context.SaveChanges();

                // Get the UserID of the newly created user
                int newUserId = newUser.UserID;

                // Create a new Farmer entry with the UserID
                var newFarmer = new Farmer
                {
                    UserID = newUserId
                };
                _context.Farmers.Add(newFarmer);
                _context.SaveChanges();

                _logger.LogInformation($"New user registered: {model.Username}");
                return RedirectToAction("Index", "Login");
            }
            catch (Exception ex)
            {
                _logger.LogError($"Error registering user: {ex.Message}");
                ModelState.AddModelError("", "An error occurred while registering the user. Please try again later.");
                return View("Register", model);
            }
        }

        private bool IsValidPassword(string password)
        {
            var hasUpperCase = new Regex(@"[A-Z]+");
            var hasLowerCase = new Regex(@"[a-z]+");
            var hasNumbers = new Regex(@"[0-9]+");
            var hasSpecialChar = new Regex(@"[\W_]+");

            return hasUpperCase.IsMatch(password) &&
                   hasLowerCase.IsMatch(password) &&
                   hasNumbers.IsMatch(password) &&
                   hasSpecialChar.IsMatch(password);
        }
    }
}