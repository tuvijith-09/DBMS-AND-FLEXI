@echo off
REM Build and compile all Java files for IMS Tomcat deployment
REM This script handles servlet compilation correctly

setlocal enabledelayedexpansion

cd /d "c:\Users\PRACHI\Downloads\Flexi Project\DBMS-AND-FLEXI"

echo ============================================
echo   IMS Project Build Script
echo ============================================
echo.

REM Check Tomcat installation (Search in common paths)
set TOMCAT_PATH=C:\Apache\Tomcat
if not exist "!TOMCAT_PATH!\lib\servlet-api.jar" (
    set TOMCAT_PATH=C:\Program Files\Apache Software Foundation\Tomcat 9.0
)
if not exist "!TOMCAT_PATH!\lib\servlet-api.jar" (
    set TOMCAT_PATH=C:\Program Files\Apache Software Foundation\Tomcat 10.1
)

if not exist "!TOMCAT_PATH!\lib\servlet-api.jar" (
    echo ERROR: servlet-api.jar not found!
    echo Please ensure Tomcat is installed and update TOMCAT_PATH in this script.
    timeout /t 5
    exit /b 1
)
echo Found Tomcat at: !TOMCAT_PATH!

echo [1/4] Creating build directories...
mkdir build\FlexiProject\WEB-INF\classes 2>nul
mkdir build\FlexiProject\WEB-INF\lib 2>nul
echo OK

echo [2/4] Compiling Java source files...
echo Compiling db, model, service, util packages...
javac -cp "src;lib\mysql-connector-j-9.6.0.jar" -d build\FlexiProject\WEB-INF\classes ^
  src\db\*.java src\model\*.java src\service\*.java src\util\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile core classes
    exit /b 1
)

echo Compiling servlet package with Tomcat servlet-api.jar...
javac -cp "src;lib\mysql-connector-j-9.6.0.jar;!TOMCAT_PATH!\lib\servlet-api.jar" -d build\FlexiProject\WEB-INF\classes src\servlet\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile servlets
    exit /b 1
)

echo Compiling UI and main packages...
javac -cp "src;lib\mysql-connector-j-9.6.0.jar" -d build\FlexiProject\WEB-INF\classes src\ui\*.java src\main\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile UI classes
    exit /b 1
)

echo OK

echo [3/4] Copying configuration and resources...
if exist "WEB-INF\web.xml" copy "WEB-INF\web.xml" "build\FlexiProject\WEB-INF\" >nul
xcopy "web\*.*" "build\FlexiProject\" /Y /Q /E
xcopy "lib\*.jar" "build\FlexiProject\WEB-INF\lib\" /Y /Q
echo OK

echo [4/4] Deploying to Tomcat...
REM Delete corruption interference
if exist "!TOMCAT_PATH!\webapps\FlexiProject.war" del /q "!TOMCAT_PATH!\webapps\FlexiProject.war"
if exist "!TOMCAT_PATH!\webapps\IMS.war" del /q "!TOMCAT_PATH!\webapps\IMS.war"
if exist "!TOMCAT_PATH!\webapps\FlexiProject" (
    rmdir /s /q "!TOMCAT_PATH!\webapps\FlexiProject"
)

mkdir "!TOMCAT_PATH!\webapps\FlexiProject"
xcopy "build\FlexiProject\*.*" "!TOMCAT_PATH!\webapps\FlexiProject\" /E /Y /Q
if errorlevel 1 (
    echo ERROR: Deployment failed. Is Tomcat running and locking files?
    exit /b 1
)
echo OK

echo.
echo ======================================================
echo SUCCESS: FlexiProject deployed to:
echo !TOMCAT_PATH!\webapps\FlexiProject
echo ======================================================
echo Please restart Tomcat to apply changes.
echo Access URL: http://localhost:8080/FlexiProject/index.html
echo.
echo Check DB Status at: http://localhost:8080/FlexiProject/test_db.jsp
echo.
timeout /t 5
exit /b 0
