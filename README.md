### 1. System Architecture & Component Workflow
The project follows a **Service-Oriented Architecture (SOA)**, separating the user interface from the business logic and database operations. This prevents spaghetti code and makes the application scalable.

**The 3-Tier Execution Flow (Example: Adding a Supplier)**
1. **Presentation Layer (UI):** `SupplierUI.java` captures the user's input from text fields. It calls `InputValidator.java` to verify the data format. If valid, it creates a `Supplier` object.
2. **Business/Service Layer:** `SupplierService.java` receives the object. It handles the logical workflow (e.g., checking if the person already exists before creating a new record).
3. **Data Access Layer (JDBC):** `DBConnection.java` establishes a connection. `SupplierService` executes the specific `PreparedStatement` SQL queries, captures the result, and returns a success/fail boolean back to the UI.

---

### 2. Comprehensive Input Validation Strategy
Validations act as the first line of defense. By rejecting bad data at the UI level, we prevent unnecessary database queries and maintain **Data Integrity**.

| Field Type | Regex Pattern Used | Purpose & Logic |
| :--- | :--- | :--- |
| **Empty Fields** | `.trim().isEmpty()` | Prevents users from submitting blank forms or strings made entirely of spaces. |
| **Names / City** | `^[a-zA-Z\s]{2,}$` | Ensures strings only contain upper/lowercase letters and spaces. Rejects numbers, special characters, and single-letter entries. |
| **Phone Number**| `^\d{10}$` | Enforces exact 10-digit numeric lengths. Rejects alphabetic characters and international formatting symbols (like `+` or `-`). |
| **Email Address** | `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[a-zA-Z]{2,}$` | Requires a valid prefix, an `@` symbol, a domain name, and a Top Level Domain (like `.com` or `.in` with at least 2 characters). |
| **Password** | `pass.length() >= 6` | Enforces a minimum length requirement for basic security during warehouse registration. |

---

### 3. Core Workflows & Logic Algorithms

#### A. The Multi-Tenant Registration Workflow
When a new warehouse owner registers, the system must create multiple linked entities simultaneously without corrupting the database if one step fails.
1. System validates all UI inputs.
2. `con.setAutoCommit(false)` is triggered to start an **ACID Transaction**.
3. `INSERT INTO Shop` executes. The system retrieves the auto-generated `ShopID`.
4. `INSERT INTO Person` executes. The system retrieves the auto-generated `PersonID`.
5. `INSERT INTO User` links the `ShopID`, `PersonID`, and `Password`.
6. `con.commit()` saves all three records permanently.

#### B. The "Check-Then-Link" Normalization Algorithm
To prevent the `Person` table from filling up with duplicate humans, the system uses a smart routing algorithm when adding Customers or Suppliers:
1. **Search:** `SELECT PersonID FROM Person WHERE Email = ?`
2. **Condition 1 (New Human):** If no result, create a new `Person` record and grab the new ID.
3. **Condition 2 (Existing Human):** If a result is found, grab the existing `PersonID`.
4. **Duplicate Check:** `SELECT 1 FROM Customer WHERE PersonID = ? AND ShopID = ?`. If they are already registered at *this specific warehouse*, abort and show an error.
5. **Link:** `INSERT INTO Customer (PersonID, ShopID)`. This allows Amit to be a customer at Shop 1 and Shop 2, utilizing the same `Person` record!

#### C. The Checkout & Billing Workflow (State Management)
Generating an invoice is the most critical workflow because it alters inventory stock.
1. Cashier adds items to the UI cart.
2. System checks `Quantity` in the `Product` table to ensure sufficient stock.
3. Transaction begins (`setAutoCommit(false)`).
4. `INSERT INTO Invoice` generates the main bill and captures the `InvoiceID`.
5. System loops through the cart:
   * Executes `INSERT INTO InvoiceItem` to record the sale line-by-line.
   * Executes `UPDATE Product SET Quantity = Quantity - ?` to deduct the sold stock.
6. If any product fails (e.g., stock runs out mid-transaction), the system triggers `con.rollback()`, erasing the invoice and restoring all stock to its original state.

---

### 4. Advanced Technical Highlights (For Viva / Defense)

* **Compile-Time Polymorphism (Method Overloading):** Utilized in the Service classes (e.g., having a standard `add(Product p)` method and an overloaded `add(Product p, String addedBy)` method for logging purposes).
* **Prevention of SQL Injection:** Zero string-concatenation is used for SQL queries. All user inputs are sanitized and passed through `PreparedStatement` parameters (`?`), ensuring malicious code cannot be executed by the database engine.
* **Memory Leak Prevention:** Standard JDBC logic is prone to memory leaks if connections remain open. This project uses Java's **`try-with-resources`** block. This guarantees that `Connection`, `PreparedStatement`, and `ResultSet` objects are automatically closed the moment the query finishes, keeping the application lightweight.
* **Cascading Deletions:** The database schema utilizes `ON DELETE CASCADE` foreign keys. If a `Shop` is deleted, the database engine automatically scrubs all products, invoices, and supplier links associated with that shop, preventing orphaned data.
