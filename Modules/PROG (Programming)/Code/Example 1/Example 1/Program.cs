using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Example_1
{
    internal class Program
    {
        static void Main(string[] args)
        {
            try
            {
                int linecounter = 1;
                for (int i = 0; i < 100; i++)
                {
                    string LineFinal = LineAssemblyright(linecounter);
                    Console.WriteLine(LineFinal);
                        LineFinal = null;
                        LineFinal = LineAssemblyleft(linecounter);
                    Console.WriteLine(LineFinal);
                    linecounter++;
                }
            }
            catch
            {

            }
            Console.ReadKey();
        }
        static string LineAssemblyright(int line)
        {
            string defaultline = "\\";
            string assembled = "";
            for (int i = 0; i < line; i++)
            {
                assembled += defaultline;
            }

            return assembled;
        }
        static string LineAssemblyleft (int line)
        {
            string defaultline = "/";
            string assembled = "";
            for (int i = 0; i < line; i++)
            {
                assembled += defaultline;
            }

            return assembled;
        }
    }
}
