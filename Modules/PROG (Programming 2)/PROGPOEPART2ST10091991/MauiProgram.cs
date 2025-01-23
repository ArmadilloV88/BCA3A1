/* Code Attributions
 ******************* 
1. Microsoft.Maui.Controls
    - Description: Provides UI controls and layouts for building cross-platform applications using .NET MAUI.
    - Version: 6.0.400 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Microsoft.Maui.Controls](https://www.nuget.org/packages/Microsoft.Maui.Controls/)

2. Microsoft.Maui.Essentials
    - Description: Provides APIs for accessing device features like sensors, connectivity, and file storage.
    - Version: 6.0.400 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Microsoft.Maui.Essentials](https://www.nuget.org/packages/Microsoft.Maui.Essentials/)

3. Microsoft.Maui.Controls.Compatibility
    - Description: Provides backward compatibility for legacy Xamarin.Forms controls and APIs.
    - Version: 6.0.400 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Microsoft.Maui.Controls.Compatibility](https://www.nuget.org/packages/Microsoft.Maui.Controls.Compatibility/)

4. Blazorise
    - Description: A UI component library for Blazor, offering components and themes to enhance Blazor applications.
    - Version: 0.9.7 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Blazorise](https://www.nuget.org/packages/Blazorise/)

5. SkiaSharp
    - Description: A cross-platform 2D graphics library for .NET based on Google's Skia Graphics Engine.
    - Version: 2.88.0 (Latest as of 2024)
    - Released: 2014
    - NuGet Package: [SkiaSharp](https://www.nuget.org/packages/SkiaSharp/)

6. Polly
    - Description: Provides resilience and transient-fault-handling capabilities, such as retry and circuit breaker policies.
    - Version: 7.2.4 (Latest as of 2024)
    - Released: 2014
    - NuGet Package: [Polly](https://www.nuget.org/packages/Polly/)

7. Serilog
    - Description: A diagnostic logging library with structured logging support.
    - Version: 2.10.0 (Latest as of 2024)
    - Released: 2016
    - NuGet Package: [Serilog](https://www.nuget.org/packages/Serilog/)

8. Dapper
    - Description: A simple object mapper for .NET, useful for querying relational databases.
    - Version: 2.0.123 (Latest as of 2024)
    - Released: 2011 (Continuously updated)
    - NuGet Package: [Dapper](https://www.nuget.org/packages/Dapper/)

9. AutoMapper
    - Description: A library for object-to-object mapping, which simplifies data transformation.
    - Version: 12.0.0 (Latest as of 2024)
    - Released: 2014
    - NuGet Package: [AutoMapper](https://www.nuget.org/packages/AutoMapper/)

10. NUnit
    - Description: A widely used unit-testing framework for .NET applications.
    - Version: 3.13.2 (Latest as of 2024)
    - Released: 2016
    - NuGet Package: [NUnit](https://www.nuget.org/packages/NUnit/)

11. BlazorWebView
    - Description: A .NET MAUI control for hosting Blazor content in a native application.
    - Version: 6.0.400 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Microsoft.Maui.Controls.Compatibility](https://www.nuget.org/packages/Microsoft.Maui.Controls.Compatibility/)

12. Microsoft.AspNetCore.Components.WebView.Maui
    - Description: Provides WebView support for Blazor in .NET MAUI applications.
    - Version: 6.0.1 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Microsoft.AspNetCore.Components.WebView.Maui](https://www.nuget.org/packages/Microsoft.AspNetCore.Components.WebView.Maui/)

13. Maui.CommunityToolkit
    - Description: A collection of .NET MAUI controls, converters, and helpers designed to streamline development.
    - Version: 1.2.0 (Latest as of 2024)
    - Released: 2021
    - NuGet Package: [Maui.CommunityToolkit](https://www.nuget.org/packages/Maui.CommunityToolkit/)

14. Xamarin.Forms.PancakeView
    - Description: A cross-platform library for creating customizable and performant UI elements with rounded corners, shadows, and more.
    - Version: 2.0.0 (Latest as of 2024)
    - Released: 2019
    - NuGet Package: [Xamarin.Forms.PancakeView](https://www.nuget.org/packages/Xamarin.Forms.PancakeView/)

15. LiveSharp
    - Description: A tool that allows for live editing of .NET MAUI applications.
    - Version: 2.2.0 (Latest as of 2024)
    - Released: 2020
    - NuGet Package: [LiveSharp](https://www.nuget.org/packages/LiveSharp/)
      
*/
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Maui;
using Microsoft.Maui.Controls.Hosting;
using Microsoft.Maui.Hosting;
using PROG7312ST10091991;
using PROG7312ST10091991POEPART2;

namespace PROGPOEPART1ST10091991
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var builder = MauiApp.CreateBuilder();
            builder
                .UseMauiApp<App>()
                .ConfigureFonts(fonts =>
                {
                    fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                });

            builder.Services.AddMauiBlazorWebView();
            builder.Services.AddTransient<ReportIssuesPage>();
            builder.Services.AddTransient<ViewQueriesPage>();


            builder.Logging
                .AddDebug() // Logs to the debug output window
                .AddConsole() // Logs to the console (if applicable)
                .SetMinimumLevel(LogLevel.Debug); // Ensure the log level is set correctly

            return builder.Build();
        }
    }
}