@echo off
setlocal enabledelayedexpansion

REM Set paths
set PROJECT_DIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI
set SRC_DIR=%PROJECT_DIR%\src
set LIB_DIR=%PROJECT_DIR%\lib
set MYSQL_JAR=%LIB_DIR%\mysql-connector-j-9.6.0.jar
set SERVLET_JAR=%LIB_DIR%\servlet-api.jar

REM Create bin directory
if not exist "%PROJECT_DIR%\bin" mkdir "%PROJECT_DIR%\bin"

echo.
echo ========================================
echo Compiling Java files...
echo ========================================

REM Compile all Java files at once
cd /d "%SRC_DIR%"
javac -cp "%MYSQL_JAR%;%SERVLET_JAR%" -d "%PROJECT_DIR%\bin" db\*.java model\*.java service\*.java util\*.java main\*.java 2>&1

if errorlevel 1 (
    echo.
    echo WARNING: Compilation had issues, attempting to run anyway...
)

echo.
echo ========================================
echo Running MainApp...
echo ========================================
echo.

REM Run the application
cd /d "%PROJECT_DIR%"
java -cp "%PROJECT_DIR%\bin;%MYSQL_JAR%;%SERVLET_JAR%" main.MainApp

pause
