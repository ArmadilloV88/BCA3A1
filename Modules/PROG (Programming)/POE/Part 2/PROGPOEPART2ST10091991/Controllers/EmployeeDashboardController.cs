using Microsoft.AspNetCore.Mvc;

namespace PROGPOEPART2ST10091991.Controllers
{
    [Route("EmployeeDashboard")]
    public class EmployeeDashboardController : Controller
    {
        [Route("Index")]
        public IActionResult Index()
        {
            // Logic for the employee dashboard
            return View();
        }

        [Route("MyProfile")]
        public IActionResult MyProfile()
        {
            return View();
        }

        [Route("Products")]
        public IActionResult Products()
        {
            return View();
        }

        [Route("Highlights")]
        public IActionResult Highlights()
        {
            return View();
        }

        [Route("Farmers")]
        public IActionResult Farmers()
        {
            return View();
        }
    }
}
