using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.ERD;
using PROGPOEPART2ST10091991.Models.Register;
using System.Linq;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class RegisterController : Controller
    {
        private readonly AppDbContext _context;

        public RegisterController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult Index()
        {
            return View("Register");
        }

        [HttpPost]
        public IActionResult Index(RegisterViewModel model)
        {
            if (ModelState.IsValid)
            {
                // Check if the username already exists
                var existingUser = _context.Users.FirstOrDefault(u => u.Username == model.Username);
                if (existingUser != null)
                {
                    ModelState.AddModelError("", "Username already exists. Please choose a different username.");
                    return View("Register", model);
                }

                // Create a new user
                var newUser = new User
                {
                    Username = model.Username,
                    Password = model.Password,
                    // Add additional properties as needed
                };

                _context.Users.Add(newUser);
                _context.SaveChanges();

                // Redirect to login page after successful registration
                return RedirectToAction("Index", "Login");
            }

            return View("Register", model);
        }
    }
}
