using PROG7312ST10091991POEPART2;
using System;
using System.Collections.Generic;
using System.Linq;

namespace PROGPOEPART1ST10091991.Services
{
    public class ServiceRequestGraph
    {
        private Dictionary<string, List<ServiceRequest>> categoryLocationMap;

        public ServiceRequestGraph()
        {
            categoryLocationMap = new Dictionary<string, List<ServiceRequest>>();
        }

        // Adds a request to the graph based on category and location
        public void AddRequest(ServiceRequest request)
        {
            string key = $"{request.ReportDetails.Category}-{request.ReportDetails.Location}";
            if (!categoryLocationMap.ContainsKey(key))
            {
                categoryLocationMap[key] = new List<ServiceRequest>();
            }
            categoryLocationMap[key].Add(request);
        }

        // Filters by category and location
        public List<ServiceRequest> FilterByCategoryAndLocation(string category, string location)
        {
            string key = $"{category}-{location}";
            return categoryLocationMap.ContainsKey(key) ? categoryLocationMap[key] : new List<ServiceRequest>();
        }

        // Searches for service requests based on location
        public List<ServiceRequest> SearchByLocation(string location)
        {
            // Filter requests that match the specified location
            return categoryLocationMap
                .Where(kv => kv.Key.Contains(location, StringComparison.OrdinalIgnoreCase)) // Case-insensitive search
                .SelectMany(kv => kv.Value)
                .ToList();
        }
    }
}