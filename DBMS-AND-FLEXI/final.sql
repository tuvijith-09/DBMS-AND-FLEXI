
DROP DATABASE IF EXISTS multi_tenant_inventory;
CREATE DATABASE multi_tenant_inventory;
USE multi_tenant_inventory;

CREATE TABLE Person (
    PersonID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50),
    Email VARCHAR(100) UNIQUE,
    PhoneNo VARCHAR(15),
    City VARCHAR(50)
);

CREATE TABLE Shop (
    ShopID INT AUTO_INCREMENT PRIMARY KEY,
    ShopName VARCHAR(100) NOT NULL,
    City VARCHAR(50)
);

CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    Password VARCHAR(255) NOT NULL DEFAULT 'admin123',
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT,
    ShopID INT,
    Status VARCHAR(50) DEFAULT 'Active',
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    UNIQUE (PersonID, ShopID)
);

CREATE TABLE Supplier (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT,
    ShopID INT,
    CompanyName VARCHAR(100) NOT NULL,
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    UNIQUE (PersonID, ShopID)
);

CREATE TABLE Category (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT,
    Name VARCHAR(100) NOT NULL,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

CREATE TABLE Product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT,
    CategoryID INT,
    Name VARCHAR(100) NOT NULL,
    CostPrice DECIMAL(10,2) CHECK (CostPrice >= 0),
    SellingPrice DECIMAL(10,2) CHECK (SellingPrice >= 0),
    Quantity INT CHECK (Quantity >= 0),
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID) ON DELETE CASCADE
);

CREATE TABLE Invoice (
    InvoiceID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT,
    CustomerID INT,
    TotalAmount DECIMAL(10,2) DEFAULT 0.00 CHECK (TotalAmount >= 0),
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE
);

CREATE TABLE InvoiceItem (
    InvoiceItemID INT AUTO_INCREMENT PRIMARY KEY,
    InvoiceID INT,
    ProductID INT,
    Quantity INT CHECK (Quantity > 0),
    Subtotal DECIMAL(10,2) CHECK (Subtotal >= 0),
    FOREIGN KEY (InvoiceID) REFERENCES Invoice(InvoiceID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);

-- Shops
INSERT INTO Shop (ShopName, City) VALUES 
('Tech World', 'Hyderabad'),
('Fresh Mart', 'Mumbai'),
('Fashion Hub', 'Pune');

-- Persons
INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City) VALUES
('Ravi', 'Kumar', 'ravi@mail.com', '9001', 'Hyderabad'),
('Amit', 'Sharma', 'amit@mail.com', '9002', 'Mumbai'),
('Sita', 'Reddy', 'sita@mail.com', '9003', 'Hyderabad'),
('Raj', 'Mehta', 'raj@mail.com', '9004', 'Mumbai'),
('Supplier1', 'Global', 'sup1@mail.com', '9005', 'Hyderabad'),
('Supplier2', 'Metro', 'sup2@mail.com', '9006', 'Mumbai'),
('Pooja', 'Joshi', 'pooja@mail.com', '9007', 'Pune'),
('Vikram', 'Singh', 'vikram@mail.com', '9008', 'Hyderabad'),
('Neha', 'Gupta', 'neha@mail.com', '9009', 'Mumbai'),
('Rahul', 'Verma', 'rahul@mail.com', '9010', 'Pune'),
('Priya', 'Desai', 'priya@mail.com', '9011', 'Pune'),
('Supplier3', 'StyleCo', 'sup3@mail.com', '9012', 'Pune'),
('Supplier4', 'AllIndia', 'sup4@mail.com', '9013', 'Delhi');

-- Users 
INSERT INTO User (PersonID, ShopID, Password) VALUES 
(1, 1, 'ravi_password'), 
(2, 2, 'amit_password'),
(7, 3, 'pooja_password');

