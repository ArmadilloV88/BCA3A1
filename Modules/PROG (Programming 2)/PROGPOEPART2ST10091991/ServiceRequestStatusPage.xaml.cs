using System;
using System.Collections.ObjectModel;
using System.Linq;
using Microsoft.Maui.Controls;
using System.ComponentModel;
using PROG7312ST10091991POEPART2;
using PROGPOEPART1ST10091991.Services;

namespace PROGPOEPART1ST10091991
{
    public partial class ServiceRequestStatusPage : ContentPage, INotifyPropertyChanged
    {
        private string _selectedFilter;
        public ObservableCollection<string> FilterOptions { get; set; } = new ObservableCollection<string>
        {
            "Date",
            "Category",
            "Location"
        };

        public string SelectedFilter
        {
            get => _selectedFilter;
            set
            {
                if (_selectedFilter != value)
                {
                    _selectedFilter = value;
                    OnPropertyChanged();
                    // Reset the search bar visibility when filter changes
                    SearchBar.IsVisible = !string.IsNullOrEmpty(_selectedFilter);
                }
            }
        }

        public ObservableCollection<ServiceRequest> FilteredServiceRequests { get; set; } = new ObservableCollection<ServiceRequest>();

        private AVLTreeServiceRequest categoryTree = new AVLTreeServiceRequest();
        private ServiceRequestGraph requestGraph = new ServiceRequestGraph();
        private ServiceRequestMinHeap priorityQueue = new ServiceRequestMinHeap();

        public ServiceRequestStatusPage()
        {
            InitializeComponent();
            BindingContext = this;  // Ensure BindingContext is set to this (the current page)

            // Predefined service requests (mock or initial data)
            var predefinedRequests = new ObservableCollection<ServiceRequest>
            {
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Broken pipe in kitchen",
                        EventDate = new DateTime(2024, 10, 5),
                        Category = "Plumbing",
                        Location = "Kitchen"
                    },
                    Status = 1
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "AC not cooling properly",
                        EventDate = new DateTime(2024, 11, 1),
                        Category = "HVAC",
                        Location = "Living Room"
                    },
                    Status = 2
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Leaking faucet in bathroom",
                        EventDate = new DateTime(2024, 10, 20),
                        Category = "Plumbing",
                        Location = "Bathroom"
                    },
                    Status = 3
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "I love PROG7312",
                        EventDate = new DateTime(2024, 12, 20),
                        Category = "Utilities",
                        Location = "Limpopo"
                    },
                    Status = 4
                }
            };

            // Initialize FilteredServiceRequests to ensure binding works
            FilteredServiceRequests = new ObservableCollection<ServiceRequest>(predefinedRequests);

            // You can also add this list to GlobalStorage.ServiceRequests if needed
            GlobalStorage.ServiceRequests = new ObservableCollection<ServiceRequest>(predefinedRequests);
        }

        private void OnCreateServiceRequestClicked(object sender, EventArgs e)
        {
            // Pass the reference of ServiceRequestStatusPage to NewServiceRequestPage
            var newRequestPage = new NewServiceRequestPage(this);
            Navigation.PushAsync(newRequestPage);
        }


        private void OnFilterSelected(object sender, EventArgs e)
        {
            if (FilterPicker.SelectedIndex == -1) return;

            // Show the search bar when a filter option is selected
            SearchBar.IsVisible = true;
            SearchBar.Text = string.Empty;

            switch (FilterPicker.SelectedItem.ToString())
            {
                case "Date":
                    SearchBar.Placeholder = "Enter date (YYYY-MM-DD)";
                    break;
                case "Category":
                    SearchBar.Placeholder = "Enter category";
                    break;
                case "Location":
                    SearchBar.Placeholder = "Enter location";
                    break;
            }
        }

        private void OnSearchTextChanged(object sender, TextChangedEventArgs e)
        {
            string searchText = e.NewTextValue;

            // Ensure the search filters apply when there's search text
            if (string.IsNullOrEmpty(searchText))
            {
                // Reset to full list if search bar is cleared
                FilteredServiceRequests.Clear();
                foreach (var request in GlobalStorage.ServiceRequests.OrderBy(r => r.Status))
                {
                    FilteredServiceRequests.Add(request);
                }
            }
            else
            {
                FilterResults(SelectedFilter, searchText);
            }
        }

        private void FilterResults(string filterType, string searchText)
        {
            var filteredRequests = GlobalStorage.ServiceRequests.AsEnumerable(); // Start with all requests

            // Apply filter based on selected option
            switch (filterType)
            {
                case "Date":
                    // If searchText is just a year (e.g., "2024"), filter by year
                    if (searchText.Length == 4 && int.TryParse(searchText, out int year))
                    {
                        filteredRequests = filteredRequests.Where(request =>
                            request.ReportDetails?.EventDate.Year == year); // Match by year
                    }
                    // If searchText is in "YYYY-MM" format (e.g., "2024-10"), filter by year and month
                    else if (searchText.Length == 7 && DateTime.TryParseExact(searchText, "yyyy-MM", null, System.Globalization.DateTimeStyles.None, out DateTime monthSearchDate))
                    {
                        filteredRequests = filteredRequests.Where(request =>
                            request.ReportDetails?.EventDate.Year == monthSearchDate.Year &&
                            request.ReportDetails?.EventDate.Month == monthSearchDate.Month); // Match by year and month
                    }
                    // If searchText is a full date (e.g., "2024-10-05"), try to parse and match exactly
                    else if (DateTime.TryParse(searchText, out DateTime date))
                    {
                        filteredRequests = filteredRequests.Where(request =>
                            request.ReportDetails?.EventDate.Date == date.Date); // Compare only the date part
                    }
                    else
                    {
                        // If invalid date format, reset or show no results
                        filteredRequests = Enumerable.Empty<ServiceRequest>();
                    }
                    break;

                case "Category":
                    // Ensure case-insensitive and partial match for category
                    filteredRequests = filteredRequests.Where(request =>
                        request.ReportDetails?.Category?.Contains(searchText, StringComparison.OrdinalIgnoreCase) == true);
                    break;

                case "Location":
                    // Ensure case-insensitive and partial match for location
                    filteredRequests = filteredRequests.Where(request =>
                        request.ReportDetails?.Location?.Contains(searchText, StringComparison.OrdinalIgnoreCase) == true);
                    break;
            }

            // Order filtered results by status first, then by selected filter
            filteredRequests = filteredRequests
                .OrderBy(r => r.Status)  // Order by status (1-4)
                .ThenBy(r => r.ReportDetails?.EventDate) // Then order by EventDate if Date is the filter
                .ThenBy(r => r.ReportDetails?.Category); // Then order by Category if Category is the filter

            // Clear and update ObservableCollection
            FilteredServiceRequests.Clear();
            foreach (var request in filteredRequests)
            {
                FilteredServiceRequests.Add(request);
            }
        }

        // Implement the INotifyPropertyChanged interface to notify the UI of property changes
        public event PropertyChangedEventHandler PropertyChanged;
        protected virtual void OnPropertyChanged(string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        // Method to force refresh after creating a new service request
        public void RefreshServiceRequests()
        {
            FilteredServiceRequests.Clear();
            foreach (var request in GlobalStorage.ServiceRequests.OrderBy(r => r.Status))
            {
                FilteredServiceRequests.Add(request);
            }
        }
    }
}