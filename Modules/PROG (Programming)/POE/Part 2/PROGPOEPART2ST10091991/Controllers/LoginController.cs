using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using PROGPOEPART2ST10091991.Models.Login;
using System.Linq;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class LoginController : Controller
    {
        private readonly AppDbContext _context;

        public LoginController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult Index()
        {
            return View("Login");
        }

        [HttpPost]
        public IActionResult Index(LoginViewModel model)
        {
            if (ModelState.IsValid)
            {
                var user = _context.Users.FirstOrDefault(u => u.Username == model.Username && u.Password == model.Password);
                if (user != null)
                {
                    // Authentication successful
                    // Redirect to dashboard with user ID
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