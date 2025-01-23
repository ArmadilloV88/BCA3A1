using PROG7312ST10091991POEPART2;
using System;
using System.Collections.Generic;
using System.Linq;

namespace PROGPOEPART1ST10091991
{
    public class ServiceRequestPriorityQueue
    {
        private readonly SortedDictionary<int, Queue<ServiceRequest>> _priorityQueue;

        public ServiceRequestPriorityQueue()
        {
            _priorityQueue = new SortedDictionary<int, Queue<ServiceRequest>>
            {
                { 1, new Queue<ServiceRequest>() }, // Priority 1: Request
                { 2, new Queue<ServiceRequest>() }, // Priority 2: Review
                { 3, new Queue<ServiceRequest>() }, // Priority 3: In Process
                { 4, new Queue<ServiceRequest>() }  // Priority 4: Processed
            };
        }

        public void Enqueue(ServiceRequest request)
        {
            if (_priorityQueue.ContainsKey(request.Status))
            {
                _priorityQueue[request.Status].Enqueue(request);
            }
        }

        public ServiceRequest Dequeue()
        {
            foreach (var priority in _priorityQueue.Keys)
            {
                if (_priorityQueue[priority].Count > 0)
                {
                    return _priorityQueue[priority].Dequeue();
                }
            }
            return null;
        }

        public List<ServiceRequest> GetAllRequests()
        {
            return _priorityQueue.Values
                .SelectMany(queue => queue)
                .ToList();
        }

        public bool HasRequests => _priorityQueue.Values.Any(queue => queue.Count > 0);
    }
}