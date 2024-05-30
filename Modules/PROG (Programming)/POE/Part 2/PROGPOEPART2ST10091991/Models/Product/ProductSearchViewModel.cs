namespace PROGPOEPART2ST10091991.Models.Product
{
    public class ProductSearchViewModel
    {
        public string? SearchTerm { get; set; }
        public char? AlphabetFilter { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public int? FarmerID { get; set; }
        public IEnumerable<PROGPOEPART2ST10091991.Models.ERD.Product>? Results { get; set; }
    }
}

