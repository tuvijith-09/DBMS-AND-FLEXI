#!/usr/bin/env python3
import subprocess
import os
import sys
from pathlib import Path

def main():
    # Set paths
    project_dir = Path(r"c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI")
    src_dir = project_dir / "src"
    lib_dir = project_dir / "lib"
    mysql_jar = lib_dir / "mysql-connector-j-9.6.0.jar"
    servlet_jar = lib_dir / "servlet-api.jar"
    
    print("\n" + "="*60)
    print("  Multi-Tenant Inventory System - Compile & Run")
    print("="*60 + "\n")
    
    # Change to src directory
    os.chdir(str(src_dir))
    
    # Create classpath
    classpath = f"{mysql_jar};{servlet_jar};."
    
    print("[1/2] Compiling Java files...")
    print(f"Project Dir: {project_dir}")
    print(f"Source Dir:  {src_dir}")
    print(f"Classpath:   {classpath}\n")
    
    # Collect all Java files
    java_files = []
    for root, dirs, files in os.walk("."):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    
    java_files.sort()
    print(f"Java files found: {len(java_files)}")
    for jf in java_files:
        print(f"  - {jf}")
    print()
    
    # Compile command
    compile_cmd = ["javac", "-cp", classpath, "-d", "."] + java_files
    
    print("Compiling...")
    result = subprocess.run(compile_cmd, capture_output=True, text=True)
    
    if result.returncode != 0:
        print(f"\n❌ Compilation FAILED (exit code: {result.returncode})")
        if result.stderr:
            print("\nSTDERR:")
            print(result.stderr)
        if result.stdout:
            print("\nSTDOUT:")
            print(result.stdout)
        print("\nAttempting to run anyway...")
    else:
        print("✓ Compilation successful!\n")
    
    # Change back to project directory
    os.chdir(str(project_dir))
    
    print("\n" + "="*60)
    print("  Running MainApp...")
    print("="*60 + "\n")
    
    # Run command
    run_cmd = [
        "java",
        "-cp", f"{project_dir}{os.pathsep}{src_dir}{os.pathsep}{mysql_jar}{os.pathsep}{servlet_jar}",
        "main.MainApp"
    ]
    
    print("Executing: java main.MainApp\n")
    print("-"*60)
    
    result = subprocess.run(run_cmd, capture_output=False, text=True)
    
    print("-"*60)
    print(f"\nApplication exited with code: {result.returncode}")
    
    return result.returncode

if __name__ == "__main__":
    sys.exit(main())
