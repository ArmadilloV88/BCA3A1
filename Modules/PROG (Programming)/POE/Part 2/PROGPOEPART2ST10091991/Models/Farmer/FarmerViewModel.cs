namespace PROGPOEPART2ST10091991.Models.Farmer
{
    public class FarmerViewModel
    {
        public int FarmerID { get; set; }
        public int UserID { get; set; } // Add UserID property
        public string? Name { get; set; }
        public string? Surname { get; set; }
    }
}