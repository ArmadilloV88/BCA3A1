using PROG7312ST10091991POEPART2;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROGPOEPART1ST10091991.Modules
{
    public class BSTNode
    {
        public ServiceRequest Request { get; set; } // This stores the ServiceRequest object
        public BSTNode Left { get; set; } // Left child of the node
        public BSTNode Right { get; set; } // Right child of the node

        public BSTNode(ServiceRequest request)
        {
            Request = request;
            Left = null;
            Right = null;
        }
    }
}
