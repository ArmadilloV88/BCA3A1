using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
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
            int[] arr = new int[itt];
            double[] totalarray = new double[10];
            string place = "";
            string line = "";
            int[] it = new int[10];
            int[] grid = new int[] {1, 2, 8, 16, 32, 64, 128, 256, 512, 1024};
            for (int y = 0; y < 10; y++) //longitutde
            {
                
              for (int i = 0; i < itt; i++) //latitude
              {
                    int num = rnd.Next(0, 2);
                    arr[i] = num;
                    if (num == 0)
                    {

                    }
                    else
                    {
                        total = total + grid[i];
                        totalarray[y] = totalarray[y] + grid[i];
                    }
                    if (num == 0){
                        it[0]++;
                    }else if (num == 1)
                    {
                        it[1]++;
                    }
                    else if (num == 2)
                    {
                        it[2]++;
                    }
                    else if (num == 3)
                    {
                        it[3]++;
                    }
                    else if (num == 4)
                    {
                        it[4]++;
                    }
                    else if (num == 5)
                    {
                        it[5]++;
                    }
                    else if (num == 6)
                    {
                        it[6]++;
                    }
                    else if (num == 7)
                    {
                        it[7]++;
                    }
                    else if (num == 8)
                    {
                        it[8]++;
                    }
                    else if (num == 9)
                    {
                        it[9]++;
                    }
                    else
                    {

                    }
                    //System.Threading.Thread.Sleep(1000);
                    place = "[" + arr[i] + "]";
                    Console.Write("|"+place+"|");
              }
                Console.Write(" = " + totalarray[y]);
                place = "";
                Console.WriteLine("");
            }
            int counter = 0;
            for (int i = 0; i < itt; i++)
            {
                counter++;  
                line = line +"=====";
            }
            Console.WriteLine(line);
            if (counter >=1)
            {
                Console.WriteLine("itterations|");
                Console.WriteLine("===========");
            }
            else
            {
                Console.WriteLine("itterations");
                Console.WriteLine("===========");
            }
            
            Console.WriteLine("[0] = " + it[0]);
            Console.WriteLine("[1] = " + it[1]);
            Console.WriteLine("Total is : "+total);
        Console.ReadKey();
        }
    }
}
