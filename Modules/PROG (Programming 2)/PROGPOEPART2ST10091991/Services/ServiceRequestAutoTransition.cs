using PROG7312ST10091991POEPART2;
using System;
using System.Linq;
using System.Threading.Tasks;
using System.Timers;

namespace PROGPOEPART1ST10091991
{
    public class ServiceRequestAutoTransition
    {
        private static System.Timers.Timer _transitionTimer; // Specify Timer from System.Timers
        private static readonly TimeSpan TransitionInterval = TimeSpan.FromDays(3); // Set for 3-day interval

        public static void StartAutoTransition()
        {
            // Set up a timer to trigger the status check every 3 days
            _transitionTimer = new System.Timers.Timer(TransitionInterval.TotalMilliseconds); // Use Timer from System.Timers
            _transitionTimer.Elapsed += OnTimedEvent;
            _transitionTimer.AutoReset = true;
            _transitionTimer.Start();
        }

        private static void OnTimedEvent(object sender, ElapsedEventArgs e)
        {
            // Check each service request and update its status if necessary
            foreach (var request in GlobalStorage.ServiceRequests.Where(r => r.Status < 4))
            {
                // Automatically transition to the next phase
                request.Status = request.Status + 1;
                // Optionally, update any UI or notify the user about the transition
                Console.WriteLine($"Request {request.RequestId} moved to status {request.Status}");
            }
        }

        public static void StopAutoTransition()
        {
            _transitionTimer?.Stop();
        }
    }
}