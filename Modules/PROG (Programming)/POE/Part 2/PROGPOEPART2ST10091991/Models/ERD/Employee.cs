using System.ComponentModel.DataAnnotations.Schema;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    [Table("Employee")]
    public class Employee
    {
        public int EmployeeID { get; set; }
        public int UserID { get; set; }
        public User User { get; set; }
    }
}