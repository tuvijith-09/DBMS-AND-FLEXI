@echo off
REM Multi-Tenant Inventory System - Compile and Execute
REM This script compiles the updated Java files and runs MainApp

setlocal enabledelayedexpansion

set PROJ_DIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI
set SRC_DIR=%PROJ_DIR%\src
set LIB_DIR=%PROJ_DIR%\lib
set MYSQL_JAR=%LIB_DIR%\mysql-connector-j-9.6.0.jar
set SERVLET_JAR=%LIB_DIR%\servlet-api.jar

cls
echo.
echo ============================================================
echo   Multi-Tenant Inventory System - Compile ^& Execute
echo ============================================================
echo.
echo Project: %PROJ_DIR%
echo.

REM ==========================================
REM Step 1: Verify Java is available
REM ==========================================
echo [STEP 1] Checking Java...
java -version 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)
echo OK - Java is available
echo.

REM ==========================================
REM Step 2: Compile Java files
REM ==========================================
echo [STEP 2] Compiling Java files...
echo ==========================================

cd /d "%SRC_DIR%"
echo Current directory: %CD%
echo.
echo Compiling db\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . db\*.java 2>nul

echo Compiling model\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . model\*.java 2>nul

echo Compiling service\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . service\*.java 2>nul

echo Compiling util\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . util\*.java 2>nul

echo Compiling ui\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . ui\*.java 2>nul

echo Compiling main\*.java
javac -cp ".;%MYSQL_JAR%;%SERVLET_JAR%" -d . main\*.java 2>nul

echo.
echo ✓ Compilation completed
echo.

REM ==========================================
REM Step 3: Run the application
REM ==========================================
echo [STEP 3] Running MainApp...
echo ==========================================
echo.

cd /d "%PROJ_DIR%"
java -cp ".;%SRC_DIR%;%MYSQL_JAR%;%SERVLET_JAR%" main.MainApp

echo.
echo ==========================================
echo   Execution completed
echo ==========================================
echo.
pause
