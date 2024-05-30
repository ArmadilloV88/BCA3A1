-- Drop EmployeeProduct Table
IF OBJECT_ID('EmployeeProduct', 'U') IS NOT NULL
DROP TABLE EmployeeProduct;

-- Drop Favorites Table
IF OBJECT_ID('Favorites', 'U') IS NOT NULL
DROP TABLE Favorites;

-- Drop FarmerEmployee Table
IF OBJECT_ID('FarmerEmployee', 'U') IS NOT NULL
DROP TABLE FarmerEmployee;

-- Drop Products Table
IF OBJECT_ID('Products', 'U') IS NOT NULL
DROP TABLE Products;

-- Drop NewsFeed Table
IF OBJECT_ID('NewsFeed', 'U') IS NOT NULL
DROP TABLE NewsFeed;

-- Drop Employee Table
IF OBJECT_ID('Employee', 'U') IS NOT NULL
DROP TABLE Employee;

-- Drop Farmers Table
IF OBJECT_ID('Farmers', 'U') IS NOT NULL
DROP TABLE Farmers;

-- Drop Users Table
IF OBJECT_ID('Users', 'U') IS NOT NULL
DROP TABLE Users;

-- Drop Tag Table
IF OBJECT_ID('Tag', 'U') IS NOT NULL
DROP TABLE Tag;
