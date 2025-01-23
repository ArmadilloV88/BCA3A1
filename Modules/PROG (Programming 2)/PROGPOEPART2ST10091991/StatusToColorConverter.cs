using System;
using Microsoft.Maui.Controls;
using Microsoft.Maui.Graphics;

namespace PROGPOEPART1ST10091991
{
    public class StatusToColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if (value is int status)
            {
                return status switch
                {
                    1 => Colors.Yellow,      // Request (Phase 1)
                    2 => Colors.Orange,      // Review (Phase 2)
                    3 => Colors.Blue,        // In Process (Phase 3)
                    4 => Colors.Green,       // Processed (Phase 4)
                    _ => Colors.Gray,        // Unknown or invalid status
                };
            }
            return Colors.Gray;          // Default fallback color for non-integer status
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException(); // No need to implement ConvertBack for this case
        }
    }
}
