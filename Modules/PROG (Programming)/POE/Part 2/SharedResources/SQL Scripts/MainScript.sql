-- Create Tag Table, used to store static tag data
CREATE TABLE Tag (
    TagID INT PRIMARY KEY IDENTITY,
    Description NVARCHAR(50) NOT NULL
);

-- Insert predefined Tag values
INSERT INTO Tag (Description) VALUES ('Employee'), ('Farmer');

-- Create Users Table, used to store user profiles
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY,
    Username NVARCHAR(50) NOT NULL,
    Password NVARCHAR(50) NOT NULL,
    TagID INT NOT NULL,
    Name NVARCHAR(50),
    Surname NVARCHAR(50),
    Age INT,
    Email NVARCHAR(100),
    Gender NVARCHAR(10),
    CONSTRAINT FK_Users_Tag FOREIGN KEY (TagID) REFERENCES Tag(TagID)
);

-- Insert predefined Users with new data
INSERT INTO Users (Username, Password, TagID, Name, Surname, Age, Email, Gender)
VALUES 
('employee1', 'password1', (SELECT TagID FROM Tag WHERE Description = 'Employee'), 'John', 'Doe', 30, 'john.doe@example.com', 'Male'),
('employee2', 'password2', (SELECT TagID FROM Tag WHERE Description = 'Employee'), 'Jane', 'Smith', 28, 'jane.smith@example.com', 'Female'),
('farmer1', 'password1', (SELECT TagID FROM Tag WHERE Description = 'Farmer'), 'Michael', 'Johnson', 45, 'michael.johnson@example.com', 'Male'),
('farmer2', 'password2', (SELECT TagID FROM Tag WHERE Description = 'Farmer'), 'Emily', 'Brown', 35, 'emily.brown@example.com', 'Female'),
('farmer3', 'password3', (SELECT TagID FROM Tag WHERE Description = 'Farmer'), 'David', 'Martinez', 50, 'david.martinez@example.com', 'Male'),
('farmer4', 'password4', (SELECT TagID FROM Tag WHERE Description = 'Farmer'), 'Sophia', 'Lee', 40, 'sophia.lee@example.com', 'Female');

CREATE TABLE Favorites (
    FavoriteID INT PRIMARY KEY IDENTITY,
    EmployeeID INT NOT NULL,
    FarmerID INT NOT NULL,
    CONSTRAINT FK_Favorites_Employee FOREIGN KEY (EmployeeID) REFERENCES Users(UserID),
    CONSTRAINT FK_Favorites_Farmer FOREIGN KEY (FarmerID) REFERENCES Users(UserID)
);

-- Create Employee Table, used to store the employee user link per user
CREATE TABLE Employee (
    EmployeeID INT PRIMARY KEY IDENTITY,
    UserID INT NOT NULL,
    CONSTRAINT FK_Employee_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Insert predefined Employees
INSERT INTO Employee (UserID) VALUES 
((SELECT UserID FROM Users WHERE Username = 'employee1')),
((SELECT UserID FROM Users WHERE Username = 'employee2'));

-- Create Farmers Table, used to store the Farmer user link per user 
CREATE TABLE Farmers (
    FarmerID INT PRIMARY KEY IDENTITY,
    UserID INT NOT NULL,
    CONSTRAINT FK_Farmer_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Insert predefined Farmers. 
INSERT INTO Farmers (UserID) VALUES 
((SELECT UserID FROM Users WHERE Username = 'farmer1')),
((SELECT UserID FROM Users WHERE Username = 'farmer2')),
((SELECT UserID FROM Users WHERE Username = 'farmer3')),
((SELECT UserID FROM Users WHERE Username = 'farmer4'));

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

CREATE TABLE EmployeeProduct (
    EmployeeProductID INT PRIMARY KEY IDENTITY,
    EmployeeID INT NOT NULL,
    ProductID INT NOT NULL,
    CONSTRAINT FK_EmployeeProduct_Employee FOREIGN KEY (EmployeeID) REFERENCES Users(UserID),
    CONSTRAINT FK_EmployeeProduct_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Insert predefined Products
INSERT INTO Products (ProductName, ProductDescription, ProductCategory, ProductDate, FarmerID) VALUES 
('Tomatoes', 'Fresh organic tomatoes', 'Vegetable', '2023-05-01', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer1'))),
('Apples', 'Red juicy apples', 'Fruit', '2023-04-15', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer2'))),
('Lettuce', 'Crisp green lettuce', 'Vegetable', '2023-05-10', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer1'))),
('Carrots', 'Sweet and crunchy carrots', 'Vegetable', '2023-05-20', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer2'))),
('Blueberries', 'Fresh and sweet blueberries', 'Fruit', '2023-05-25', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer3'))),
('Pumpkins', 'Large and ripe pumpkins', 'Vegetable', '2023-06-01', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer3'))),
('Oranges', 'Juicy and tangy oranges', 'Fruit', '2023-06-10', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer4'))),
('Potatoes', 'Starchy and versatile potatoes', 'Vegetable', '2023-06-15', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer4'))),
('Grapes', 'Seedless and sweet grapes', 'Fruit', '2023-06-20', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer1'))),
('Cabbage', 'Fresh green cabbage', 'Vegetable', '2023-06-25', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'farmer2')));

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
('Farmers Market Event', '2023-05-10', '2023-06-10'),
('Harvest Festival', '2023-06-01', '2023-07-01'),
('Sustainable Farming Workshop', '2023-05-20', '2023-06-20'),
('Local Produce Fair', '2023-06-15', '2023-07-15');
