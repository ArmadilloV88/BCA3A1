using Microsoft.Extensions.Logging;
using PROGPOEPART1ST10091991;
using PROG7312ST10091991;

namespace PROG7312ST10091991POEPART2
{
    public partial class AppShell : Shell
    {
        private readonly ILogger<AppShell> _logger;
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<ViewQueriesPage> _viewQueriesPageLogger;

        public AppShell(ILogger<AppShell> logger, IServiceProvider serviceProvider)
        {
            InitializeComponent();
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _serviceProvider = serviceProvider ?? throw new ArgumentNullException(nameof(serviceProvider));
        }
        private async void OnReportIssuesClicked(object sender, EventArgs e)
        {
            try
            {
                var reportIssuesPage = _serviceProvider.GetRequiredService<ReportIssuesPage>();
                await Navigation.PushAsync(reportIssuesPage);
                _logger.LogInformation("Navigated to ReportIssuesPage.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error navigating to ReportIssuesPage.");
            }
        }
        private async void OnViewQueriesClicked(object sender, EventArgs e)
        {
            try
            {
                // Ensure _viewQueriesPageLogger is of type ILogger<ViewQueriesPage>
                var viewQueriesPage = new ViewQueriesPage(_viewQueriesPageLogger);
                if (Navigation != null)
                {
                    await Navigation.PushAsync(viewQueriesPage);
                    _logger?.LogInformation("Navigated to ViewQueriesPage.");
                }
                else
                {
                    _logger?.LogWarning("Navigation is null. Ensure MainPage is in a NavigationPage.");
                }
            }
            catch (Exception ex)
            {
                _logger?.LogError(ex, "Error navigating to ViewQueriesPage.");
            }
        }
    }
}