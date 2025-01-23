using PROG7312ST10091991POEPART2;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROGPOEPART1ST10091991.Services
{
    public class ServiceRequestMinHeap
    {
        private List<ServiceRequest> heap = new List<ServiceRequest>();

        // Enqueue requests by adding to the heap and reordering
        public void Enqueue(ServiceRequest request)
        {
            heap.Add(request);
            // Heapify Up to maintain min-heap
            HeapifyUp(heap.Count - 1);
        }

        public ServiceRequest Dequeue()
        {
            if (heap.Count == 0) return null;

            // Root of the heap has the highest priority
            ServiceRequest topRequest = heap[0];

            // Replace root with the last element and heapify down
            heap[0] = heap[heap.Count - 1];
            heap.RemoveAt(heap.Count - 1);
            HeapifyDown(0);

            return topRequest;
        }

        private void HeapifyUp(int index)
        {
            while (index > 0)
            {
                int parentIndex = (index - 1) / 2;

                // Access EventDate from ReportDetails
                if (heap[index].ReportDetails?.EventDate >= heap[parentIndex].ReportDetails?.EventDate) break;

                // Swap if current is less than parent
                Swap(index, parentIndex);
                index = parentIndex;
            }
        }

        private void HeapifyDown(int index)
        {
            int lastIndex = heap.Count - 1;
            while (true)
            {
                int leftIndex = 2 * index + 1;
                int rightIndex = 2 * index + 2;
                int smallest = index;

                if (leftIndex <= lastIndex && heap[leftIndex].ReportDetails?.EventDate < heap[smallest].ReportDetails?.EventDate)
                    smallest = leftIndex;
                if (rightIndex <= lastIndex && heap[rightIndex].ReportDetails?.EventDate < heap[smallest].ReportDetails?.EventDate)
                    smallest = rightIndex;

                if (smallest == index) break;

                Swap(index, smallest);
                index = smallest;
            }
        }

        private void Swap(int index1, int index2)
        {
            var temp = heap[index1];
            heap[index1] = heap[index2];
            heap[index2] = temp;
        }
    }

}
