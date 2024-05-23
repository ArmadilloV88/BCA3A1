using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using System.Linq;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class DashboardController : Controller
    {
        private readonly AppDbContext _context;

        public DashboardController(AppDbContext context)
        {
            _context = context;
        }

        [Route("Dashboard/Redirect/{userId}")]
        public IActionResult Redirect(int userId)
        {
            // Get the current user's role
            var currentUser = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (currentUser != null)
            {
                // Access the Tag through the navigation property
                var userTag = _context.Tag.FirstOrDefault(t => t.TagID == currentUser.TagID);
                if (userTag != null)
                {
                    if (userTag.Description == "Employee")
                    {
                        // Redirect to the EmployeeDashboardController's Index action
                        return RedirectToAction("Index", "EmployeeDashboard");
                    }
                    else if (userTag.Description == "Farmer")
                    {
                        // Redirect to the FarmerDashboardController's Index action
                        return RedirectToAction("Index", "FarmerDashboard");
                    }
                }
            }
            // Default to home page if user's role cannot be determined
            return RedirectToAction("Index", "Home");
        }
    }
}
