using Microsoft.Extensions.Logging;
using Microsoft.Maui.Controls;
using Microsoft.Maui.Graphics;
using PROG7312ST10091991POEPART2;

namespace PROG7312ST10091991
{
    public partial class ReportIssuesPage : ContentPage
    {
        private readonly ILogger<ReportIssuesPage> _logger;
        private string _selectedFilePath;
        private double _progressPercentage;

        public ReportIssuesPage() : this(null) { }

        public ReportIssuesPage(ILogger<ReportIssuesPage> logger)
        {
            InitializeComponent();
            _logger = logger;
            _logger?.LogInformation("ReportIssuesPage initialized with logger.");
        }

        private async void OnAttachImageClicked(object sender, EventArgs e)
        {
            // Check if both location and category are selected
            if (string.IsNullOrEmpty(locationPicker.SelectedItem?.ToString()))
            {
                await DisplayAlert("Error", "Please select a location before attaching an image.", "OK");
                return;
            }

            if (string.IsNullOrEmpty(categoryPicker.SelectedItem?.ToString()))
            {
                await DisplayAlert("Error", "Please select a category before attaching an image.", "OK");
                return;
            }

            try
            {
                var result = await FilePicker.Default.PickAsync(new PickOptions
                {
                    PickerTitle = "Select a file",
                    FileTypes = new FilePickerFileType(
                        new Dictionary<DevicePlatform, IEnumerable<string>>
                        {
                            { DevicePlatform.Android, new[] { ".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx" } },
                            { DevicePlatform.iOS, new[] { ".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx" } },
                            { DevicePlatform.WinUI, new[] { ".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx" } },
                            { DevicePlatform.macOS, new[] { ".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx" } }
                        })
                });

                if (result != null)
                {
                    _selectedFilePath = result.FullPath;
                    await DisplayAlert("File Selected", $"You selected {result.FileName}", "OK");
                    UpdateProgress();

                    // Change the border color to green when a file is selected
                    attachImageFrame.BorderColor = Colors.Green;
                }
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", ex.Message, "OK");
                _logger?.LogError(ex, "Error selecting file.");
            }
        }

        private void UpdateProgress()
        {
            _progressPercentage = 0;

            // Update Location Label
            if (!string.IsNullOrEmpty(locationPicker.SelectedItem?.ToString()))
            {
                _progressPercentage += 0.25;
                locationLabel.TextColor = Colors.Green; // Change label color to green if a location is selected
            }
            else
            {
                locationLabel.TextColor = Colors.White; // Reset label color to default
            }

            // Update Category Label
            if (!string.IsNullOrEmpty(categoryPicker.SelectedItem?.ToString()))
            {
                _progressPercentage += 0.25;
                categoryLabel.TextColor = Colors.Green; // Change label color to green if a category is selected
            }
            else
            {
                categoryLabel.TextColor = Colors.White; // Reset label color to default
            }

            // Update Description Label
            if (!string.IsNullOrEmpty(descriptionEditor.Text))
            {
                _progressPercentage += 0.25;
                descriptionLabel.TextColor = Colors.Green; // Change label color to green if there is text
            }
            else
            {
                descriptionLabel.TextColor = Colors.White; // Reset label color to default
            }

            // Check for file selection
            if (!string.IsNullOrEmpty(_selectedFilePath))
            {
                _progressPercentage += 0.25;
            }

            // Update ProgressBar and Button Colors
            progressBar.Progress = _progressPercentage;
            UpdateProgressBarAndButtonColors();

            progressPercentageLabel.Text = (_progressPercentage * 100).ToString("F0") + "%";

            // Enable or disable Attach Image button based on selected items
            attachImageButton.IsEnabled = !string.IsNullOrEmpty(locationPicker.SelectedItem?.ToString()) &&
                                          !string.IsNullOrEmpty(categoryPicker.SelectedItem?.ToString());
        }

        private void UpdateProgressBarAndButtonColors()
        {
            if (_progressPercentage < 0.5)
            {
                progressBar.ProgressColor = Colors.Red; // Color for filled portion
                submitButton.BackgroundColor = Colors.Red;
                attachImageFrame.BorderColor = Colors.Red; // Keep red
            }
            else if (_progressPercentage < 0.75)
            {
                progressBar.ProgressColor = Color.FromRgb(255, 165, 0); // Orange
                submitButton.BackgroundColor = Color.FromRgb(255, 165, 0); // Orange
                attachImageFrame.BorderColor = Colors.Red; // Keep red
            }
            else if (_progressPercentage < 1.0)
            {
                progressBar.ProgressColor = Color.FromRgb(144, 238, 144); // Light Green
                submitButton.BackgroundColor = Color.FromRgb(144, 238, 144); // Light Green
                attachImageFrame.BorderColor = Colors.Red; // Keep red
            }
            else
            {
                progressBar.ProgressColor = Color.FromRgb(0, 100, 0); // Dark Green
                submitButton.BackgroundColor = Color.FromRgb(0, 100, 0); // Dark Green
                attachImageFrame.BorderColor = Colors.Green; // Change border to green
                submitButton.IsEnabled = true; // Enable submit button when progress is complete
            }
        }

        private void OnFieldChanged(object sender, EventArgs e)
        {
            UpdateProgress();
        }

        private async void OnSubmitClicked(object sender, EventArgs e)
        {
            var location = locationPicker.SelectedItem?.ToString();
            var category = categoryPicker.SelectedItem?.ToString();
            var description = descriptionEditor.Text;

            if (string.IsNullOrEmpty(location) || string.IsNullOrEmpty(category) || string.IsNullOrEmpty(description))
            {
                await DisplayAlert("Error", "Please fill in all fields.", "OK");
                return;
            }

            var newReport = new IssueReport(category, location, description, _selectedFilePath);
            GlobalStorage.IssueReports.Add(newReport);

            // Reset form
            ClearForm();
            await DisplayAlert("Success", "Issue reported successfully.", "OK");
        }

        private void ClearForm()
        {
            locationPicker.SelectedIndex = -1;
            categoryPicker.SelectedIndex = -1;
            descriptionEditor.Text = string.Empty;
            _selectedFilePath = null;

            // Reset colors and progress
            locationLabel.TextColor = Colors.White;
            categoryLabel.TextColor = Colors.White;
            descriptionLabel.TextColor = Colors.White;
            attachImageFrame.BorderColor = Colors.Red; // Reset to original color

            UpdateProgress();
        }

        private async void OnBackToMainMenuClicked(object sender, EventArgs e)
        {
            await Navigation.PopAsync();
        }
    }
}