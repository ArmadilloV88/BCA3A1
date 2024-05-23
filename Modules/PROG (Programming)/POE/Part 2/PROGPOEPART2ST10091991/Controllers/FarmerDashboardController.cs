using Microsoft.AspNetCore.Mvc;

namespace PROGPOEPART2ST10091991.Controllers
{
    [Route("FarmerDashboard")] // Base route for the controller
    public class FarmerDashboardController : Controller
    {
        [Route("Index")] // Route for the Index action
        public IActionResult Index()
        {
            // Logic for the dashboard landing page
            return View();
        }
        [Route("MyProfile")] // Route for the MyProfile action
        public IActionResult MyProfile()
        {
            return View();
        }

        [Route("MyProducts")] // Route for the MyProducts action
        public IActionResult MyProducts()
        {
            return View();
        }

        [Route("Products")] // Route for the Products action
        public IActionResult Products()
        {
            return View();
        }

        [Route("Highlights")] // Route for the Highlights action
        public IActionResult Highlights()
        {
            return View();
        }
    }
}
