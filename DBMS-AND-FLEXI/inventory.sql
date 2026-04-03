CREATE DATABASE IF NOT EXISTS multi_tenant_inventory;
USE multi_tenant_inventory;

-- ========================
-- PERSON (SUPER ENTITY)
-- ========================
CREATE TABLE Person (
    PersonID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    MiddleName VARCHAR(50),
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE,
    PhoneNo VARCHAR(15) UNIQUE,
    StreetNo VARCHAR(100),
    City VARCHAR(50),
    PostalCode VARCHAR(10)
);

-- ========================
-- SHOP
-- ========================
CREATE TABLE Shop (
    ShopID INT AUTO_INCREMENT PRIMARY KEY,
    ShopName VARCHAR(100) NOT NULL,
    City VARCHAR(50),
    PostalCode VARCHAR(10)
);

-- ========================
-- USER (Subclass of Person)
-- ========================
CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    IsVerified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

-- ========================
-- CUSTOMER (Subclass)
-- ========================
CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    Status VARCHAR(50),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

-- ========================
-- SUPPLIER (Subclass)
-- ========================
CREATE TABLE Supplier (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    PersonID INT UNIQUE,
    ShopID INT,
    TaxID VARCHAR(50),
    CompanyName VARCHAR(100),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID) ON DELETE CASCADE,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

-- ========================
-- CATEGORY
-- ========================
CREATE TABLE Category (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

-- ========================
-- PRODUCT
-- ========================
CREATE TABLE Product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    CategoryID INT,
    Name VARCHAR(100) NOT NULL,
    CostPrice DECIMAL(10,2) NOT NULL,
    SellingPrice DECIMAL(10,2) NOT NULL,
    Quantity INT DEFAULT 0,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID) ON DELETE SET NULL
);

-- ========================
-- INVOICE
-- ========================
CREATE TABLE Invoice (
    InvoiceID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    CustomerID INT,
    Date DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(10,2),
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE SET NULL
);

-- ========================
-- INVOICE ITEM
-- ========================
CREATE TABLE InvoiceItem (
    InvoiceItemID INT AUTO_INCREMENT PRIMARY KEY,
    InvoiceID INT,
    ProductID INT,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2),
    FOREIGN KEY (InvoiceID) REFERENCES Invoice(InvoiceID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);

-- ========================
-- FINANCIAL TRANSACTION (SUPER ENTITY)
-- ========================
CREATE TABLE FinancialTransaction (
    FinancialTransactionID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    Date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE
);

-- ========================
-- PAYMENT (Subclass)
-- ========================
CREATE TABLE Payment (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    FinancialTransactionID INT UNIQUE,
    PaymentMethod ENUM('RTGS','NEFT','NetBanking','Cash','UPI'),
    ReferenceNo VARCHAR(100),
    Remarks TEXT,
    FOREIGN KEY (FinancialTransactionID)
        REFERENCES FinancialTransaction(FinancialTransactionID)
        ON DELETE CASCADE
);

-- ========================
-- EXPENSE (Subclass)
-- ========================
CREATE TABLE Expense (
    ExpenseID INT AUTO_INCREMENT PRIMARY KEY,
    FinancialTransactionID INT UNIQUE,
    PaidTo VARCHAR(100),
    Category VARCHAR(100),
    FOREIGN KEY (FinancialTransactionID)
        REFERENCES FinancialTransaction(FinancialTransactionID)
        ON DELETE CASCADE
);

-- ========================
-- PURCHASE ORDER
-- ========================
CREATE TABLE PurchaseOrder (
    PurchaseOrderID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    SupplierID INT,
    Date DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(10,2),
    FOREIGN KEY (ShopID) REFERENCES Shop(ShopID) ON DELETE CASCADE,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID) ON DELETE SET NULL
);

-- ========================
-- PURCHASE ORDER ITEM
-- ========================
CREATE TABLE PurchaseOrderItem (
    POItemID INT AUTO_INCREMENT PRIMARY KEY,
    PurchaseOrderID INT,
    ProductID INT,
    Quantity INT,
    CostPrice DECIMAL(10,2),
    Subtotal DECIMAL(10,2),
    FOREIGN KEY (PurchaseOrderID)
        REFERENCES PurchaseOrder(PurchaseOrderID)
        ON DELETE CASCADE,
    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);
USE multi_tenant_inventory;

