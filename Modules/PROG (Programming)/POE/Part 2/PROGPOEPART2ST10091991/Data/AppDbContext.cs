namespace PROGPOEPART2ST10091991.Data
{
    using Microsoft.EntityFrameworkCore;
    using PROGPOEPART2ST10091991.Models.ERD;

    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; }
        public DbSet<Tag> Tag { get; set; }
        public DbSet<Employee> Employees { get; set; }
        public DbSet<Farmer> Farmers { get; set; }
        public DbSet<Product> Products { get; set; }
        public DbSet<NewsFeed> NewsFeeds { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Tag>().HasData(
                new Tag { TagID = 1, Description = "Employee" },
                new Tag { TagID = 2, Description = "Farmer" }
            );
        }
    }

}
