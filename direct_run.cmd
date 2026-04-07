@echo off

REM Direct execution script for compilation and running Java app
REM This script uses only standard cmd features

setlocal enabledelayedexpansion

echo Starting compilation and execution...
echo.

set "PROJECT_DIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
set "SRC_DIR=!PROJECT_DIR!\src"
set "LIB_DIR=!PROJECT_DIR!\lib"
set "MYSQL_JAR=!LIB_DIR!\mysql-connector-j-9.6.0.jar"
set "SERVLET_JAR=!LIB_DIR!\servlet-api.jar"

echo ============================================================
echo  Multi-Tenant Inventory System - Compile and Run
echo ============================================================
echo.

echo Step 1: Compile Java files
echo Project Directory: !PROJECT_DIR!
echo Source Directory: !SRC_DIR!
echo.

cd /d "!SRC_DIR!"

echo Compiling...
javac -cp "!MYSQL_JAR!;!SERVLET_JAR!;." -d . db\*.java model\*.java service\*.java util\*.java ui\*.java main\*.java 2>&1

if errorlevel 1 (
    echo.
    echo WARNING: Compilation had issues. Errorlevel: %errorlevel%
    echo Continuing to execution...
) else (
    echo Compilation successful!
)

echo.
echo ============================================================
echo Step 2: Run MainApp
echo ============================================================
echo.

cd /d "!PROJECT_DIR!"

echo Executing application...
java -cp "!PROJECT_DIR!;!SRC_DIR!;!MYSQL_JAR!;!SERVLET_JAR!" main.MainApp

echo.
echo ============================================================
echo Execution completed.
echo ============================================================
echo.

endlocal
