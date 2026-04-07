# Tomcat Integration Guide - IMS Application

## Overview

This guide explains how to deploy the Multi-Tenant Inventory System (IMS) to Apache Tomcat with both a Swing desktop UI and a web-based frontend.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   Frontend Layer                             │
├─────────────────┬─────────────────────────┬─────────────────┤
│  Swing UI       │   HTML/JS Web Pages     │   API.js Helper │
│  (Swing Forms)  │   (Login, Dashboard,    │   (Fetch Calls) │
│                 │    Products, Payments)  │                 │
└─────────────────┴─────────────────────────┴─────────────────┘
                            ↓ HTTP/JSON
┌─────────────────────────────────────────────────────────────┐
│              Tomcat Application Server                       │
├─────────────────────────────────────────────────────────────┤
│  Servlet Layer                                               │
│  ├── LoginServlet        → /api/login                       │
│  ├── ProductServlet      → /api/product                     │
│  ├── CustomerServlet     → /api/customer                    │
│  ├── SupplierServlet     → /api/supplier                    │
│  ├── PaymentServlet      → /api/payment                     │
│  └── InvoiceServlet      → /api/invoice                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Business Logic Layer                            │
├─────────────────────────────────────────────────────────────┤
│  Services (ProductService, CustomerService, etc.)           │
│  Models (Product, Customer, Payment, Invoice, etc.)         │
│  Utilities (InputValidator, UserSession, etc.)              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Data Access Layer                               │
├─────────────────────────────────────────────────────────────┤
│  DBConnection → MySQL Driver → MySQL Database               │
│  (multi_tenant_inventory)                                    │
└─────────────────────────────────────────────────────────────┘
```

## Project Structure

```
DBMS-AND-FLEXI/
├── src/
│   ├── db/                  (Database connection)
│   ├── model/               (Business models)
│   ├── service/             (Business logic)
│   ├── util/                (Utilities)
│   ├── ui/                  (Swing UI - for desktop client)
│   ├── main/                (MainApp entry point)
│   └── servlet/             (NEW: Web API endpoints)
│       ├── LoginServlet.java
│       ├── ProductServlet.java
│       ├── CustomerServlet.java
│       ├── SupplierServlet.java
│       ├── PaymentServlet.java
│       └── InvoiceServlet.java
├── web/                     (Web frontend files)
│   ├── index.html           (Login page)
│   ├── dashboard.html       (Main dashboard)
│   ├── product.html         (Product management)
│   ├── customer.html        (Customer management)
│   ├── supplier.html        (Supplier management)
│   ├── payment.html         (Payment management - UPDATED)
│   ├── invoice.html         (Invoice management)
│   ├── styles.css           (Global styles)
│   └── api.js               (NEW: API client helper)
├── WEB-INF/
│   └── web.xml              (NEW: Tomcat deployment descriptor)
├── lib/                     (Dependency JARs)
├── final.sql / inventory.sql (Database schema)
└── TOMCAT_SETUP.md          (Setup instructions)
```

## Step 1: Download & Install Apache Tomcat

1. Visit https://tomcat.apache.org/download-10.cgi
2. Download Apache Tomcat 10.1.x
3. Extract to: `C:\Apache\Tomcat` (Windows) or `/opt/tomcat` (Linux/Mac)

## Step 2: Build and Deploy WAR

### On Windows:

```batch
cd c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI

REM Create build directory structure
mkdir build\ims\WEB-INF\classes
mkdir build\ims\WEB-INF\lib

