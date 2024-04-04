using System;
using System.Collections.Generic;
using System.Threading;


// Define a common interface for all products
interface ICar
{
    string Brand { get; set; }
    int Mileage { get; set; }
    string RegistrationPlate { get; set; }
    string Type { get; set; }
    string Color { get; set; }
    void Display();
}


// Concrete implementation of a car
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

// Concrete creator class that overrides the factory method
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

        Console.WriteLine("Exiting application...");
    }

    static void MenuThread()
    {
        while (!exitRequested)
        {
            Console.WriteLine("\nMenu:");
            Console.WriteLine("1. Add Car");
            Console.WriteLine("2. View Cars");
            Console.WriteLine("3. Edit Car");
            Console.WriteLine("4. Exit Application");
            Console.Write("Enter your choice: ");
            string choice = Console.ReadLine();

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
                        exitRequested = true;
                        break;
                    default:
                        Console.WriteLine("Invalid choice. Please try again.");
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
        Console.WriteLine("\nAdding Car:");

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

            Console.WriteLine("Car added successfully!");
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
        Console.WriteLine("\nViewing Cars:");

        if (cars.Count == 0)
        {
            Console.WriteLine("No cars added yet.");
            return;
        }

        foreach (var car in cars)
        {
            car.Display();
        }
    }

    static void EditCar()
    {
        Console.WriteLine("\nEditing Car:");

        if (cars.Count == 0)
        {
            Console.WriteLine("No cars added yet.");
            return;
        }

        ViewCars();

        try
        {
            Console.WriteLine("Enter the details of the car you want to edit:");
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
                Console.WriteLine("Car not found.");
                return;
            }

            Console.WriteLine("Enter the new details:");

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

            Console.WriteLine("Car edited successfully!");
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
}
