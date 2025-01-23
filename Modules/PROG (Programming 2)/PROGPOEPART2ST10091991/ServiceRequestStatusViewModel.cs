using System;
using System.Collections.ObjectModel;
using System.Linq;
using PROG7312ST10091991;
using PROG7312ST10091991POEPART2;
using System.Timers;

namespace PROGPOEPART1ST10091991
{
    public class ServiceRequestStatusViewModel
    {
        public ObservableCollection<ServiceRequest> ServiceRequests { get; set; }
        private ServiceRequestPriorityQueue PriorityQueue { get; set; }
        private static System.Timers.Timer _transitionTimer;
        private string _searchLocationFilter; // Store the search filter value

        public ServiceRequestStatusViewModel()
        {
            // Initialize Priority Queue and ServiceRequests collection
            PriorityQueue = new ServiceRequestPriorityQueue();
            ServiceRequests = new ObservableCollection<ServiceRequest>
            {
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Issue 1",
                        EventDate = DateTime.Now.AddDays(-1),
                        Category = "Category A",
                        Location = "Location X"
                    },
                    Status = 1 // Request phase
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Issue 2",
                        EventDate = DateTime.Now.AddDays(-3),
                        Category = "Category B",
                        Location = "Location Y"
                    },
                    Status = 2 // Review phase
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Issue 3",
                        EventDate = DateTime.Now.AddDays(-6),
                        Category = "Category B",
                        Location = "Location Y"
                    },
                    Status = 2 // Review phase
                },
                new ServiceRequest
                {
                    ReportDetails = new ServiceDetails
                    {
                        Description = "Issue 4",
                        EventDate = DateTime.Now.AddDays(-9),
                        Category = "Category B",
                        Location = "Location Y"
                    },
                    Status = 2 // Review phase
                }
            };

            // Add existing ServiceRequests from GlobalStorage
            foreach (var request in GlobalStorage.ServiceRequests)
            {
                ServiceRequests.Add(request);
            }

            UpdateStatusesAndEnqueue(); // Initial status update
            StartAutoTransition(); // Start automatic status updates
        }

        public void SetLocationSearchFilter(string filter)
        {
            _searchLocationFilter = filter;  // Store the search filter

            // Reapply filter based on the updated location
            ApplyLocationSearchFilter();
        }

        private void ApplyLocationSearchFilter()
        {
            var filteredRequests = ServiceRequests.Where(request =>
                string.IsNullOrEmpty(_searchLocationFilter) ||
                request.ReportDetails.Location.Contains(_searchLocationFilter, StringComparison.OrdinalIgnoreCase)
            ).ToList();

            // Clear and re-populate the collection with filtered requests
            ServiceRequests.Clear();
            foreach (var request in filteredRequests)
            {
                ServiceRequests.Add(request);
            }
        }

        private void UpdateStatusesAndEnqueue()
        {
            PriorityQueue = new ServiceRequestPriorityQueue(); // Reset the queue to avoid duplicates

            foreach (var request in ServiceRequests)
            {
                var daysSinceEvent = (DateTime.Now - request.ReportDetails.EventDate).Days;

                // Update status based on days since EventDate
                if (daysSinceEvent < 3)
                {
                    request.Status = 1; // Request
                }
                else if (daysSinceEvent < 6)
                {
                    request.Status = 2; // Review
                }
                else if (daysSinceEvent < 9)
                {
                    request.Status = 3; // In Process
                }
                else
                {
                    request.Status = 4; // Processed
                }

                PriorityQueue.Enqueue(request);
            }

            ServiceRequests.Clear();
            foreach (var request in PriorityQueue.GetAllRequests())
            {
                ServiceRequests.Add(request);
            }
        }

        public ServiceRequest GetNextServiceRequest()
        {
            return PriorityQueue.HasRequests ? PriorityQueue.Dequeue() : null;
        }

        private void StartAutoTransition()
        {
            _transitionTimer = new System.Timers.Timer(TimeSpan.FromDays(3).TotalMilliseconds); // Set the interval to 3 days
            _transitionTimer.Elapsed += (sender, e) => OnTimedTransition();
            _transitionTimer.AutoReset = true;
            _transitionTimer.Start();
        }

        private void OnTimedTransition()
        {
            foreach (var request in ServiceRequests.Where(r => r.Status < 4))
            {
                request.Status += 1;
                Console.WriteLine($"Request {request.RequestId} moved to status {request.Status}");
            }

            // Refresh the observable collection to reflect updated statuses
            UpdateStatusesAndEnqueue();
        }

        public void StopAutoTransition()
        {
            _transitionTimer?.Stop();
            _transitionTimer?.Dispose();
        }
    }
}
