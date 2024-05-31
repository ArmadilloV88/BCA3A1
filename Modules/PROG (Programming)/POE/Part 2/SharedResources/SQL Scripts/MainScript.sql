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

INSERT INTO Users (Username, Password, TagID, Name, Surname, Age, Email, Gender)
VALUES 
('JoeBoe234', 'J0eB03!@#', 1, 'Joe', 'Boe', 25, 'joe.boe@example.com', 'Male'),
('MarySmith123', 'M@rY123!@#', 1, 'Mary', 'Smith', 30, 'mary.smith@example.com', 'Female'),
('JohnDoe456', 'J0hnD0e!@#', 2, 'John', 'Doe', 35, 'john.doe@example.com', 'Male'),
('EmilyBrown789', 'Em1lyBr0wn!@#', 2, 'Emily', 'Brown', 40, 'emily.brown@example.com', 'Female'),
('SarahJohnson234', 'S@r@hJ0hn!@#', 1, 'Sarah', 'Johnson', 28, 'sarah.johnson@example.com', 'Female'),
('DavidWilson567', 'D@v1dW1ls0n!@#', 1, 'David', 'Wilson', 33, 'david.wilson@example.com', 'Male'),
('JessicaLee890', 'J3ss!c@L33!@#', 2, 'Jessica', 'Lee', 38, 'jessica.lee@example.com', 'Female'),
('MichaelThompson123', 'M1ch@3lT@0mp!@#', 2, 'Michael', 'Thompson', 42, 'michael.thompson@example.com', 'Male'),
('AnnaKlein789', 'AnN@Kl3in789!@#', 1, 'Anna', 'Klein', 29, 'anna.klein@example.com', 'Female'),
('PeterWong456', 'P3t3rW0ng456!@#', 1, 'Peter', 'Wong', 31, 'peter.wong@example.com', 'Male'),
('SophieTaylor123', 'S0ph13T@yl0r!@#', 2, 'Sophie', 'Taylor', 37, 'sophie.taylor@example.com', 'Female'),
('JackSmith789', 'J@ckSm1th789!@#', 2, 'Jack', 'Smith', 36, 'jack.smith@example.com', 'Male');

-- Create Favourites table
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
INSERT INTO Employee (UserID)
VALUES 
((SELECT UserID FROM Users WHERE Username = 'JoeBoe234')),
((SELECT UserID FROM Users WHERE Username = 'MarySmith123')),
((SELECT UserID FROM Users WHERE Username = 'DavidWilson567')),
((SELECT UserID FROM Users WHERE Username = 'SarahJohnson234')),
((SELECT UserID FROM Users WHERE Username = 'AnnaKlein789')),
((SELECT UserID FROM Users WHERE Username = 'PeterWong456'));


-- Create Farmers Table, used to store the Farmer user link per user 
CREATE TABLE Farmers (
    FarmerID INT PRIMARY KEY IDENTITY,
    UserID INT NOT NULL,
    CONSTRAINT FK_Farmer_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Insert predefined Farmers
INSERT INTO Farmers (UserID)
VALUES 
((SELECT UserID FROM Users WHERE Username = 'JohnDoe456')),
((SELECT UserID FROM Users WHERE Username = 'EmilyBrown789')),
((SELECT UserID FROM Users WHERE Username = 'JessicaLee890')),
((SELECT UserID FROM Users WHERE Username = 'MichaelThompson123')),
((SELECT UserID FROM Users WHERE Username = 'SophieTaylor123')),
((SELECT UserID FROM Users WHERE Username = 'JackSmith789'));

-- Create Products Table, used to store the products data
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
INSERT INTO Products (ProductName, ProductDescription, ProductCategory, ProductDate, FarmerID)
VALUES 
('Red Bell Peppers', 'Fresh red bell peppers', 'Vegetable', '2023-05-01', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'JohnDoe456'))),
('Gala Apples', 'Sweet and crisp gala apples', 'Fruit', '2023-04-15', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'EmilyBrown789'))),
('Romaine Lettuce', 'Crisp romaine lettuce', 'Vegetable', '2023-05-10', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'JessicaLee890'))),
('Baby Carrots', 'Tender baby carrots', 'Vegetable', '2023-05-20', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'MichaelThompson123'))),
('Strawberries', 'Juicy and sweet strawberries', 'Fruit', '2023-05-25', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'SophieTaylor123'))),
('Butternut Squash', 'Nutty and sweet butternut squash', 'Vegetable', '2023-06-01', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'JackSmith789'))),
('Navel Oranges', 'Juicy and seedless navel oranges', 'Fruit', '2023-06-10', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'JohnDoe456'))),
('Yukon Gold Potatoes', 'Creamy Yukon gold potatoes', 'Vegetable', '2023-06-15', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'EmilyBrown789'))),
('Concord Grapes', 'Sweet and tart concord grapes', 'Fruit', '2023-06-20', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'JessicaLee890'))),
('Green Beans', 'Fresh and crunchy green beans', 'Vegetable', '2023-06-25', (SELECT FarmerID FROM Farmers WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'MichaelThompson123')));


CREATE TABLE EmployeeProduct (
    EmployeeProductID INT PRIMARY KEY IDENTITY,
    EmployeeID INT NOT NULL,
    ProductID INT NOT NULL,
    CONSTRAINT FK_EmployeeProduct_Employee FOREIGN KEY (EmployeeID) REFERENCES Users(UserID),
    CONSTRAINT FK_EmployeeProduct_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
-- Create NewsFeed Table, used to store news data
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
