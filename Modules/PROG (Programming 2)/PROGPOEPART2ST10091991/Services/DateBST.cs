using PROG7312ST10091991POEPART2;
using PROGPOEPART1ST10091991.Modules;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROGPOEPART1ST10091991.Services
{
    public class DateBST
    {
        private BSTNode root;

        // Insert service requests by date

        public List<ServiceRequest> FilterByDateRange(DateTime startDate, DateTime endDate)
        {
            var results = new List<ServiceRequest>();
            FilterByDateRange(root, startDate, endDate, results);
            return results;
        }

        private void FilterByDateRange(BSTNode node, DateTime startDate, DateTime endDate, List<ServiceRequest> results)
        {
            if (node == null) return;

            // Check if current node's date falls within range
            if (node.Request.ReportDetails.EventDate >= startDate && node.Request.ReportDetails.EventDate <= endDate)
            {
                results.Add(node.Request);
            }

            // Traverse left subtree if range overlaps
            if (startDate < node.Request.ReportDetails.EventDate)
            {
                FilterByDateRange(node.Left, startDate, endDate, results);
            }

            // Traverse right subtree if range overlaps
            if (endDate > node.Request.ReportDetails.EventDate)
            {
                FilterByDateRange(node.Right, startDate, endDate, results);
            }
        }
    }
}
