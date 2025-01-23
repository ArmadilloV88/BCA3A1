using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Maui.Controls;

namespace PROG7312ST10091991POEPART2
{
    public partial class App : Application
    {
        private readonly ILogger<AppShell> _appShellLogger;
        private readonly IServiceProvider _serviceProvider;

        public App(ILogger<AppShell> appShellLogger, IServiceProvider serviceProvider)
        {
            InitializeComponent();
            _appShellLogger = appShellLogger;
            _serviceProvider = serviceProvider;

            // Create an instance of AppShell with the correct parameters
            MainPage = new AppShell(_appShellLogger, _serviceProvider);
        }
    }
}