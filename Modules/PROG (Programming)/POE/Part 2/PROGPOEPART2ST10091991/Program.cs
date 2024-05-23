using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using PROGPOEPART2ST10091991.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddControllersWithViews();

var app = builder.Build();

// Check database connection
using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    var dbContext = services.GetRequiredService<AppDbContext>();
    var logger = services.GetRequiredService<ILogger<Program>>();
    if (!dbContext.Database.CanConnect())
    {
        logger.LogError("Database connection is not valid. Please check your connection string.");
        return;
    }
    logger.LogInformation("Database is live and well");
}

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseRouting();

app.UseAuthorization();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.MapControllerRoute(
    name: "login",
    pattern: "{controller=Login}/{action=Index}/{id?}");

app.MapControllerRoute(
    name: "register",
    pattern: "{controller=Register}/{action=Index}/{id?}");

app.MapControllerRoute(
    name: "employee-dashboard",
    pattern: "EmployeeDashboard/{action=Index}/{id?}",
    defaults: new { controller = "EmployeeDashboardController", action = "Index" });

app.MapControllerRoute(
    name: "farmer-dashboard",
    pattern: "FarmerDashboard/{action=Index}/{id?}",
    defaults: new { controller = "FarmerDashboardController", action = "Index" });

app.MapControllerRoute(
    name: "dashboard-redirect",
    pattern: "Dashboard/{action}/{id?}",
    defaults: new { controller = "Dashboard", action = "Redirect" });

app.Run();
