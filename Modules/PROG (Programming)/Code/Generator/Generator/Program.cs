using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Generator
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Random rnd = new Random();
            int itt = rnd.Next(1, 10);
            int total = 0;

            for (int i = 0; i < itt; i++)
            {
                int num = rnd.Next(1, 10);
                total += num;
            }
        Console.WriteLine("Total is : "+total);
        Console.ReadKey();
        }
    }
}
