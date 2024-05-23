-- Create Tag Table
CREATE TABLE Tag (
    TagID INT PRIMARY KEY IDENTITY,
    Description NVARCHAR(50) NOT NULL
);

-- Insert predefined Tag values
INSERT INTO Tag (Description) VALUES ('Employee'), ('Farmer');

-- Create Users Table
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY,
    Username NVARCHAR(50) NOT NULL,
    Password NVARCHAR(50) NOT NULL,
    TagID INT NOT NULL,
    CONSTRAINT FK_Users_Tag FOREIGN KEY (TagID) REFERENCES Tag(TagID)
);

-- Insert predefined Users
INSERT INTO Users (Username, Password, TagID) VALUES 
('employee1', 'password1', (SELECT TagID FROM Tag WHERE Description = 'Employee')),
('farmer1', 'password1', (SELECT TagID FROM Tag WHERE Description = 'Farmer')),
('farmer2', 'password2', (SELECT TagID FROM Tag WHERE Description = 'Farmer'));

-- Create Employee Table
CREATE TABLE Employee (
    EmployeeID INT PRIMARY KEY IDENTITY,
    UserID INT NOT NULL,
    CONSTRAINT FK_Employee_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Insert predefined Employees
INSERT INTO Employee (UserID) VALUES 
((SELECT UserID FROM Users WHERE Username = 'employee1'));

-- Create Farmers Table
CREATE TABLE Farmers (
    FarmerID INT PRIMARY KEY IDENTITY,
    UserID INT NOT NULL,
    CONSTRAINT FK_Farmer_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Insert predefined Farmers
INSERT INTO Farmers (UserID) VALUES 
((SELECT UserID FROM Users WHERE Username = 'farmer1')),
((SELECT UserID FROM Users WHERE Username = 'farmer2'));

-- Create FarmerEmployee Table
CREATE TABLE FarmerEmployee (
    FarmerEmployeeID INT PRIMARY KEY IDENTITY,
    FarmerID INT NOT NULL,
    EmployeeID INT NOT NULL,
    CONSTRAINT FK_FarmerEmployee_Farmer FOREIGN KEY (FarmerID) REFERENCES Farmers(FarmerID),
    CONSTRAINT FK_FarmerEmployee_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Create Products Table
CREATE TABLE Products (
    ProductID INT PRIMARY KEY IDENTITY,
    ProductName NVARCHAR(100) NOT NULL,
    ProductDescription NVARCHAR(255),
    ProductCategory NVARCHAR(50),
    ProductDate DATE,
    FarmerID INT NOT NULL,
    CONSTRAINT FK_Products_Farmer FOREIGN KEY (FarmerID) REFERENCES Farmers(FarmerID)
);

-- Insert predefined Products
INSERT INTO Products (ProductName, ProductDescription, ProductCategory, ProductDate, FarmerID) VALUES 
('Tomatoes', 'Fresh organic tomatoes', 'Vegetable', '2023-05-01', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer1'))),
('Apples', 'Red juicy apples', 'Fruit', '2023-04-15', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer2'))),
('Lettuce', 'Crisp green lettuce', 'Vegetable', '2023-05-10', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer1')));

-- Create NewsFeed Table
CREATE TABLE NewsFeed (
    NewsFeedID INT PRIMARY KEY IDENTITY,
    NewsFeedName NVARCHAR(100) NOT NULL,
    NewsFeedDate DATE NOT NULL,
    NewsFeedDateExp DATE NOT NULL
);

-- Insert predefined NewsFeed entries
INSERT INTO NewsFeed (NewsFeedName, NewsFeedDate, NewsFeedDateExp) VALUES 
('Spring Market Opening', '2023-05-01', '2023-06-01'),
('New Organic Certifications', '2023-04-15', '2023-05-15'),
('Farmers Market Event', '2023-05-10', '2023-06-10');
