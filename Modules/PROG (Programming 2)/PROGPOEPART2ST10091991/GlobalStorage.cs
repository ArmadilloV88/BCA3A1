using PROGPOEPART1ST10091991;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace PROG7312ST10091991POEPART2
{
    public static class GlobalStorage
    {
        public static List<string> UserSearchPatterns { get; set; } = new List<string>();

        // Sorted dictionary to organize events by date
        public static SortedDictionary<DateTime, GlobalEvent> EventsByDate { get; set; } = new SortedDictionary<DateTime, GlobalEvent>();

        // Queue for upcoming events in order of priority (by date)
        public static Queue<GlobalEvent> UpcomingEventsQueue { get; set; } = new Queue<GlobalEvent>();

        // Set for unique categories
        public static HashSet<string> EventCategories { get; set; } = new HashSet<string>();

        public static List<IssueReport> IssueReports { get; set; } = new List<IssueReport>();

        public static ObservableCollection<ServiceRequest> ServiceRequests { get; set; } = new ObservableCollection<ServiceRequest>();


    }
    public class ServiceRequest
    {
        public string RequestId { get; set; }
        public int Status { get; set; } // 1: Request, 2: Review, 3: In Process, 4: Processed
        public ServiceDetails ReportDetails { get; set; }
        public DateTime RequestDate { get; set; }

        // For UI display based on Status
        public string StatusDisplay
        {
            get
            {
                return Status switch
                {
                    1 => "Request",
                    2 => "In Review",
                    3 => "In Process",
                    4 => "Processed",
                    _ => "Unknown"
                };
            }
        }
    }

}