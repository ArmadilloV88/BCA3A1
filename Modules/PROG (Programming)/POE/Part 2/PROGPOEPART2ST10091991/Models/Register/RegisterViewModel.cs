using System.ComponentModel.DataAnnotations;

namespace PROGPOEPART2ST10091991.Models.Register
{
    public class RegisterViewModel
    {
        [Required]
        [StringLength(50)]
        public string? Username { get; set; }

        [Required]
        [DataType(DataType.Password)]
        [StringLength(50)]
        public string? Password { get; set; }

        [Required]
        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "The password and confirmation password do not match.")]
        public string? ConfirmPassword { get; set; }

        [StringLength(50)]
        public string? Name { get; set; }

        [StringLength(50)]
        public string? Surname { get; set; }

        public int? Age { get; set; }

        [EmailAddress]
        [StringLength(100)]
        public string? Email { get; set; }

        [StringLength(10)]
        public string? Gender { get; set; }
    }
}