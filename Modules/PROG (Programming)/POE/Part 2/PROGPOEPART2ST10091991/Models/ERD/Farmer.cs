using System.ComponentModel.DataAnnotations;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    public class Farmer
    {
        [Key]
        public int FarmerID { get; set; }

        [Required]
        public int UserID { get; set; }

        public User User { get; set; }
    }
}