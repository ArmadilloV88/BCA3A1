namespace PROGPOEPART2ST10091991.Models.ERD
{
    public class Favorite
    {
        public int FavoriteID { get; set; }
        public int EmployeeID { get; set; }
        public int FarmerID { get; set; }

        // Navigation properties
        public User Employee { get; set; }
        public User Farmer { get; set; }
    }
}
