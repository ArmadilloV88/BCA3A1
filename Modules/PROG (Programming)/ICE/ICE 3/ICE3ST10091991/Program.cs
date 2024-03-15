namespace ICE3ST10091991
{
    internal class Program
    {
        static void Main(string[] args)
        {
            bool stop = false;
            
            do
            {
                bool initialized = Initializer.Init();
            } while (stop == false);
        }
    }
}
