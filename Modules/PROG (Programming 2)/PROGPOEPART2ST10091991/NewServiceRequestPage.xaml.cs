using Microsoft.Maui.Controls;
using PROG7312ST10091991POEPART2;
using System;

namespace PROGPOEPART1ST10091991
{
    public partial class NewServiceRequestPage : ContentPage
    {
        private readonly ServiceRequestStatusPage _serviceRequestStatusPage;

        public NewServiceRequestPage(ServiceRequestStatusPage serviceRequestStatusPage)
        {
            InitializeComponent();
            _serviceRequestStatusPage = serviceRequestStatusPage; // Pass the reference of ServiceRequestStatusPage
        }

        private async void OnCreateRequestClicked(object sender, EventArgs e)
        {
            var category = CategoryPicker.SelectedItem?.ToString();
            var location = LocationPicker.SelectedItem?.ToString();
            var description = DescriptionEditor.Text;

            // Ensure all required fields are filled
            if (string.IsNullOrEmpty(category) || string.IsNullOrEmpty(location) || string.IsNullOrEmpty(description))
            {
                await DisplayAlert("Error", "All fields must be filled", "OK");
                return;
            }

            // Create a new service request
            var newRequest = new ServiceRequest
            {
                ReportDetails = new ServiceDetails
                {
                    Category = category,
                    Location = location,
                    Description = description,
                    EventDate = DateTime.Now // Set current date
                },
                Status = 1 // New request is always in status 1
            };

            // Add to GlobalStorage
            GlobalStorage.ServiceRequests.Add(newRequest);

            // Refresh the filtered list in ServiceRequestStatusPage
            _serviceRequestStatusPage.RefreshServiceRequests();

            // Navigate back to the ServiceRequestStatusPage
            await Navigation.PopAsync();
        }
    }

public class ServiceDetails
    {
        public string? Category { get; set; }
        public string? Location { get; set; }
        public string? Description { get; set; }
        public string? FilePath { get; set; }
        public bool IsImage { get; set; }
        public bool IsPdf { get; set; }
        public bool IsDoc { get; set; }
        public bool IsPdfWebViewVisible { get; set; }
        public string? PdfWebViewSource { get; set; }
        public DateTime EventDate { get; set; }
    }
}