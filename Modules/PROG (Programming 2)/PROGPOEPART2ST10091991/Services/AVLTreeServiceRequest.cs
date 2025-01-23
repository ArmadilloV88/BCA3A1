using PROG7312ST10091991POEPART2;
using PROGPOEPART1ST10091991.Modules;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROGPOEPART1ST10091991
{
    public class AVLTreeServiceRequest
    {
        private AVLTreeNode root;

        // Insert method as shown previously

        // Search method to retrieve all requests in a specific category
        public List<ServiceRequest> SearchByCategory(string category)
        {
            var results = new List<ServiceRequest>();
            SearchByCategory(root, category, results);
            return results;
        }

        private void SearchByCategory(AVLTreeNode node, string category, List<ServiceRequest> results)
        {
            if (node == null) return;

            // If category matches, add to results
            if (node.Request.ReportDetails.Category.Equals(category, StringComparison.OrdinalIgnoreCase))
            {
                results.Add(node.Request);
            }

            // Traverse left and right children
            SearchByCategory(node.Left, category, results);
            SearchByCategory(node.Right, category, results);
        }
    }

}
