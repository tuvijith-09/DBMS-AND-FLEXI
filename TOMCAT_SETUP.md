# Apache Tomcat Setup Guide

## Installation

1. Download Apache Tomcat 10.x from https://tomcat.apache.org/download-10.cgi
2. Extract to: `C:\Apache\Tomcat` (or your preferred location)
3. Ensure Java is in PATH

## Deployment Instructions

### Step 1: Install Apache Tomcat First

Before building, you **must** have Tomcat installed because the servlet classes need Tomcat's servlet-api JAR.

1. Download Apache Tomcat 10.x from https://tomcat.apache.org/download-10.cgi
2. Extract to: `C:\Apache\Tomcat`

### Step 2: Build WAR File

The correct servlet-api.jar is in Tomcat. Update the build command to use it:

```cmd
cd c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI

REM Compile all Java files (use Tomcat's servlet-api)
javac -cp "src;lib\mysql-connector-j-9.6.0.jar;C:\Apache\Tomcat\lib\servlet-api.jar" -d build\ims\WEB-INF\classes ^
  src\db\*.java src\model\*.java src\service\*.java src\util\*.java src\servlet\*.java src\ui\*.java src\main\*.java

REM Copy configuration and libraries
mkdir build\ims\WEB-INF\classes
mkdir build\ims\WEB-INF\lib
copy WEB-INF\web.xml build\ims\WEB-INF\
xcopy web\*.* build\ims\ /Y
xcopy lib\*.jar build\ims\WEB-INF\lib\ /Y

REM Create WAR file
cd build
For /D %%X in (ims) do tar.exe -acf ims.war %%X

REM Deploy
copy ims.war C:\Apache\Tomcat\webapps\
```

### Step 3: Start Tomcat

## API Endpoints

- **Login**: `POST /ims/api/login` (username, password, shopId)
- **Products**: `GET/POST/DELETE /ims/api/product`
- **Customers**: `GET/POST/DELETE /ims/api/customer`
- **Suppliers**: `GET/POST/DELETE /ims/api/supplier`
- **Payments**: `GET/POST/DELETE /ims/api/payment`
- **Invoices**: `GET/POST/DELETE /ims/api/invoice`

## Troubleshooting

### Port 8080 already in use
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### MIME type not recognized
Update `WEB-INF\web.xml` with:
```xml
<mime-mapping>
  <extension>js</extension>
  <mime-type>text/javascript</mime-type>
</mime-mapping>
```

### Database Connection Error
Ensure `multi_tenant_inventory` database exists and run:
```sql
source final.sql;
```

## Stopping Tomcat
```cmd
C:\Apache\Tomcat\bin\catalina.bat stop
```
