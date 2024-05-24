using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Data;
using System.Linq;
using Microsoft.Extensions.Logging;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class DashboardController : Controller
    {
        private readonly AppDbContext _context;
        private readonly ILogger<DashboardController> _logger;

        public DashboardController(AppDbContext context, ILogger<DashboardController> logger)
        {
            _context = context;
            _logger = logger;
        }

        [Route("Dashboard/Redirect/{userId}")]
        public IActionResult Redirect(int userId)
        {
            _logger.LogInformation($"Redirect action called with userId: {userId}");
            var currentUser = _context.Users.FirstOrDefault(u => u.UserID == userId);
            if (currentUser != null)
            {
                var userTag = _context.Tag.FirstOrDefault(t => t.TagID == currentUser.TagID);
                if (userTag != null)
                {
                    if (userTag.Description == "Employee")
                    {
                        _logger.LogInformation($"Redirecting to EmployeeDashboard for userId: {userId}");
                        return RedirectToAction("Index", "EmployeeDashboard", new { userId = currentUser.UserID });
                    }
                    else if (userTag.Description == "Farmer")
                    {
                        _logger.LogInformation($"Redirecting to FarmerDashboard for userId: {userId}");
                        return RedirectToAction("Index", "FarmerDashboard", new { userId = currentUser.UserID });
                    }
                }
            }
            _logger.LogInformation($"Unable to determine role for userId: {userId}");
            return RedirectToAction("Index", "Home");
        }
    }
}