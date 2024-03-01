from asyncio.windows_events import NULL
import socket
import uuid
import requests
import platform
import datetime
import psutil
import os
import time
import pyodbc

# Establish a connection to the database
connection_string = 'Driver={ODBC Driver 17 for SQL Server};Server=ALPHA\SQLEXPRESS;Database=AVA Local;Trusted_Connection=yes;'
# Global variable to track database status
ActiveDB = False

# Operating System Information
os_info = platform.uname()
GREEN = '\033[32m'
RESET = '\033[0m'
RED = '\033[91m'
YELLOW = '\033[33m'

# System Hardware Information
cpu_info = platform.processor()
memory_info = psutil.virtual_memory()
disk_info = psutil.disk_usage('/')

# System Time
current_time = datetime.datetime.now()

# System Uptime
uptime = psutil.boot_time()

# Installed Software
installed_software = [process.name() for process in psutil.process_iter()]

# System Environment Variables
env_vars = dict(os.environ)

def get_host_ip_address():
    try:
        # Get the IP address of the default network interface
        ip_address = socket.gethostbyname(socket.gethostname())
        return ip_address
    except Exception as e:
        print("Error:", e)
        return None

def get_host_name():
    try:
        # Get the hostname
        host_name = socket.gethostname()
        return host_name
    except Exception as e:
        print("Error:", e)
        return None

def get_mac_address():
    try:
        # Get the MAC address of the default network interface
        mac_address = ':'.join(['{:02x}'.format((uuid.getnode() >> elements) & 0xff) for elements in range(0,2*6,2)][::-1])
        return mac_address
    except Exception as e:
        print("Error:", e)
        return None

def get_public_ip():
    try:
        # Make a request to a service that echoes back your public IP address
        response = requests.get('https://api.ipify.org')
        if response.status_code == 200:
            return response.text
        else:
            print("Failed to retrieve public IP address. Status code:", response.status_code)
            return None
    except Exception as e:
        print("Error:", e)
        return None
    
def test_connection(connection_string):
    global ActiveDB  # Declare ActiveDB as global
    try:
        connection = pyodbc.connect(connection_string)
        ActiveDB = True
        print(GREEN + "Connection successful. Database is active and reachable.")
        connection.close()
        time.sleep(2)
    except Exception as e:
        ActiveDB = False
        print(RED + "Connection failed. Please check your connection string and database configuration.")
        print("Error:", e)
        time.sleep(2)
        
def loadscreen():
    print(RESET + " ------------------------------------------------------")
    print("|       AAA        VV             VV        AAA        | AVA BUILD        :  V1")
    print("|      AA AA        VV           VV        AA AA       | Application type :  Console")
    print("|     AA   AA        VV         VV        AA   AA      | Device name      : ", get_host_name())
    print("|    AA     AA        VV       VV        AA     AA     | Device IPv4 int  : ", get_host_ip_address())
    print("|   AAAAAAAAAAA        VV     VV        AAAAAAAAAAA    | Device IPv4 out  : ", get_public_ip())
    print("|  AA         AA        VV   VV        AA         AA   | Device MAC       : ", get_mac_address())
    print("| AA           AA        VV VV        AA           AA  | Current Time     : ", current_time)
    print("|AA             AA        VVV        AA             AA |")
    print("|------------------------------------------------------|")
    print("|           ADVANCED VALNERABILITY ACCESSOR            |")
    print(" ------------------------------------------------------")

def disclaimer():
    print(" ----------")
    print("|DISCLAIMER|")
    print(" ----------")
    print("Hello user and welcome to my back end console access point.\nI have gathered the details and processes of your machine\nfor legal purposes and for ethnicity, this doesnt mean \nI have control of your device it is just used for monitoring ")
    
def DeveloperMSG():
    print(" -----------------")
    print("|Developer Message|")
    print(" -----------------")
    print("Hello Developer, AVA systems are active but limited")

def Menu():
    print(" ----")
    print("|Menu|")
    print(" ----")
    print("1. Login to AVA")
    print("2. Register to AVA")
    print("3. View AVA information")
    print("4. Exit AVA")
    print("Please select an option")
    
def LoginToAVA(macAddress, bypass):
    print("Login")
       
def RegisterToAVA(macAddress, bypass):
    print("Register to AVA")
    
def AVAInfo():
    print("AVA Info")
     
def ExitAVA():
    print("Exit to AVA")
    
def Decision(macAddress, bypass):
     try:
         user_input = int(input("Please select a number: "))
         if user_input >= 1 and user_input <= 4:
            if user_input == 1:
               LoginToAVA(macAddress, bypass)
            elif user_input == 2:
               RegisterToAVA(macAddress, bypass)
            elif user_input == 3:
               AVAInfo()
            elif user_input == 4:
               ExitAVA()
            else:
               print("Error: Please enter a valid number.")
         else:
              print("Error: Please enter a valid number.")
     except ValueError:
          print("Error: Please enter a valid number.")
          
def Init():
    # Print initial information 
     print("obtaining information, please wait:")
     time.sleep(2)
     print("Operating System Information:", os_info)
     time.sleep(1)
     print("CPU Information:", cpu_info)
     time.sleep(1)
     print("Memory Information:", memory_info)
     time.sleep(1)
     print("Disk Usage:", disk_info)
     time.sleep(2)
     print("Current Time:", current_time)
     time.sleep(1)
     print("System Uptime:", uptime)
     time.sleep(1)
     print("Installed Software:", installed_software)
     time.sleep(1)
     print("System Environment Variables:", env_vars)
     time.sleep(2)
     print(GREEN + "obtaining complete")
     time.sleep(2)
     print(RESET + "Testing database connection, please wait:")
     time.sleep(5)
     test_connection(connection_string)
    
Init()
# Clear the screen
print("\033[H\033[J")  # ANSI escape code to clear the screen
# Depending on the database status, wait for a few seconds
macAddress = get_mac_address()
bypass = False
publicIP = get_public_ip()

# Check if publicIP is None (NULL is not a valid Python keyword)
if publicIP is None:
    print(RED + "AVA Developer Bypass granted")

    # You don't need to check publicIP again in the elif condition
    if macAddress == "a3:8c:33:cf:3e:fb":
        print("\033[H\033[J")  
        print(YELLOW + "AVA Developer Bypass granted however limited due to no internet access")
        bypass = True
        time.sleep(2)
        loadscreen()
        time.sleep(2)
        DeveloperMSG()  
        time.sleep(2)
        Menu()
        Decision(macAddress, bypass)
elif not ActiveDB and macAddress == "a3:8c:33:cf:3e:fb":
    time.sleep(5)
    print("\033[H\033[J")  
    print(YELLOW + "AVA Developer Bypass granted")
    bypass = True
    time.sleep(2)
    loadscreen()
    time.sleep(2)
    DeveloperMSG()  
    time.sleep(2)
    Menu()
    Decision(macAddress, bypass)
elif not ActiveDB:
    # Add code for the second condition
    time.sleep(5)
    print("\033[H\033[J")  # ANSI escape code to clear the screen
    print(RED + "AVA not initialized")
    time.sleep(2)
    loadscreen()
    pass  # Not sure what you want to do here
else:
    time.sleep(3)
    print("\033[H\033[J")  # ANSI escape code to clear the screen
    print(GREEN + "Initializing AVA...")
    time.sleep(2)
    print("\033[H\033[J")  # ANSI escape code to clear the screen
    loadscreen()
    time.sleep(2)
    disclaimer()
    time.sleep(2)
    Menu()
    Decision(macAddress, bypass)