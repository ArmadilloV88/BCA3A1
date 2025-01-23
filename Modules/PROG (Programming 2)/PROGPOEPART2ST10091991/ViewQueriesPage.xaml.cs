using Microsoft.Extensions.Logging;
using Microsoft.Maui.Controls;
using System;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Maui.ApplicationModel; // For file opening

namespace PROG7312ST10091991POEPART2
{
    public partial class ViewQueriesPage : ContentPage
    {
        private readonly ILogger<ViewQueriesPage> _logger;
        private bool _docxWarningDisplayed = false; // Flag to track if the warning has been displayed

        public ViewQueriesPage() : this(null) { }

        public ViewQueriesPage(ILogger<ViewQueriesPage> logger)
        {
            InitializeComponent();
            _logger = logger;
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
            InitializeData();
        }

        private async void InitializeData()
        {
            try
            {
                var issueReports = GlobalStorage.IssueReports;

                // Recommended Events - filter based on most searched terms (if needed)
                var recommendedEvents = issueReports
                    .Where(report => report.EventDate <= DateTime.Now.AddDays(10)) // Events within the next 10 days
                    .OrderBy(report => report.EventDate)
                    .ToList();

                // Combine recommended events
                var combinedEvents = new ObservableCollection<IssueReport>(recommendedEvents);

                if (combinedEvents != null && combinedEvents.Count > 0)
                {
                    QueriesCollectionViewControl.ItemsSource = combinedEvents;
                    QueriesCollectionViewControl.IsVisible = true;
                    NoQueriesLabelControl.IsVisible = false;
                }
                else
                {
                    QueriesCollectionViewControl.ItemsSource = null;
                    QueriesCollectionViewControl.IsVisible = false;
                    NoQueriesLabelControl.IsVisible = true;
                }
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", $"Error retrieving events: {ex.Message}", "OK");
            }
        }

        private async Task ShowDocxWarning()
        {
            if (!_docxWarningDisplayed) // Check if warning has already been shown
            {
                await DisplayAlert("Preview Warning", "Cannot preview, please click on the file path to open via Word.", "OK");
                _docxWarningDisplayed = true; // Set the flag to true to avoid showing it again
            }
        }

        private void OpenDocxFile(object sender, TappedEventArgs e)
        {
            var docxFilePath = e.Parameter as string; // Get the file path from the TapGestureRecognizer
            if (!string.IsNullOrEmpty(docxFilePath))
            {
                OpenDocxFile(docxFilePath); // Call the existing method to open the file
            }
        }

        private void OpenDocxFile(string docxFilePath)
        {
            try
            {
                System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo
                {
                    FileName = docxFilePath,
                    UseShellExecute = true // Open the file with the default application (Word)
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error opening DOCX file");
                DisplayAlert("Error", $"Could not open the DOCX file: {ex.Message}", "OK");
            }
        }

        private bool CheckIfImage(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return false;
            var extensions = new[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
            return extensions.Contains(Path.GetExtension(filePath).ToLower());
        }

        private bool CheckIfPdf(string filePath)
        {
            return filePath != null && Path.GetExtension(filePath).Equals(".pdf", StringComparison.OrdinalIgnoreCase);
        }

        private bool CheckIfDoc(string filePath)
        {
            var extensions = new[] { ".doc", ".docx" };
            return filePath != null && extensions.Contains(Path.GetExtension(filePath).ToLower());
        }
    }

    // Model class to represent IssueReport
    public class IssueReport
    {
        public string Category { get; set; }
        public string Location { get; set; }
        public string Description { get; set; }
        public string FilePath { get; set; }
        public bool IsImage { get; set; }
        public bool IsPdf { get; set; }
        public bool IsDoc { get; set; }
        public bool IsPdfWebViewVisible { get; set; }
        public string PdfWebViewSource { get; set; }
        public DateTime EventDate { get; set; } // Event Date property

        public IssueReport(string category, string location, string description, string filePath)
        {
            Category = category;
            Location = location;
            Description = description;
            FilePath = filePath;

            // Determine file type flags based on FilePath
            IsImage = CheckIfImage(filePath);
            IsPdf = CheckIfPdf(filePath);
            IsDoc = CheckIfDoc(filePath);

            // If it's a PDF, set up the WebView source
            if (IsPdf)
            {
                IsPdfWebViewVisible = true; // Correct the property name here
                PdfWebViewSource = filePath; // Assuming this is the path to the PDF
            }
            else
            {
                IsPdfWebViewVisible = false; // Hide WebView for non-PDF files
            }
        }

        private bool CheckIfImage(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return false;
            var extensions = new[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
            return extensions.Contains(Path.GetExtension(filePath).ToLower());
        }

        private bool CheckIfPdf(string filePath)
        {
            return filePath != null && Path.GetExtension(filePath).Equals(".pdf", StringComparison.OrdinalIgnoreCase);
        }

        private bool CheckIfDoc(string filePath)
        {
            var extensions = new[] { ".doc", ".docx" };
            return filePath != null && extensions.Contains(Path.GetExtension(filePath).ToLower());
        }
    }
}