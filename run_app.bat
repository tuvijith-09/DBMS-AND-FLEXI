@echo off
cd /d "c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"

set CLASSPATH=lib\mysql-connector-j-9.6.0.jar;lib\servlet-api.jar;src

echo Compiling all Java files...
cd src
javac -cp ".;..\lib\mysql-connector-j-9.6.0.jar;..\lib\servlet-api.jar" -d . db\*.java model\*.java service\*.java util\*.java main\*.java 2>nul

cd ..

echo.
echo ========================================
echo   Multi-Tenant Inventory System
echo ========================================
echo.
echo Running MainApp...
echo.

java -cp "src;lib\mysql-connector-j-9.6.0.jar;lib\servlet-api.jar" main.MainApp
