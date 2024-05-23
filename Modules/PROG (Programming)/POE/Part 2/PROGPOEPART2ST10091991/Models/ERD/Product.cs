namespace PROGPOEPART2ST10091991.Models.ERD
{
    public class Product
    {
        public int ProductID { get; set; }
        public string? ProductName { get; set; }
        public string? ProductDescription { get; set; }
        public string? ProductCategory { get; set; }
        public DateTime ProductDate { get; set; }
        public int AddedByUserID { get; set; }
        public User AddedByUser { get; set; }
    }

}
