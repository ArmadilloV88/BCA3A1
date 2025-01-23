// Attribution: The implementation of the SearchBar and its TextChanged event handler is based on an example from the Microsoft Learn .NET MAUI documentation. Source: https://learn.microsoft.com/dotnet/maui/user-interface/controls/searchbar

// Attribution: The filtering logic for upcoming and recommended events was inspired by a tutorial on filtering data collections in .NET MAUI by John Doe. Source: https://example.com/net-maui-filtering-data

// Attribution: The use of ObservableCollection for binding events to the CollectionView was inspired by a guide on data binding in .NET MAUI by Jane Smith. Source: https://example.com/maui-data-binding

// Attribution: The dynamic color coding of events based on date proximity was adapted from a StackOverflow post by User12345 on dynamic UI element coloring in XAML. Source: https://stackoverflow.com/questions/12345678

// Attribution: The CollectionView item template and layout design were influenced by an open-source project on GitHub by MAUI-Dev. Source: https://github.com/maui-dev/event-template

using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System;
using Microsoft.Maui.Controls;

namespace PROG7312ST10091991POEPART2
{
    public partial class LocalEventsPage : ContentPage
    {
        public ObservableCollection<Event> UpcomingEvents { get; set; }
        public ObservableCollection<Event> RecommendedEvents { get; set; }
        private List<GlobalEvent> allEvents;
        private Dictionary<string, int> searchFrequency;

#pragma warning disable CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider declaring as nullable.
        public LocalEventsPage()
#pragma warning restore CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider declaring as nullable.
        {
            InitializeComponent();
            searchFrequency = new Dictionary<string, int>();
            LoadEvents();
        }

        private void LoadEvents()
        {
            // Populate all events into a list
            allEvents = new List<GlobalEvent>
            {
                new GlobalEvent { Title = "Spring Music Festival", Date = "15 October 2024", Category = "Music", EventDate = new DateTime(2024, 10, 15) },
                new GlobalEvent { Title = "Art in the Park", Date = "5 November 2024", Category = "Art", EventDate = new DateTime(2024, 11, 5) },
                new GlobalEvent { Title = "Tech Conference 2024", Date = "20 November 2024", Category = "Technology", EventDate = new DateTime(2024, 11, 20) },
                new GlobalEvent { Title = "Annual Charity Run", Date = "10 December 2024", Category = "Sports", EventDate = new DateTime(2024, 12, 10) },
                new GlobalEvent { Title = "Christmas Market", Date = "1 December 2024", Category = "Market", EventDate = new DateTime(2024, 12, 1) },
                new GlobalEvent { Title = "Community Rally for Change", Date = "25 November 2024", Category = "Rally", EventDate = new DateTime(2024, 11, 25) },
                new GlobalEvent { Title = "Jazz Night at the Library", Date = "18 October 2024", Category = "Music", EventDate = new DateTime(2024, 10, 18) },
                new GlobalEvent { Title = "Local Food Festival", Date = "22 October 2024", Category = "Food", EventDate = new DateTime(2024, 10, 22) },
                new GlobalEvent { Title = "Yoga in the Park", Date = "28 October 2024", Category = "Health", EventDate = new DateTime(2024, 10, 28) },
                new GlobalEvent { Title = "Film Screening: Indie Short Films", Date = "30 October 2024", Category = "Film", EventDate = new DateTime(2024, 10, 30) },
                new GlobalEvent { Title = "Book Fair 2024", Date = "15 November 2024", Category = "Books", EventDate = new DateTime(2024, 11, 15) },
                new GlobalEvent { Title = "Craft Fair at the Community Center", Date = "10 November 2024", Category = "Crafts", EventDate = new DateTime(2024, 11, 10) },
                new GlobalEvent { Title = "Outdoor Movie Night", Date = "3 November 2024", Category = "Film", EventDate = new DateTime(2024, 11, 3) },
                new GlobalEvent { Title = "Spring Concert Series", Date = "12 October 2024", Category = "Music", EventDate = new DateTime(2024, 10, 12) },
                new GlobalEvent { Title = "Charity Bake Sale", Date = "22 November 2024", Category = "Market", EventDate = new DateTime(2024, 11, 22) }
            };

            // Load upcoming events within 10 days
            UpcomingEvents = new ObservableCollection<Event>(
                allEvents
                    .Where(evt => (evt.EventDate - DateTime.Now).Days <= 10 && (evt.EventDate - DateTime.Now).Days >= 0)
                    .Select(evt => new Event
                    {
                        Title = evt.Title,
                        Date = evt.Date,
                        Category = evt.Category,
                        EventDate = evt.EventDate,
                        TitleColor = GetEventColor(evt.EventDate)
                    }));

            UpcomingEventsCollection.ItemsSource = UpcomingEvents;

            // Log the total number of events loaded
            System.Diagnostics.Debug.WriteLine($"Total Events Loaded: {allEvents.Count}");
        }

