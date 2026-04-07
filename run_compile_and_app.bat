@echo off
REM Build and Run Script for Multi-Tenant Inventory System

setlocal enabledelayedexpansion

set PROJECT_DIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI
set SRC_DIR=%PROJECT_DIR%\src
set LIB_DIR=%PROJECT_DIR%\lib
set MYSQL_JAR=%LIB_DIR%\mysql-connector-j-9.6.0.jar
set SERVLET_JAR=%LIB_DIR%\servlet-api.jar

echo.
echo ============================================================
echo  Multi-Tenant Inventory System - Compile ^& Run
echo ============================================================
echo.

echo [Step 1/2] Compiling Java files...
echo Project Directory: %PROJECT_DIR%
echo Source Directory:  %SRC_DIR%
echo.

cd /d "%SRC_DIR%"

REM Compile all Java files
echo Compiling all Java files from: %SRC_DIR%
javac -cp "%MYSQL_JAR%;%SERVLET_JAR%;." -d . db\*.java model\*.java service\*.java util\*.java ui\*.java main\*.java 2>&1

if !errorlevel! equ 0 (
    echo.
    echo Compilation successful!
) else (
    echo.
    echo WARNING: Compilation had some issues but proceeding to run...
    echo Exit Code: !errorlevel!
)

echo.
echo ============================================================
echo [Step 2/2] Running MainApp...
echo ============================================================
echo.

cd /d "%PROJECT_DIR%"

java -cp "%PROJECT_DIR%;%SRC_DIR%;%MYSQL_JAR%;%SERVLET_JAR%" main.MainApp

echo.
echo ============================================================
echo Application completed.
echo ============================================================

endlocal
