using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Example_1
{
    internal class Program
    {
        static void Main(string[] args)
        {
            bool Init = Initialization();
            if(Init)
            {
                Console.BackgroundColor = ConsoleColor.Black;
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("         A     RRRR    CCCC");
                Console.WriteLine("        A A    R   R  C     C");
                Console.WriteLine("       A   A   RRRR  C");
                Console.WriteLine("      AAAAAAA  R  R   C     C");
                Console.WriteLine("     A       A R   R   CCCC");
                Console.WriteLine("---------------------------------------------");
                Console.WriteLine("                 Arc Ready                   ");
                Console.WriteLine("---------------------------------------------");
                Console.ForegroundColor = ConsoleColor.White;
                Console.BackgroundColor = ConsoleColor.Green;
                Console.WriteLine("Status : Ready to execute");
                Console.BackgroundColor = ConsoleColor.Black;
                Console.WriteLine("Please press enter to continue");
                Console.ReadLine();
                Menu();
            }
            else
            {
                Console.BackgroundColor = ConsoleColor.Black;
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("                   A    A");
                Console.WriteLine("                    R  R");
                Console.WriteLine("                     CC");
                Console.WriteLine("                    R  R");
                Console.WriteLine("                   A    A");
                Console.WriteLine("---------------------------------------------");
                Console.WriteLine("                 Arc not Ready                   ");
                Console.WriteLine("---------------------------------------------");
                bool made = CreateDirectory("C:\\Users\\ccver\\Desktop\\VC\\BCA3A1\\Modules\\PROG (Programming)\\Code\\Example 1\\Example 1\\Data");
                 if (made)
                {

                    CheckDirectory("C:\\Users\\ccver\\Desktop\\VC\\BCA3A1\\Modules\\PROG (Programming)\\Code\\Example 1\\Example 1\\Data");
                }
                else
                {

                }
            }
            Console.ReadKey();
        }
        static void Menu()
        {
            Console.Clear();
            Console.WriteLine("|-------------------------------------------------------|");
            Console.WriteLine("|    A     RRRRR    CCCC                                |");
            Console.WriteLine("|   A A    R    R  C    C                               |");
            Console.WriteLine("|  A   A   RRRRR  C                                     |");
            Console.WriteLine("| AAAAAAA  R  R    C    C                               |");
            Console.WriteLine("|A       A R   R    CCCC                                |");
            Console.WriteLine("|                        M       M EEEEE N    N U    U  |");
            Console.WriteLine("|                        MM     MM E     N N  N U    U  |");
            Console.WriteLine("|                        M M   M M EEEEE N  N N U    u  |");
            Console.WriteLine("|                        M  M M  M E     N   NN U    U  |");
            Console.WriteLine("|                        M   M   M EEEEE N    N  UUUU   |");
            Console.WriteLine("|-------------------------------------------------------|");
        }
        static bool Initialization()
        {
            bool valid = false;
            string currentDirectoryPath = "C:\\Users\\ccver\\Desktop\\VC\\BCA3A1\\Modules\\PROG (Programming)\\Code\\Example 1\\Example 1\\Data";
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine("Looking for root directory");
            if (Directory.Exists(currentDirectoryPath))
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("Directory exists!");
                Console.ForegroundColor = ConsoleColor.White;
                valid = true;
                CheckDirectory(currentDirectoryPath);
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Directory does not exist!");
                Console.ForegroundColor = ConsoleColor.White;
                valid = false;
                CreateDirectory(currentDirectoryPath);
            }
            return valid;
        }
        static bool CheckDirectoryFiles(string path)
        {
            bool valid = false;
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine("Checking Entities folder for UseData.txt file");

            if (File.Exists(path +"\\Entities\\UserData.txt"))
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("File UserData exists in Entities folder");
                Console.ForegroundColor = ConsoleColor.White;
                if (File.Exists(path + "\\Entities\\Data.txt"))
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("File Data found in Entities folder");
                    Console.ForegroundColor = ConsoleColor.White;
                    Console.WriteLine("Checking Models folder for Data.txt file");
                    if (File.Exists(path + "\\Models\\Data.txt"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("File Data exists in Models folder");
                        valid = true;
                    }
                    else
                    {
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("File Data does not exists in Models folder");
                        Console.WriteLine("");
                        Console.ForegroundColor = ConsoleColor.Yellow;
                        Console.WriteLine("Creating file Data in Models folder");
                        File.CreateText(path + "\\Models\\Data.txt");
                        Console.ForegroundColor = ConsoleColor.White;
                        Console.WriteLine("Checking Models folder for Data.txt file");
                        if (File.Exists(path + "\\Models\\Data.txt"))
                        {
                            Console.ForegroundColor = ConsoleColor.Green;
                            Console.WriteLine("File Data found in Models folder");
                            valid = true;
                        }
                        else
                        {
                            Console.ForegroundColor = ConsoleColor.Red;
                            Console.WriteLine("File Data not found in Entities folder");
                        }

                    }
                }
                else
                {
                    File.CreateText(path + "\\Entities\\Data.txt");
                    Console.ForegroundColor = ConsoleColor.White;
                    Console.WriteLine("Checking Entities folder for Data.txt file");
                    if (File.Exists(path + "\\Entities\\Data.txt"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("File Data found in Models folder");
                    }
                    else
                    {
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("File Data not found in Models folder");
                    }

                }
            }
            else
            {
                File.CreateText(path + "\\Entities\\UserData.txt");
                Console.ForegroundColor = ConsoleColor.White;
                Console.WriteLine("Checking Entities folder for UserData.txt file");
                if (File.Exists(path + "\\Entities\\UserData.txt"))
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("File UserData made in Entities folder");
                    if (File.Exists(path + "\\Models\\Data.txt"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("File Data exists in Models folder");
                        valid = true;
                    }
                    else
                    {
                        File.CreateText(path + "\\Models\\Data.txt");
                        Console.ForegroundColor = ConsoleColor.White;
                        Console.WriteLine("Checking Models folder for Data.txt file");
                        if (File.Exists(path + "\\Models\\Data.txt"))
                        {
                            Console.ForegroundColor = ConsoleColor.Green;
                            Console.WriteLine("File Data found in Models folder");
                            valid = true;
                        }
                        else
                        {
                            Console.ForegroundColor = ConsoleColor.Red;
                            Console.WriteLine("File Data not found in Models folder");
                        }

                    }
                }
                else
                {
                    Console.WriteLine("File UserData not made in Entities folder");
                    Console.ForegroundColor = ConsoleColor.White;
                    Console.WriteLine("Checking Entities folder for Data.txt file");
                    if (File.Exists(path + "\\Entities\\Data.txt"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("File UserData exists in Entities folder");

                    }
                    else
                    {
                        File.CreateText(path + "\\Entities\\Data.txt");
                        Console.ForegroundColor = ConsoleColor.White;
                        Console.WriteLine("Checking Entities folder for Data.txt file");
                        if (File.Exists(path + "\\Entities\\Data.txt"))
                        {
                            Console.WriteLine("File Data made in Entities folder");
                            Console.ForegroundColor = ConsoleColor.White;
                            Console.WriteLine("Checking Entities folder for UserData.txt file");
                            if (File.Exists(path + "\\Entities\\UserData.txt"))
                            {
                                Console.WriteLine("File UserData made in Entities folder");
                                Console.ForegroundColor = ConsoleColor.White;
                                Console.WriteLine("Checking Moodels folder for Data.txt file");
                                if (File.Exists(path + "\\Models\\Data.txt"))
                                {
                                    Console.WriteLine("File Data exists in Models folder");
                                    valid = true;
                                }
                                else
                                {
                                    File.CreateText(path + "\\Models\\Data.txt");
                                    Console.ForegroundColor = ConsoleColor.White;
                                    Console.WriteLine("Checking Moodels folder for Data.txt file");
                                    if (File.Exists(path + "\\Models\\Data.txt"))
                                    {
                                        Console.WriteLine("File Data found in Models folder");
                                        valid = true;
                                    }
                                    else
                                    {
                                        Console.WriteLine("File Data not found in Models folder");
                                    }

                                }
                            }
                        }
                        else
                        {
                            Console.WriteLine("File Data not made in Entities folder");
                        }

                    }
                }
            }
                return valid;
        }
        static bool CheckDirectory(string directory)
        {
            bool valid = false;
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine("Looking for 'Entities' folder");
            if (Directory.Exists(directory+"\\Entities"))
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("Entities folder found");
                Console.ForegroundColor = ConsoleColor.White;
                Console.WriteLine("Looking for 'Models' folder");
                if (Directory.Exists(directory + "\\Models"))
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("Models folder found");
                    CheckDirectoryFiles(directory);
                }
                else
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("Models folder not found");
                    Console.ForegroundColor = ConsoleColor.Yellow;
                    Console.WriteLine("Creating 'Models' folder");
                    Directory.CreateDirectory(directory + "\\Models");
                    Console.ForegroundColor = ConsoleColor.White;
                    Console.WriteLine("Looking for 'Models' folder");
                    if (Directory.Exists(directory + "\\Models"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("Models folder made");
                        CheckDirectoryFiles(directory);
                    }
                    else
                    {
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("Models folder not made");
                    }
                }
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Entities folder not found");
                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine("Creating 'Entities' folder");
                Directory.CreateDirectory(directory + "\\Entities");
                if (Directory.Exists(directory + "\\Entities"))
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("Entities folder made");
                    if (Directory.Exists(directory + "\\Models"))
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("Models folder found");
                        CheckDirectoryFiles(directory);
                    }
                    else
                    {
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("Models folder not found");
                        Console.ForegroundColor = ConsoleColor.Yellow;
                        Console.WriteLine("Creating 'Entities' folder");
                        Directory.CreateDirectory(directory + "\\Models");
                        if (Directory.Exists(directory + "\\Models"))
                        {
                            Console.ForegroundColor = ConsoleColor.Green;
                            Console.WriteLine("Models folder made");
                            CheckDirectoryFiles(directory);
                        }
                        else
                        {
                            Console.ForegroundColor = ConsoleColor.Red;
                            Console.WriteLine("Models folder not made");
                        }
                    }
                }
                else
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("Entities folder not made");
                }
            }
            return valid;
        }
        static bool CreateDirectory(string path)
        {
            bool made = false;
            Directory.CreateDirectory(path);
            if (Directory.Exists(path))
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("Directory created");
                CheckDirectory(path);
                made = true;
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Directory not created!");
                made = false;
            }
            return made;
        }
        /*static void WalkDirectoryTree(System.IO.DirectoryInfo root)
        {
            System.IO.FileInfo[] files = null;
            System.IO.DirectoryInfo[] subDirs = null;

            // First, process all the files directly under this folder 
            try
            {
                files = root.GetFiles("C:\\");
            }
            // This is thrown if even one of the files requires permissions greater 
            // than the application provides. 
            catch (UnauthorizedAccessException)
            {
                // This code just writes out the message and continues to recurse. 
                // You may decide to do something different here. For example, you 
                // can try to elevate your privileges and access the file again.
                Console.WriteLine("Unauthorized access");
            }

            catch (System.IO.DirectoryNotFoundException e)
            {
                Console.WriteLine(e.Message);
            }

            if (files != null)
            {
                foreach (System.IO.FileInfo fi in files)
                {
                    // In this example, we only access the existing FileInfo object. If we 
                    // want to open, delete or modify the file, then 
                    // a try-catch block is required here to handle the case 
                    // where the file has been deleted since the call to TraverseTree().
                    Console.WriteLine(fi.FullName);
                }

                // Now find all the subdirectories under this directory.
                subDirs = root.GetDirectories();

                foreach (System.IO.DirectoryInfo dirInfo in subDirs)
                {
                    // Resursive call for each subdirectory.
                    WalkDirectoryTree(dirInfo);
                }
            }
        }*/
    }
}
