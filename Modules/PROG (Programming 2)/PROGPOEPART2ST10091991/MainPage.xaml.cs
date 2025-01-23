using Microsoft.Extensions.Logging;
using Microsoft.Maui.Controls;
using PROG7312ST10091991;
using PROGPOEPART1ST10091991;
using System;

namespace PROG7312ST10091991POEPART2
{
    public partial class MainPage : ContentPage
    {
        private readonly ILogger<MainPage> _logger;

        // Parameterless constructor for XAML to use
        public MainPage() : this(null) { }

        // Constructor with dependency injection
        public MainPage(ILogger<MainPage> logger)
        {
            InitializeComponent();
            _logger = logger;
            _logger?.LogInformation("MainPage initialized with logger.");
            ServiceRequestStatusButton.IsEnabled = true; // Ensure the button is enabled on initialization
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();

            // Animate the page content
            await AnimatePageContent();
        }

        private async Task AnimatePageContent()
        {
            // Fade in the whole page
            await this.FadeTo(1, 1000, Easing.CubicInOut);

            // Slide in the buttons with a delay between each
            await ReportIssuesButton.TranslateTo(0, 0, 500, Easing.SpringOut);
            await Task.Delay(100);
            await LocalEventsButton.TranslateTo(0, 0, 500, Easing.SpringOut);
            await Task.Delay(100);
            await ServiceRequestStatusButton.TranslateTo(0, 0, 500, Easing.SpringOut);
        }

        private async void OnReportIssuesClicked(object sender, EventArgs e)
        {
            // Bounce animation on button click
            await AnimateButton(ReportIssuesButton);
            // Add functionality for the button click
            await Navigation.PushAsync(new ReportIssuesPage());
        }

        private async void OnLocalEventsClicked(object sender, EventArgs e)
        {
            await AnimateButton(LocalEventsButton);
            await Navigation.PushAsync(new LocalEventsPage());
        }

        private async void OnServiceRequestStatusClicked(object sender, EventArgs e)
        {
            await AnimateButton(ServiceRequestStatusButton);
            await Navigation.PushAsync(new ServiceRequestStatusPage());
        }

        private async Task AnimateButton(Button button)
        {
            // Bounce animation
            await button.ScaleTo(1.2, 100, Easing.BounceIn);
            await button.ScaleTo(1, 100, Easing.BounceOut);
        }
    }
}
