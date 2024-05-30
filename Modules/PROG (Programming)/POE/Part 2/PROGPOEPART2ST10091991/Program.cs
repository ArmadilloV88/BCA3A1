using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using PROGPOEPART2ST10091991.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
    .AddCookie(options =>
    {
        options.Cookie.Name = "ORACookie";
    });

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

app.UseAuthentication();
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
    defaults: new { controller = "EmployeeDashboard", action = "Index" });

app.MapControllerRoute(
    name: "farmer-dashboard",
    pattern: "FarmerDashboard/{action=Index}/{id?}",
    defaults: new { controller = "FarmerDashboard", action = "Index" });

app.MapControllerRoute(
    name: "farmer-products",
    pattern: "FarmerDashboard/Products/{action=Products}/{id?}",
    defaults: new { controller = "FarmerDashboard", action = "Products" });

app.MapControllerRoute(
    name: "employee-products",
    pattern: "EmployeeDashboard/Products/{action=Products}/{id?}",
    defaults: new { controller = "EmployeeDashboard", action = "Products" });

app.Run();