-- Customers
INSERT INTO Customer (PersonID, ShopID, Status) VALUES 
(3, 1, 'Active'), 
(4, 2, 'Active'),
(8, 1, 'Active'),
(9, 2, 'Active'),
(10, 3, 'Active'),
(11, 1, 'Active'),
(11, 2, 'Active');

-- Suppliers
INSERT INTO Supplier (PersonID, ShopID, CompanyName) VALUES 
(5, 1, 'Global Pvt Ltd'), 
(6, 2, 'Metro Ltd'),
(12, 3, 'StyleCo Textiles'),
(13, 1, 'AllIndia Logistics'),
(13, 2, 'AllIndia Logistics');

-- Categories
INSERT INTO Category (ShopID, Name) VALUES 
(1, 'Electronics'), (2, 'Groceries'), (1, 'Accessories'),
(2, 'Dairy'), (3, 'Mens Clothing'), (3, 'Womens Clothing');

-- Products
INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity) VALUES
(1, 1, 'Laptop', 40000, 45000, 10),
(1, 1, 'Mobile', 15000, 18000, 20),
(2, 2, 'Rice', 400, 500, 50),
(2, 2, 'Oil', 120, 150, 100),
(1, 3, 'Bluetooth Headphones', 2000, 2500, 30),
(1, 1, 'SmartWatch', 3000, 4000, 15),
(2, 4, 'Milk (1L)', 50, 60, 100),
(2, 4, 'Butter (500g)', 200, 250, 40),
(3, 5, 'Cotton T-Shirt', 300, 500, 200),
(3, 5, 'Denim Jeans', 800, 1200, 150),
(3, 6, 'Floral Dress', 1000, 1500, 80);

DELIMITER //

-- Trigger 1: Deduct Stock on Sale
CREATE TRIGGER after_invoiceitem_insert
AFTER INSERT ON InvoiceItem
FOR EACH ROW
BEGIN
    UPDATE Product
    SET Quantity = Quantity - NEW.Quantity
    WHERE ProductID = NEW.ProductID;
END //

-- Trigger 2: Sync Invoice TotalAmount
CREATE TRIGGER after_invoiceitem_sync_total
AFTER INSERT ON InvoiceItem
FOR EACH ROW
BEGIN
    DECLARE new_total DECIMAL(10,2);
    SELECT SUM(Subtotal) INTO new_total
    FROM InvoiceItem
    WHERE InvoiceID = NEW.InvoiceID;
    
    UPDATE Invoice
    SET TotalAmount = new_total
    WHERE InvoiceID = NEW.InvoiceID;
END //

DELIMITER ;

-- Create the empty invoice headers first
INSERT INTO Invoice (ShopID, CustomerID, TotalAmount) VALUES 
(1, 1, 0), (2, 2, 0), (1, 3, 0),
(2, 4, 0), (3, 5, 0), (1, 6, 0), (2, 7, 0);

-- Insert items (Triggers will dynamically calculate everything)
INSERT INTO InvoiceItem (InvoiceID, ProductID, Quantity, Subtotal) VALUES
(1, 1, 1, 45000.00), (2, 3, 1, 500.00), (3, 2, 1, 18000.00),
(3, 5, 2, 5000.00), (4, 3, 2, 1000.00), (4, 7, 5, 300.00),
(5, 9, 3, 1500.00), (5, 10, 1, 1200.00), (6, 6, 1, 4000.00),
(7, 8, 2, 500.00), (7, 4, 1, 150.00);

-- FUNCTIONS
DELIMITER //

-- Function 1: Get Total Revenue per Shop
CREATE FUNCTION getTotalRevenue(in_shopid INT) 
RETURNS DECIMAL(12, 2)
READS SQL DATA
BEGIN
    DECLARE total_rev DECIMAL(12, 2);
    SELECT COALESCE(SUM(TotalAmount), 0.00) INTO total_rev
    FROM Invoice
    WHERE ShopID = in_shopid;
    RETURN total_rev;
END //

