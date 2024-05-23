using Azure;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    public class User
    {
        public int UserID { get; set; }
        public string? Username { get; set; }
        public string? Password { get; set; }
        public int TagID { get; set; }
        public Tag Tag { get; set; }
    }

}
