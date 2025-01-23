using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection; // Ensure this namespace is included
using System.Collections.Generic;
using System;
using SkiaSharp;
using System.IO;
using System.Linq; // Ensure LINQ is included

namespace PROG7312ST10091991POEPART2
{
    public class IssueReportService
    {
        private static readonly IssueReportService _instance = new IssueReportService();
        private readonly ILogger<IssueReportService> _logger;
        private List<IssueReport> _issueReports = new List<IssueReport>();

        public static IssueReportService Instance => _instance;

        private IssueReportService()
        {
            // Configure logging
            var loggerFactory = LoggerFactory.Create(builder =>
            {
                builder.AddConsole(); // Ensure the Microsoft.Extensions.Logging.Console package is installed
            });
            _logger = loggerFactory.CreateLogger<IssueReportService>();
        }

        public List<IssueReport> GetIssueReports()
        {
            _logger.LogInformation("Fetching all issue reports.");
            if (_issueReports.Count == 0)
            {
                _logger.LogInformation("No issue reports found.");
            }
            return _issueReports;
        }

        public void AddIssueReport(IssueReport issueReport)
        {
            if (issueReport == null)
            {
                _logger.LogWarning("Attempted to add a null issue report.");
                return;
            }

            _issueReports.Add(issueReport);
            _logger.LogInformation("New issue added. Location: {Location}, Category: {Category}, File: {File}, Description: {Description}",
                issueReport.Location, issueReport.Category, issueReport.FilePath, issueReport.Description);
        }
    }

    /*public class IssueReport
    {
        public string Category { get; set; }
        public string Location { get; set; }
        public string Description { get; set; }
        public string FilePath { get; set; }
        public bool IsImage { get; set; }
        public bool IsPdf { get; set; }
        public bool IsDoc { get; set; }
        public double ImageHeight { get; set; }
        public double ImageWidth { get; set; }

        public IssueReport(string category, string location, string description, string filePath)
        {
            Category = category;
            Location = location;
            Description = description;
            FilePath = filePath;

            IsImage = CheckIfImage(filePath);
            IsPdf = CheckIfPdf(filePath);
            IsDoc = CheckIfDoc(filePath);
            (ImageHeight, ImageWidth) = GetImageDimensions(filePath);
        }

        private bool CheckIfImage(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return false;
            var extensions = new[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
            return extensions.Contains(Path.GetExtension(filePath).ToLower());
        }

        private bool CheckIfPdf(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return false;
            return Path.GetExtension(filePath).Equals(".pdf", StringComparison.OrdinalIgnoreCase);
        }

        private bool CheckIfDoc(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return false;
            var extensions = new[] { ".doc", ".docx" };
            return extensions.Contains(Path.GetExtension(filePath).ToLower());
        }

        private (double Height, double Width) GetImageDimensions(string filePath)
        {
            if (string.IsNullOrEmpty(filePath)) return (0, 0);

            try
            {
                using (var stream = File.OpenRead(filePath))
                {
                    using (var bitmap = SKBitmap.Decode(stream))
                    {
                        if (bitmap == null)
                        {
                            // Log a warning if the bitmap could not be decoded
                            LogBitmapDecodeFailure(filePath);
                            return (0, 0);
                        }
                        return (bitmap.Height, bitmap.Width);
                    }
                }
            }
            catch (FileNotFoundException ex)
            {
                // Log error if the file is not found
                LogFileNotFound(filePath, ex);
                return (0, 0);
            }
            catch (Exception ex)
            {
                // Log general errors
                LogGeneralError(filePath, ex);
                return (0, 0);
            }
        }

        private void LogBitmapDecodeFailure(string filePath)
        {
            // You can inject ILogger into this class to log the warning
            Console.WriteLine($"Warning: Unable to decode the image at {filePath}. Returning default dimensions.");
        }

        private void LogFileNotFound(string filePath, Exception ex)
        {
            Console.WriteLine($"Error: File not found - {filePath}. Exception: {ex.Message}");
        }

        private void LogGeneralError(string filePath, Exception ex)
        {
            Console.WriteLine($"Error: An error occurred while getting image dimensions for {filePath}. Exception: {ex.Message}");
        }
    }*/
}