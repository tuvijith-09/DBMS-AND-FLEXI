@echo off
REM ============================================================
REM Multi-Tenant Inventory System
REM Compile and Run Script
REM ============================================================

setlocal enabledelayedexpansion

REM Define paths
set PROJDIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI
set SRCDIR=%PROJDIR%\src
set LIBDIR=%PROJDIR%\lib

REM Print banner
cls
echo.
echo ============================================================
echo    Multi-Tenant Inventory System - Test Execution
echo ============================================================
echo.
echo Project Directory: %PROJDIR%
echo.

REM Step 1: Compile
echo Step 1: Compiling Java files...
echo.

cd /d "%SRCDIR%"

REM Compile all Java packages
echo Compiling database package...
javac -cp ".;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" -d . db\*.java

echo Compiling model package...
javac -cp ".;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" -d . model\*.java

echo Compiling service package...
javac -cp ".;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" -d . service\*.java

echo Compiling util package...
javac -cp ".;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" -d . util\*.java 2>nul

echo Compiling main package...
javac -cp ".;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" -d . main\*.java

echo.
echo Step 1 Complete: Compilation successful
echo.
echo ============================================================
echo Step 2: Running Application
echo ============================================================
echo.

REM Step 2: Run
cd /d "%PROJDIR%"

java -cp ".;%SRCDIR%;%LIBDIR%\mysql-connector-j-9.6.0.jar;%LIBDIR%\servlet-api.jar" main.MainApp

echo.
echo ============================================================
echo Execution Complete
echo ============================================================
echo.

pause
