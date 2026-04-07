$ProjectDir = "c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
$SrcDir = "$ProjectDir\src"
$LibDir = "$ProjectDir\lib"
$BinDir = "$ProjectDir\bin"
$MysqlJar = "$LibDir\mysql-connector-j-9.6.0.jar"
$ServletJar = "$LibDir\servlet-api.jar"

# Create bin directory if not exists
if (-not (Test-Path $BinDir)) {
    New-Item -ItemType Directory -Path $BinDir | Out-Null
}

Write-Host ""
Write-Host "========================================"
Write-Host "Compiling Java files..."
Write-Host "========================================"
Write-Host ""

# Get all Java files
$javaFiles = Get-ChildItem -Path $SrcDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

# Compile
$classPath = "$MysqlJar;$ServletJar"
$javaArgs = @("-cp", $classPath, "-d", $BinDir) + $javaFiles

Write-Host "Compiling with classpath: $classPath"
Write-Host ""

& javac $javaArgs 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Compilation issues detected, but attempting to run..."
}

Write-Host ""
Write-Host "========================================"
Write-Host "Running MainApp..."
Write-Host "========================================"
Write-Host ""

Set-Location $ProjectDir
& java -cp "$BinDir;$MysqlJar;$ServletJar" main.MainApp

Write-Host ""
Read-Host "Press Enter to exit"
