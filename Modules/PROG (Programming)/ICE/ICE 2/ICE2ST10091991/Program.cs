namespace ICE2ST10091991;
using System;

public class Logger
{
    // Private constructor to prevent instantiation from outside
    private Logger()
    {
        // Initialization code, if any
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine("Logger instance created.");
    }

    // Private static instance of the Logger class
    private static Lazy<Logger> instance = new Lazy<Logger>(() => new Logger());

    // Public static method to access the Logger instance
    public static Logger Instance => instance.Value;

    // Method to log messages
    public void Log(string message)
    {
        // Basic logging functionality, you can extend this as needed
        Console.ForegroundColor = ConsoleColor.Yellow;
        Console.WriteLine($"[LOG] {DateTime.Now}: {message}");
    }
}
    internal class Program
    {
    public static void Loggermenu()
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine("  L       OOOO       GGGG       GGGG    EEEEEE   RRRR");
        Console.WriteLine("  L      O    O     G    G     G    G   E        R   R");
        Console.WriteLine("  L     O      O   G          G         EEEEEE   RRRR");
        Console.WriteLine("  L     O      O   G    GG    G    GG   EEEEEE   R R");
        Console.WriteLine("  L      O    O     G    G     G    G   E        R  R");
        Console.WriteLine("  LLLLL   OOOO       GGGG       GGGG    EEEEEE   R   R");
        Console.WriteLine("");
        Console.ForegroundColor = ConsoleColor.White;
    }
    static void Main(string[] args)
    {
        Console.Clear();
        string username = "";
        string password = "";
        Loggermenu();
        Console.WriteLine("Username : ");
        username=Console.ReadLine();
        Console.Clear();
        Loggermenu();
        Console.WriteLine("Username : "+username);
        Console.WriteLine("Password : ");
        password = Console.ReadLine();
        if (Login(username, password))
        {
            Console.ForegroundColor= ConsoleColor.Green;
            Console.WriteLine("Login granted, welcome : "+username);
        }
        else
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Login rejected : "+username +", (password or username is either incorrect or not existing)");
            Console.ReadLine();
            Main(args);
        }

        Logger logger1 = Logger.Instance;
        Logger logger2 = Logger.Instance;

        // Both logger1 and logger2 point to the same instance
        Console.ForegroundColor = ConsoleColor.Blue;
        Console.WriteLine($"Is logger1 the same instance as logger2? {logger1 == logger2}");

        // Logging messages
        logger1.Log("(log 1)");
        logger2.Log("(log 2)");

        Console.ReadKey();
    }
    public static bool Login(string username, string password)
    {
        string[,] credentials = {
        { "Admin", "admin" },
        { "User 1", "1234" },
        { "User 2", "1234" },
        { "User 3", "1234" }
    };

        bool valid = false;

        for (int i = 0; i < credentials.GetLength(0); i++)
        {
            string storedUsername = credentials[i, 0];
            string storedPassword = credentials[i, 1];

            // Check if the provided username and password match any stored credentials
            if (storedUsername == username && storedPassword == password)
            {
                valid = true;
                break;
            }
        }
        return valid;
    }
}
