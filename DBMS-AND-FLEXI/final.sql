-- ========================
-- RESET DATABASE
-- ========================
DROP DATABASE IF EXISTS multi_tenant_inventory;
CREATE DATABASE multi_tenant_inventory;
USE multi_tenant_inventory;

-- ========================
-- TABLE CREATION
-- ========================

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
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    Status VARCHAR(50) DEFAULT 'Active',
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

CREATE TABLE Supplier (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    CompanyName VARCHAR(100) NOT NULL,
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
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
    TotalAmount DECIMAL(10,2) CHECK (TotalAmount >= 0),
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

-- ========================
-- INSERT DATA
-- ========================

-- Shops
INSERT INTO Shop (ShopName, City)
VALUES 
('Tech World', 'Hyderabad'),
('Fresh Mart', 'Mumbai');

-- Persons
INSERT INTO Person (FirstName, LastName, Email, PhoneNo, City)
VALUES
('Ravi', 'Kumar', 'ravi@mail.com', '9001', 'Hyderabad'),
('Amit', 'Sharma', 'amit@mail.com', '9002', 'Mumbai'),
('Sita', 'Reddy', 'sita@mail.com', '9003', 'Hyderabad'),
('Raj', 'Mehta', 'raj@mail.com', '9004', 'Mumbai'),
('Supplier1', 'Global', 'sup1@mail.com', '9005', 'Hyderabad'),
('Supplier2', 'Metro', 'sup2@mail.com', '9006', 'Mumbai');

-- Users
INSERT INTO User (PersonID, ShopID)
VALUES (1,1), (2,2);

-- Customers
INSERT INTO Customer (PersonID, ShopID, Status)
VALUES (3,1,'Active'), (4,2,'Active');

-- Suppliers
INSERT INTO Supplier (PersonID, ShopID, CompanyName)
VALUES (5,1,'Global Pvt Ltd'), (6,2,'Metro Ltd');

-- Category
INSERT INTO Category (ShopID, Name)
VALUES (1,'Electronics'), (2,'Groceries');

-- Products
INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity)
VALUES
(1,1,'Laptop',40000,45000,10),
(1,1,'Mobile',15000,18000,20),
(2,2,'Rice',400,500,50),
(2,2,'Oil',120,150,100);

-- Invoice
INSERT INTO Invoice (ShopID, CustomerID, TotalAmount)
VALUES (1,1,45000), (2,2,500);

-- Invoice Items
INSERT INTO InvoiceItem (InvoiceID, ProductID, Quantity, Subtotal)
VALUES
(1,1,1,45000),
(2,3,1,500);

-- ========================
-- UPDATE
-- ========================
UPDATE Product
SET Quantity = Quantity - 1
WHERE ProductID = 1;

-- ========================
-- DELETE
-- ========================
DELETE FROM Customer
WHERE CustomerID = 2;

-- ========================
-- SELECT QUERIES
-- ========================

-- 1. All Products
SELECT * FROM Product;

-- 2. Customer Details
SELECT c.CustomerID, p.FirstName, s.ShopName
FROM Customer c
JOIN Person p ON c.PersonID = p.PersonID
JOIN Shop s ON c.ShopID = s.ShopID;

-- 3. Invoice Details
SELECT i.InvoiceID, pr.Name, ii.Quantity, ii.Subtotal
FROM Invoice i
JOIN InvoiceItem ii ON i.InvoiceID = ii.InvoiceID
JOIN Product pr ON ii.ProductID = pr.ProductID;

-- 4. Products per Shop
SELECT s.ShopName, p.Name, p.Quantity
FROM Product p
JOIN Shop s ON p.ShopID = s.ShopID;

-- 5. Supplier Info
SELECT sp.CompanyName, p.FirstName, s.ShopName
FROM Supplier sp
JOIN Person p ON sp.PersonID = p.PersonID
JOIN Shop s ON sp.ShopID = s.ShopID;