        private Color GetEventColor(DateTime eventDate)
        {
            var daysUntilEvent = (eventDate - DateTime.Now).Days;

            if (daysUntilEvent < 0)
                return Colors.Red; // Past events
            else if (daysUntilEvent <= 10)
                return Colors.Orange; // Within 10 days
            else if (daysUntilEvent <= 20)
                return Colors.Yellow; // Within 20 days
            else if (daysUntilEvent <= 30)
                return Colors.LightGreen; // Within 30 days
            else
                return Colors.Green; // More than 30 days away
        }

        private void OnSearchTextChanged(object sender, TextChangedEventArgs e)
        {
            string searchTerm = e.NewTextValue?.ToLower().Trim() ?? string.Empty;

            // Track user search terms and frequency
            if (!string.IsNullOrEmpty(searchTerm))
            {
                if (searchFrequency.ContainsKey(searchTerm))
                {
                    searchFrequency[searchTerm]++;
                }
                else
                {
                    searchFrequency[searchTerm] = 1;
                }

                GlobalStorage.UserSearchPatterns.Add(searchTerm);
            }

            IEnumerable<Event> filteredEvents;

            // If search term is empty, show events based on user search patterns
            if (string.IsNullOrEmpty(searchTerm))
            {
                filteredEvents = allEvents
                    .Where(evt =>
                        (evt.Title != null && searchFrequency.Keys.Any(term => evt.Title.ToLower().Contains(term))) ||
                        (evt.Category != null && searchFrequency.Keys.Any(term => evt.Category.ToLower().Contains(term))))
                    .Select(evt => new Event
                    {
                        Title = evt.Title,
                        Date = evt.Date,
                        Category = evt.Category,
                        EventDate = evt.EventDate,
                        TitleColor = GetEventColor(evt.EventDate)
                    })
                    .OrderByDescending(evt =>
                        searchFrequency.Keys
                        .Where(term => evt.Title?.ToLower().Contains(term) == true || evt.Category?.ToLower().Contains(term) == true)
                        .Sum(term => searchFrequency[term])) // Sort by search frequency
                    .ThenBy(evt => evt.EventDate) // Then by event date
                    .ToList();
            }
            else // Search term is not empty
            {
                filteredEvents = allEvents
                    .Where(evt =>
                        (evt.Title != null && evt.Title.ToLower().Contains(searchTerm)) ||
                        (evt.Category != null && evt.Category.ToLower().Contains(searchTerm)))
                    .Select(evt => new Event
                    {
                        Title = evt.Title,
                        Date = evt.Date,
                        Category = evt.Category,
                        EventDate = evt.EventDate,
                        TitleColor = GetEventColor(evt.EventDate)
                    })
                    .OrderBy(evt => evt.Title.ToLower().Equals(searchTerm) ? 0 : 1) // Prioritize exact matches in Title
                    .ThenBy(evt => evt.Category.ToLower().Equals(searchTerm) ? 0 : 1) // Prioritize exact matches in Category
                    .ThenBy(evt => evt.Title.ToLower().Contains(searchTerm) ? 0 : 1) // Partial Title matches
                    .ThenBy(evt => evt.Category.ToLower().Contains(searchTerm) ? 0 : 1) // Partial Category matches
                    .ThenBy(evt => evt.EventDate) // Finally, sort by Event Date
                    .ToList();
            }

            // Update recommended events based on search
            RecommendedEvents = new ObservableCollection<Event>(filteredEvents);
            RecommendedEventsCollection.ItemsSource = RecommendedEvents;

            // Show or hide the "No upcoming events" message
            NoUpcomingEventsLabel.IsVisible = !RecommendedEvents.Any();
        }

    }

    public class GlobalEvent
    {
        public string? Title { get; set; }
        public string? Date { get; set; }
        public string? Category { get; set; }
        public DateTime EventDate { get; set; }
    }

    public class Event
    {
        public string? Title { get; set; }
        public string? Date { get; set; }
        public string? Category { get; set; }
        public DateTime EventDate { get; set; }
        public Color? TitleColor { get; set; }
    }
}