-- Function 2: Get Total Stock Value per Shop
CREATE FUNCTION getStockValue(in_shopid INT) 
RETURNS DECIMAL(12, 2)
READS SQL DATA
BEGIN
    DECLARE stock_val DECIMAL(12, 2);
    SELECT COALESCE(SUM(CostPrice * Quantity), 0.00) INTO stock_val
    FROM Product
    WHERE ShopID = in_shopid;
    RETURN stock_val;
END //

DELIMITER ;

-- STORED PROCEDURES
DELIMITER //

-- Procedure 1: Get Shop Inventory
CREATE PROCEDURE getShopInventory(IN in_shopname VARCHAR(100))
BEGIN
    SELECT c.Name AS Category, p.Name AS Product, p.SellingPrice, p.Quantity AS Stock
    FROM Product p
    JOIN Category c ON c.CategoryID = p.CategoryID
    JOIN Shop s ON s.ShopID = p.ShopID
    WHERE s.ShopName = in_shopname
    ORDER BY c.Name, p.Name;
END //

-- Procedure 2: Secure Password Update
CREATE PROCEDURE changeUserPassword(IN in_email VARCHAR(100), IN in_newpassword VARCHAR(255))
BEGIN
    DECLARE target_userid INT;
    SELECT u.UserID INTO target_userid
    FROM User u
    JOIN Person p ON p.PersonID = u.PersonID
    WHERE p.Email = in_email LIMIT 1;
    
    IF target_userid IS NOT NULL THEN
        UPDATE User SET Password = in_newpassword WHERE UserID = target_userid;
        SELECT 'Password updated successfully.' AS Message;
    ELSE
        SELECT 'Error: Email not found.' AS Message;
    END IF;
END //

DELIMITER ;

-- PROJECT QUERIES
-- Query 1: List all products with their category and shop name
SELECT s.ShopName, c.Name AS Category, p.Name AS Product, p.CostPrice, p.SellingPrice, p.Quantity
FROM Product p
JOIN Category c ON c.CategoryID = p.CategoryID
JOIN Shop s ON s.ShopID = p.ShopID
ORDER BY s.ShopName, c.Name, p.Name;

-- Query 2: Revenue per shop 
SELECT s.ShopName, COUNT(i.InvoiceID) AS TotalInvoices, SUM(i.TotalAmount) AS TotalRevenue
FROM Invoice i
JOIN Shop s ON s.ShopID = i.ShopID
GROUP BY s.ShopName
ORDER BY TotalRevenue DESC;

-- Query 3: Customer purchase history with product details
SELECT CONCAT(per.FirstName,' ',per.LastName) AS CustomerName, s.ShopName, p.Name AS Product, ii.Quantity, ii.Subtotal
FROM InvoiceItem ii
JOIN Invoice i ON i.InvoiceID = ii.InvoiceID
JOIN Customer cu ON cu.CustomerID = i.CustomerID
JOIN Person per ON per.PersonID = cu.PersonID
JOIN Product p ON p.ProductID = ii.ProductID
JOIN Shop s ON s.ShopID = i.ShopID
ORDER BY CustomerName;

-- Query 4: Profit margin per product
SELECT s.ShopName, c.Name AS Category, p.Name AS Product, p.CostPrice, p.SellingPrice, 
(p.SellingPrice - p.CostPrice) AS UnitProfit, 
ROUND(((p.SellingPrice - p.CostPrice) / p.CostPrice) * 100, 2) AS MarginPct
FROM Product p
JOIN Category c ON c.CategoryID = p.CategoryID
JOIN Shop s ON s.ShopID = p.ShopID
ORDER BY MarginPct DESC;

-- Query 5: Low stock alert (Quantity <= 15)
SELECT s.ShopName, c.Name AS Category, p.Name AS Product, p.Quantity AS StockLeft
FROM Product p
JOIN Category c ON c.CategoryID = p.CategoryID
JOIN Shop s ON s.ShopID = p.ShopID
WHERE p.Quantity <= 15
ORDER BY p.Quantity ASC;
