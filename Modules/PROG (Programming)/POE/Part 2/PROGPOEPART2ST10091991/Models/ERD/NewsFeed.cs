using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PROGPOEPART2ST10091991.Models.ERD
{
    [Table("NewsFeed")]
    public class NewsFeed
    {
        [Key]
        public int NewsFeedID { get; set; }
        public string? NewsFeedName { get; set; }
        public DateTime NewsFeedDate { get; set; }
        public DateTime NewsFeedDateExp { get; set; }
    }
}
