using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ICE3ST10091991
{
    internal class Initializer : Program
    {
        public static bool Init()
        {
            bool initialized = false;
            string searchDirectory = @"C:\"; // Change this to the drive you want to search
            string targetFolder = "ICE3"; // Change this to the folder name you're looking for
            try
            {
                // Check if the directory exists
                if (Directory.Exists(searchDirectory))
                {
                    // Search for the target folder
                    string[] subDirectories = Directory.GetDirectories(searchDirectory, targetFolder, SearchOption.AllDirectories);

                    // Print the paths of all found directories
                    foreach (string subDirectory in subDirectories)
                    {
                        Console.WriteLine("Found directory: " + subDirectory);
                        initialized = true;
                    }
                }
                else
                {
                    Console.WriteLine("Directory does not exist: " + searchDirectory);
                }
            }
            catch (UnauthorizedAccessException)
            {
                Console.WriteLine("Access denied to directory: " + searchDirectory);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }

            return initialized;
        }
    }
}
