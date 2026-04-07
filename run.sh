#!/bin/bash

# Compilation and Execution Script for Multi-Tenant Inventory System
# This script will compile all Java files and run the MainApp

cd "c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI" || exit 1

PROJECT_DIR="c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
SRC_DIR="$PROJECT_DIR\src"
LIB_DIR="$PROJECT_DIR\lib"
MYSQL_JAR="$LIB_DIR\mysql-connector-j-9.6.0.jar"
SERVLET_JAR="$LIB_DIR\servlet-api.jar"

echo ""
echo "============================================================"
echo "  Multi-Tenant Inventory System - Compile & Run"
echo "============================================================"
echo ""

echo "[Step 1/2] Compiling Java files..."
echo "Project Directory: $PROJECT_DIR"
echo "Source Directory:  $SRC_DIR"
echo ""

cd "$SRC_DIR" || exit 1

CLASSPATH="$MYSQL_JAR;$SERVLET_JAR;."

echo "Compiling with classpath: $CLASSPATH"
echo ""

javac -cp "$CLASSPATH" -d . db/*.java model/*.java service/*.java util/*.java ui/*.java main/*.java 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Compilation successful!"
else
    echo ""
    echo "⚠ Warning: Compilation had issues but proceeding to run..."
fi

echo ""
echo "============================================================"
echo "[Step 2/2] Running MainApp..."
echo "============================================================"
echo ""

cd "$PROJECT_DIR" || exit 1

RUN_CLASSPATH="$PROJECT_DIR;$SRC_DIR;$MYSQL_JAR;$SERVLET_JAR"

echo "Executing: java main.MainApp"
echo "============================================================"
echo ""

java -cp "$RUN_CLASSPATH" main.MainApp

echo ""
echo "============================================================"
echo "Application completed."
echo "============================================================"
