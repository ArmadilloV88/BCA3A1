using Microsoft.AspNetCore.Mvc;
using PROGPOEPART2ST10091991.Models;
using System.Diagnostics;

namespace PROGPOEPART2ST10091991.Controllers
{
    public class HomeController : Controller
    {
        public IActionResult Index()
        {
            ViewData["Title"] = "Welcome Page";
            ViewData["WelcomeMessage"] = "ORA";
            return View();
        }

        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}