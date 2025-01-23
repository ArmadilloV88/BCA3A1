using PROG7312ST10091991POEPART2;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROGPOEPART1ST10091991.Modules
{
    public class AVLTreeNode
    {
        public ServiceRequest Request { get; set; } // This stores the ServiceRequest object
        public AVLTreeNode Left { get; set; } // Left child of the node
        public AVLTreeNode Right { get; set; } // Right child of the node
        public int Height { get; set; } // Height of the node for balancing

        public AVLTreeNode(ServiceRequest request)
        {
            Request = request;
            Left = null;
            Right = null;
            Height = 1; // Height is initialized to 1 for a new node
        }
    }
}
