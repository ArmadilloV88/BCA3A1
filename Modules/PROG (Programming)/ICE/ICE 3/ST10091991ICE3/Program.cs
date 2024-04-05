using ST10091991ICE3;
using System;
using System.Collections.Generic;
using System.Threading;
//Common interface for all products
interface ICar
{
    string Brand { get; set; }
    int Mileage { get; set; }
    string RegistrationPlate { get; set; }
    string Type { get; set; }
    string Color { get; set; }
    void Display();
}
//Implementation of a car
class Car : ICar
{
    public string? Brand { get; set; }
    public int Mileage { get; set; }
    public string? RegistrationPlate { get; set; }
    public string? Type { get; set; }
    public string? Color { get; set; }

    public void Display()
    {
        Console.WriteLine($"Brand: {Brand}, Mileage: {Mileage}, Registration Plate: {RegistrationPlate}, Type: {Type}, Color: {Color}");
    }
}
// Creator class
abstract class CarCreator
{
    public abstract ICar FactoryMethod(string brand, int mileage, string registrationPlate, string type, string color);
}
// Creator class that overrides the factory method
class CarFactory : CarCreator
{
    public override ICar FactoryMethod(string brand, int mileage, string registrationPlate, string type, string color)
    {
        return new Car
        {
            Brand = brand,
            Mileage = mileage,
            RegistrationPlate = registrationPlate,
            Type = type,
            Color = color
        };
    }
}
class Program
{
    static List<ICar> cars = new List<ICar>();
    static CarCreator carFactory = new CarFactory();
    static bool exitRequested = false;
    static void Main(string[] args)
    {
        Console.WriteLine("Car Login System");

        // Start a new thread for the menu
        Thread menuThread = new Thread(MenuThread);
        menuThread.Start();

        while (!exitRequested)
        {
            Thread.Sleep(100); // Sleep to reduce CPU usage
        }
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine("Exiting application...");
    }
    static void MenuThread()
    {
        while (!exitRequested)
        {
            Console.Clear();
            Console.WriteLine("\nMenu:");
            Console.WriteLine("1. Add Car (ICE3)");
            Console.WriteLine("2. View Cars (ICE3)");
            Console.WriteLine("3. Edit Car (ICE3)");
            Console.WriteLine("4. Car Builder (pop quiz)");
            Console.WriteLine("5. Exit Application (ICE3)");
            Console.Write("Enter your choice: ");
            string? choice = Console.ReadLine();

            try
            {
                switch (choice)
                {
                    case "1":
                        AddCar();
                        break;
                    case "2":
                        ViewCars();
                        break;
                    case "3":
                        EditCar();
                        break;
                    case "4":
                        PopQuizQuestion.MakeCar();
                        break;
                    case "5":
                        exitRequested = true;
                        break;
                    default:
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("Invalid choice. Please try again.");
                        Console.ForegroundColor = ConsoleColor.White;
                        break;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }
    static void AddCar()
    {
        Console.Clear();
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine("\nAdding Car:");
        Console.ForegroundColor = ConsoleColor.White;

        try
        {
            Console.Write("Enter Brand: ");
            string brand = Console.ReadLine();

            Console.Write("Enter Mileage: ");
            int mileage = int.Parse(Console.ReadLine());

            Console.Write("Enter Registration Plate: ");
            string registrationPlate = Console.ReadLine();

            Console.Write("Enter Type: ");
            string type = Console.ReadLine();

            Console.Write("Enter Color: ");
            string color = Console.ReadLine();

            ICar car = carFactory.FactoryMethod(brand, mileage, registrationPlate, type, color);
            cars.Add(car);
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("Car added successfully!");
            Console.ForegroundColor = ConsoleColor.White;
        }
        catch (FormatException)
        {
            Console.WriteLine("Invalid input format. Please enter a valid number for Mileage.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
        }
    }
    static void ViewCars()
    {
        Console.Clear();
        if (cars.Count == 0)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("No cars added yet.");
            Console.ForegroundColor = ConsoleColor.White;
            Console.ReadLine();
            return;
        }
        
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine("\nViewing Cars:");
        Console.ForegroundColor = ConsoleColor.White;
        foreach (var car in cars)
        {
            car.Display();
        }
    }
    static void EditCar()
    {
        Console.Clear();
        if (cars.Count == 0)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("No cars added yet.");
            Console.ForegroundColor = ConsoleColor.White;
            Console.ReadLine();
            return;
        }
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine("\nEditing Car:");
        Console.ForegroundColor = ConsoleColor.White;
        ViewCars();
        try
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("Enter the details of the car you want to edit:");
            Console.ForegroundColor = ConsoleColor.White;
            Console.Write("Enter Brand: ");
            string brand = Console.ReadLine();

            Console.Write("Enter Mileage: ");
            int mileage = int.Parse(Console.ReadLine());

            Console.Write("Enter Registration Plate: ");
            string registrationPlate = Console.ReadLine();

            Console.Write("Enter Type: ");
            string type = Console.ReadLine();

            Console.Write("Enter Color: ");
            string color = Console.ReadLine();

            // Search for the car to edit based on the entered details
            var carToEdit = cars.FirstOrDefault(car =>
                car.Brand == brand &&
                car.Mileage == mileage &&
                car.RegistrationPlate == registrationPlate &&
                car.Type == type &&
                car.Color == color);

            if (carToEdit == null)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Car not found.");
                Console.ForegroundColor = ConsoleColor.White;
                return;
            }
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("Enter the new details:");
            Console.ForegroundColor = ConsoleColor.White;

            Console.Write("Enter Brand: ");
            carToEdit.Brand = Console.ReadLine();

            Console.Write("Enter Mileage: ");
            carToEdit.Mileage = int.Parse(Console.ReadLine());

            Console.Write("Enter Registration Plate: ");
            carToEdit.RegistrationPlate = Console.ReadLine();

            Console.Write("Enter Type: ");
            carToEdit.Type = Console.ReadLine();

            Console.Write("Enter Color: ");
            carToEdit.Color = Console.ReadLine();
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("Car edited successfully!");
            Console.ForegroundColor = ConsoleColor.White;
        }
        catch (FormatException)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Invalid input format. Please enter a valid number for Mileage.");
            Console.ForegroundColor = ConsoleColor.White;
        }
        catch (Exception ex)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"Error: {ex.Message}");
            Console.ForegroundColor = ConsoleColor.White;
        }
    }
}