using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    [Table("EmployeeProduct")]
    public class EmployeeProduct
    {
        [Key]
        public int EmployeeProductID { get; set; }

        [ForeignKey("User")]
        public int EmployeeID { get; set; }
        public User Employee { get; set; }

        [ForeignKey("Product")]
        public int ProductID { get; set; }
        public Product Product { get; set; }
    }
}

