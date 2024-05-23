namespace PROGPOEPART2ST10091991.Models.ERD
{
    public class Tag
    {
        public int TagID { get; set; }
        public string? Description { get; set; }
        public ICollection<User>? Users { get; set; }
    }

}
