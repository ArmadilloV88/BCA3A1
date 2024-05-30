using System.ComponentModel.DataAnnotations.Schema;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    [Table("Farmer")]
    public class Farmer
    {
        public int FarmerID { get; set; }
        public int UserID { get; set; }
        public User User { get; set; }
    }

}
