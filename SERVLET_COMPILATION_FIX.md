# Servlet Compilation Fix

## Problem
Servlets fail to compile because `javax.servlet.http.*` classes are not found in classpath.

## Root Cause
The servlet API JAR must come from Apache Tomcat, not the local `lib/` folder.

## Quick Fix

### Option 1: Compile with Tomcat's JAR (Recommended)

If you have Tomcat 10.x installed at `C:\Apache\Tomcat`:

```batch
cd "c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI"

javac -cp "src;lib\mysql-connector-j-9.6.0.jar;C:\Apache\Tomcat\lib\servlet-api.jar" -d . src\servlet\*.java
```

### Option 2: Run the Build Script

We created `build.bat` which handles everything:

```batch
cd "c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI"
build.bat
```

This will:
1. Verify Tomcat is installed
2. Compile all packages in correct order
3. Copy resources
4. Create WAR file
5. Display next steps

### Option 3: For Development Only (Skip Servlet Compilation)

If you just want to test the Swing UI without servlets:

```batch
javac -cp "src;lib\mysql-connector-j-9.6.0.jar" -d . src\db\*.java src\model\*.java src\service\*.java src\util\*.java src\ui\*.java src\main\*.java

java -cp ".;lib\mysql-connector-j-9.6.0.jar" main.MainApp
```

## Installation Steps

### Step 1: Download & Install Tomcat (If Not Already Done)

1. Visit: https://tomcat.apache.org/download-10.cgi
2. Download: Apache Tomcat 10.1.x
3. Extract to: `C:\Apache\Tomcat`
4. Verify: Check that `C:\Apache\Tomcat\lib\servlet-api.jar` exists

### Step 2: Run Build Script

```batch
cd "c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI"
build.bat
```

### Step 3: Deploy to Tomcat

```batch
copy build\ims.war C:\Apache\Tomcat\webapps\
C:\Apache\Tomcat\bin\catalina.bat start
```

### Step 4: Access Application

Open browser: `http://localhost:8080/ims/`

Login:
- Shop ID: 1
- Username: admin
- Password: admin

## What's Happening

```
Servlet source files need javax.servlet.http.HttpServlet class
           ↓
This class comes from servlet-api.jar
           ↓
servlet-api.jar is in C:\Apache\Tomcat\lib\
           ↓
Tell javac where to find it using -cp path
           ↓
Compilation succeeds
```

## Troubleshooting

### "servlet-api.jar not found"
- Verify Tomcat is installed: `dir C:\Apache\Tomcat\lib\`
- If not found, download Tomcat first

### "Cannot find symbol: class HttpServlet"
- Make sure you're using the FULL path: `C:\Apache\Tomcat\lib\servlet-api.jar`
- Not relative path or symlink

### "Wrong servlet-api.jar version"
- Must be from Tomcat 10.x (Jakarta EE)
- Older versions use `javax.servlet`, newer use `jakarta.servlet`
- Our code uses `javax.servlet` (correct for Tomcat 10)

## Files Affected

- ✅ `src/servlet/LoginServlet.java`
- ✅ `src/servlet/ProductServlet.java`
- ✅ `src/servlet/CustomerServlet.java`
- ✅ `src/servlet/SupplierServlet.java`
- ✅ `src/servlet/PaymentServlet.java`
- ✅ `src/servlet/InvoiceServlet.java`

All use the same import and need the same classpath fix.

## Next Command to Run

```batch
build.bat
```

This single command will build and prepare everything for Tomcat deployment.
