namespace PROGPOEPART2ST10091991.Data
{
    using Microsoft.EntityFrameworkCore;
    using PROGPOEPART2ST10091991.Models.ERD;

    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; }
        public DbSet<Tag> Tag { get; set; }
        public DbSet<Employee> Employee { get; set; }
        public DbSet<Farmer> Farmers { get; set; }
        public DbSet<Product> Products { get; set; }
        public DbSet<NewsFeed> NewsFeeds { get; set; }
        public DbSet<Favorite> Favorites { get; set; }
        public DbSet<EmployeeProduct> EmployeeProducts { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>().ToTable("Users");
            modelBuilder.Entity<Tag>().ToTable("Tag");
            modelBuilder.Entity<Employee>().ToTable("Employee");
            modelBuilder.Entity<Farmer>().ToTable("Farmers");
            modelBuilder.Entity<Product>().ToTable("Products");
            modelBuilder.Entity<NewsFeed>().ToTable("NewsFeed");
            modelBuilder.Entity<Favorite>().ToTable("Favorites");
            modelBuilder.Entity<EmployeeProduct>().ToTable("EmployeeProduct");

            modelBuilder.Entity<Tag>().HasData(
                new Tag { TagID = 1, Description = "Employee" },
                new Tag { TagID = 2, Description = "Farmer" }
            );
        }
    }
}