-- ==============================
-- 1️⃣ INSERT SHOPS
-- ==============================
INSERT INTO Shop (ShopName, City, PostalCode)
VALUES 
('Tech World', 'Hyderabad', '500001'),
('Fresh Mart', 'Mumbai', '400001');

-- ==============================
-- 2️⃣ INSERT PERSONS (Shop Owners)
-- ==============================
INSERT INTO Person (FirstName, LastName, Email, PhoneNo, StreetNo, City, PostalCode)
VALUES
('Ravi', 'Kumar', 'ravi@techworld.com', '9000000001', '12A', 'Hyderabad', '500001'),
('Amit', 'Sharma', 'amit@freshmart.com', '9000000002', '45B', 'Mumbai', '400001');

-- ==============================
-- 3️⃣ INSERT USERS (Owners)
-- ==============================
INSERT INTO User (PersonID, ShopID, IsVerified)
VALUES
(1, 1, TRUE),
(2, 2, TRUE);

-- ==============================
-- 4️⃣ INSERT CUSTOMERS
-- ==============================
INSERT INTO Person (FirstName, LastName, Email, PhoneNo, StreetNo, City, PostalCode)
VALUES
('Sita', 'Reddy', 'sita@gmail.com', '9000000010', '22C', 'Hyderabad', '500002'),
('Raj', 'Mehta', 'raj@gmail.com', '9000000011', '78D', 'Mumbai', '400002');

INSERT INTO Customer (PersonID, ShopID, Status)
VALUES
(3, 1, 'Active'),
(4, 2, 'Active');

-- ==============================
-- 5️⃣ INSERT SUPPLIERS
-- ==============================
INSERT INTO Person (FirstName, LastName, Email, PhoneNo, StreetNo, City, PostalCode)
VALUES
('Global', 'Suppliers', 'global@supplier.com', '9000000020', '10X', 'Hyderabad', '500003'),
('Metro', 'Distributors', 'metro@supplier.com', '9000000021', '11Y', 'Mumbai', '400003');

INSERT INTO Supplier (PersonID, ShopID, TaxID, CompanyName)
VALUES
(5, 1, 'GST12345', 'Global Suppliers Pvt Ltd'),
(6, 2, 'GST67890', 'Metro Distributors Ltd');

-- ==============================
-- 6️⃣ INSERT CATEGORIES
-- ==============================
INSERT INTO Category (ShopID, Name, Description)
VALUES
(1, 'Electronics', 'Electronic Items'),
(2, 'Groceries', 'Daily Essentials');

-- ==============================
-- 7️⃣ INSERT PRODUCTS
-- ==============================
INSERT INTO Product (ShopID, CategoryID, Name, CostPrice, SellingPrice, Quantity)
VALUES
(1, 1, 'Laptop', 40000, 45000, 10),
(1, 1, 'Mobile Phone', 15000, 18000, 20),
(2, 2, 'Rice 10kg', 400, 500, 50),
(2, 2, 'Cooking Oil 1L', 120, 150, 100);

-- ==============================
-- 8️⃣ INSERT INVOICE (Shop 1)
-- ==============================
INSERT INTO Invoice (ShopID, CustomerID, TotalAmount)
VALUES
(1, 1, 45000);

-- ==============================
-- 9️⃣ INSERT INVOICE ITEMS
-- ==============================
INSERT INTO InvoiceItem (InvoiceID, ProductID, Quantity, UnitPrice, Subtotal)
VALUES
(1, 1, 1, 45000, 45000);

-- Reduce stock manually (simulate billing)
UPDATE Product
SET Quantity = Quantity - 1
WHERE ProductID = 1;

-- ==============================
-- 🔟 INSERT FINANCIAL TRANSACTION + PAYMENT
-- ==============================
INSERT INTO FinancialTransaction (ShopID, Amount)
VALUES (1, 45000);

INSERT INTO Payment (FinancialTransactionID, PaymentMethod, ReferenceNo, Remarks)
VALUES (1, 'UPI', 'UPI123456', 'Invoice Payment');

-- ==============================
-- 1️⃣1️⃣ INSERT PURCHASE ORDER (Shop 2)
-- ==============================
INSERT INTO PurchaseOrder (ShopID, SupplierID, TotalAmount)
VALUES (2, 2, 20000);

INSERT INTO PurchaseOrderItem (PurchaseOrderID, ProductID, Quantity, CostPrice, Subtotal)
VALUES (1, 3, 20, 400, 8000);

SELECT * FROM Product WHERE ShopID = 1;
customerSELECT * FROM Product WHERE ShopID = 2;

SELECT * FROM customer ;