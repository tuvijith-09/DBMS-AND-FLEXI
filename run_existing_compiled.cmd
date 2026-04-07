@echo off
REM Simple execution of already-compiled Java application

setlocal enabledelayedexpansion

set "PROJECT_DIR=c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
set "SRC_DIR=!PROJECT_DIR!\src"
set "MYSQL_JAR=!PROJECT_DIR!\lib\mysql-connector-j-9.6.0.jar"
set "SERVLET_JAR=!PROJECT_DIR!\lib\servlet-api.jar"

echo.
echo ============================================================
echo  Multi-Tenant Inventory System
echo ============================================================
echo.

echo Running MainApp with existing compiled classes...
echo.

cd /d "!PROJECT_DIR!"

java -cp "!PROJECT_DIR!;!SRC_DIR!;!MYSQL_JAR!;!SERVLET_JAR!" main.MainApp

echo.
echo ============================================================
echo.

endlocal
