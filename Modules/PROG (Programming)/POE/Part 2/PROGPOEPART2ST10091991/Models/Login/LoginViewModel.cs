using System.ComponentModel.DataAnnotations;

namespace PROGPOEPART2ST10091991.Models.Login
{
    public class LoginViewModel
    {
        [Required]
        public string? Username { get; set; }

        [Required]
        [DataType(DataType.Password)]
        public string? Password { get; set; }
    }
}
