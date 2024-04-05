using System;

namespace ST10091991ICE3
{
    internal class PopQuizQuestion : Program
    {
        public static void MakeCar()
        {
            Console.Clear();
            MotorCar car = new MotorCar();
            car.Brand = GetStringInput("Enter car brand: ");
            car.Type = GetStringInput("Enter car type: ");
            Engine engine = new Engine();
            engine.VIN = GetStringInput("Enter VIN number: ");
            engine.CC = GetIntInput("Enter engine CC: ");
            engine.Capacity = GetStringInput("Enter engine capacity: ");
            engine.BatteryNm = GetIntInput("Enter battery Nm: ");
            car.Engine = engine;
            Person Driver = new Person();
            Driver.Name = GetStringInput("Enter driver name");
            Driver.Surname = GetStringInput("Enter driver surname");
            Driver.Age = GetIntInput("Enter driver age");
        }
        // Helper method to get string input from the console
        private static string GetStringInput(string message)
        {
            Console.Write(message);
            return Console.ReadLine();
        }
        // Helper method to get integer input from the console
        private static int GetIntInput(string message)
        {
            Console.Write(message);
            int result;
            while (!int.TryParse(Console.ReadLine(), out result))
            {
                Console.WriteLine("Invalid input. Please enter a valid integer.");
                Console.Write(message);
            }
            return result;
        }
    }
    internal class Person : PopQuizQuestion
    {
        public string? Name { get; set; }
        public string? Surname { get; set; }
        public int? Age { get; set; }
    }
    internal class Engine : MotorCar
    {
        public string? VIN { get; set; }
        public int? CC { get; set; }
        public string? Capacity { get; set; }
        public int? BatteryNm { get; set; }
    }
    internal class MotorCar : PopQuizQuestion
    {
        public string? Brand { get; set; }
        public string? Type { get; set; }
        public Engine Engine { get; set; }
    }
}