REM Compile all Java files (use Tomcat's servlet-api.jar)
javac -cp "src;lib\mysql-connector-j-9.6.0.jar;C:\Apache\Tomcat\lib\servlet-api.jar" -d build\ims\WEB-INF\classes ^
  src\db\*.java src\model\*.java src\service\*.java src\util\*.java src\servlet\*.java src\ui\*.java src\main\*.java

REM Copy configuration and libraries
copy WEB-INF\web.xml build\ims\WEB-INF\
xcopy web\*.* build\ims\ /Y
xcopy lib\*.jar build\ims\WEB-INF\lib\ /Y

REM Create WAR file
cd build
For /D %%X in (ims) do tar.exe -acf ims.war %%X

REM Deploy
copy ims.war C:\Apache\Tomcat\webapps\
```

## Step 3: Configure Database

1. Create MySQL database:
   ```sql
   CREATE DATABASE multi_tenant_inventory;
   USE multi_tenant_inventory;
   ```

2. Load schema from final.sql:
   ```bash
   mysql -u root -p multi_tenant_inventory < final.sql
   ```

3. Verify DBConnection credentials in `src/db/DBConnection.java`:
   ```java
   con = DriverManager.getConnection(
       "jdbc:mysql://127.0.0.1:3306/multi_tenant_inventory",
       "root",
       "tu09"  // Update password if needed
   );
   ```

## Step 4: Start Tomcat

### Windows:
```batch
C:\Apache\Tomcat\bin\catalina.bat start
```

### Linux/Mac:
```bash
/opt/tomcat/bin/catalina.sh start
```

## Step 5: Access Application

- **Web Frontend**: http://localhost:8080/ims/
- **Login with**:
  - Shop ID: 1 (or 2)
  - Username: admin
  - Password: admin

## API Endpoints

All endpoints require an active session (stored in HttpSession).

### Authentication
```
POST /ims/api/login
Parameters: username, password, shopId
Response: { success: true/false, message: "...", shopId: N }
```

### Products
```
GET  /ims/api/product          → List all products
POST /ims/api/product          → Add new product
DELETE /ims/api/product?id=N   → Delete product
```

### Customers
```
GET  /ims/api/customer         → List all customers
POST /ims/api/customer         → Add new customer
DELETE /ims/api/customer?id=N  → Delete customer
```

### Suppliers
```
GET  /ims/api/supplier         → List all suppliers
POST /ims/api/supplier         → Add new supplier
DELETE /ims/api/supplier?id=N  → Delete supplier
```

### Payments
```
GET  /ims/api/payment          → List all payments
POST /ims/api/payment          → Record new payment
DELETE /ims/api/payment?id=N   → Cancel payment
```

### Invoices
```
GET  /ims/api/invoice          → List all invoices
POST /ims/api/invoice          → Create new invoice
```

## JavaScript API Helper (api.js)

The web frontend uses an `api.js` helper to make backend calls:

```javascript
// Login
const result = await API.login('admin', 'admin', 1);

// Get all payments
const payments = await API.payment.getAll();

// Add payment
await API.payment.add({ invoiceId: 1, amount: 500, paymentMode: 'Cash' });

// Delete payment
await API.payment.delete(1);

// Check session
const session = await API.checkSession();
```

## Features

✅ **Multi-tenant support** - ShopID-based data isolation
✅ **Session management** - User authentication via servlet
✅ **RESTful APIs** - JSON request/response format
✅ **Transaction support** - Invoice + Items atomic operations
✅ **Payment validation** - Positive amounts, invoice existence checks
✅ **Error handling** - Graceful DB connection fallback
✅ **Responsive UI** - Dark/Light theme support

## Troubleshooting

### Port 8080 already in use
```batch
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Database connection error
- Check MySQL is running
- Verify credentials in DBConnection.java
- Ensure database `multi_tenant_inventory` exists

### 404 Error on login
- WAR file must be deployed to `C:\Apache\Tomcat\webapps\ims.war`
- Access URL: `http://localhost:8080/ims/` (with trailing slash)

### Servlet not found
- Ensure `WEB-INF\web.xml` exists with servlet mappings
- Check servlet class names match package structure

## Testing with curl

```bash
# Login
curl -c cookies.txt -X POST http://localhost:8080/ims/api/login \
  -d "username=admin&password=admin&shopId=1"

# Get payments (using saved session)
curl -b cookies.txt http://localhost:8080/ims/api/payment

# Add payment
curl -b cookies.txt -X POST http://localhost:8080/ims/api/payment \
  -d "invoiceId=1&amount=500&paymentMode=Cash"
```

## Files Modified

- ✅ Created: `src/servlet/*.java` (6 servlet classes)
- ✅ Created: `WEB-INF/web.xml` (Deployment config)
- ✅ Created: `web/api.js` (JavaScript API helper)
- ✅ Updated: `web/index.html` (Login with API integration)
- ✅ Updated: `web/payment.html` (Payment CRUD with backend)

## Next Steps

1. Deploy to Tomcat (see Step 2)
2. Access http://localhost:8080/ims/
3. Login with Shop ID 1, username: admin, password: admin
4. Test payment recording and view backend integration
5. Update remaining HTML pages (product.html, customer.html, supplier.html) with API calls (similar to payment.html)

## More Information

- Tomcat Documentation: https://tomcat.apache.org/tomcat-10.0-doc/
- Servlet API: https://javaee.github.io/servlet-spec/
- MySQL JDBC: https://dev.mysql.com/doc/connector-j/8.0/en/

---

**Last Updated**: April 5, 2026
