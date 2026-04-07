#!/usr/bin/env python3
"""
Multi-Tenant Inventory System - Compile and Execute
"""

import subprocess
import sys
import os
from pathlib import Path

def main():
    project_dir = Path(r"c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI")
    src_dir = project_dir / "src"
    lib_dir = project_dir / "lib"
    
    # Ensure paths exist
    if not src_dir.exists():
        print(f"ERROR: Source directory not found: {src_dir}")
        return 1
    
    mysql_jar = lib_dir / "mysql-connector-j-9.6.0.jar"
    servlet_jar = lib_dir / "servlet-api.jar"
    
    if not mysql_jar.exists() or not servlet_jar.exists():
        print("WARNING: Required JAR files not found")
    
    print("\n" + "="*60)
    print("  Multi-Tenant Inventory System - Compile & Execute")
    print("="*60)
    
    # Step 1: Compile
    print("\n[STEP 1] Compiling Java files...")
    print("-" * 60)
    
    os.chdir(src_dir)
    
    # Find all modified Java files
    java_files = []
    modified_files = [
        "db/DBConnection.java",
        "service/ProductService.java",
        "service/CustomerService.java",
        "service/SupplierService.java",
        "main/MainApp.java",
        "model/Product.java",
        "service/CRUDOperations.java",
    ]
    
    for jf in modified_files:
        file_path = src_dir / jf
        if file_path.exists():
            java_files.append(str(file_path))
            print(f"  ✓ {jf}")
    
    classpath = f".;{mysql_jar};{servlet_jar}"
    
    compile_cmd = [
        "javac",
        "-cp", classpath,
        "-d", "."
    ] + java_files
    
    print(f"\nClasspath: {classpath}")
    print(f"\nRunning: {' '.join(compile_cmd[:4])} [files...]")
    
    result = subprocess.run(compile_cmd, capture_output=True, text=True)
    
    if result.returncode != 0:
        print("⚠ Compilation warnings/errors:")
        if result.stderr:
            print(result.stderr[:500])
    else:
        print("✓ Compilation successful")
    
    # Step 2: Run
    print("\n[STEP 2] Running MainApp...")
    print("-" * 60)
    
    os.chdir(str(project_dir))
    
    run_cmd = [
        "java",
        "-cp", f".;{src_dir};{mysql_jar};{servlet_jar}",
        "main.MainApp"
    ]
    
    print(f"Working Directory: {project_dir}")
    print(f"Classpath: {run_cmd[2]}")
    print("\n" + "="*60)
    print()
    
    result = subprocess.run(run_cmd, capture_output=True, text=True)
    
    # Print output
    print(result.stdout)
    
    if result.stderr:
        print("\n[STDERR]:")
        print(result.stderr)
    
    print("\n" + "="*60)
    
    return result.returncode

if __name__ == "__main__":
    sys.exit(main())
