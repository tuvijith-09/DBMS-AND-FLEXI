Set objShell = CreateObject("WScript.Shell")

Dim projectDir, srcDir, libDir, mysqlJar, servletJar
Dim strCommand

projectDir = "c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
srcDir = projectDir & "\src"
libDir = projectDir & "\lib"
mysqlJar = libDir & "\mysql-connector-j-9.6.0.jar"
servletJar = libDir & "\servlet-api.jar"

WScript.Echo "============================================================"
WScript.Echo "  Multi-Tenant Inventory System - Compile & Run"
WScript.Echo "============================================================"
WScript.Echo ""
WScript.Echo "[Step 1/2] Compiling Java files..."
WScript.Echo "Project Directory: " & projectDir
WScript.Echo "Source Directory:  " & srcDir
WScript.Echo ""

' Change to src directory and compile
strCommand = "cmd /c cd /d """ & srcDir & """ && javac -cp """ & mysqlJar & ";" & servletJar & ";."" -d . db\*.java model\*.java service\*.java util\*.java ui\*.java main\*.java"

WScript.Echo "Executing compilation command..."
objShell.Run strCommand, 1, True

WScript.Echo ""
WScript.Echo "============================================================"
WScript.Echo "[Step 2/2] Running MainApp..."
WScript.Echo "============================================================"
WScript.Echo ""

' Run the application
strCommand = "cmd /c cd /d """ & projectDir & """ && java -cp """ & projectDir & ";" & srcDir & ";" & mysqlJar & ";" & servletJar & """ main.MainApp"

WScript.Echo "Executing: java main.MainApp"
WScript.Echo "============================================================"
WScript.Echo ""

objShell.Run strCommand, 1, True

WScript.Echo ""
WScript.Echo "============================================================"
WScript.Echo "Application completed."
WScript.Echo "============================================================"
