import subprocess
import os
import sys

def run_compilation_and_app():
    """Compile and run the Java application"""
    
    # Define paths
    project_dir = r"c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
    src_dir = os.path.join(project_dir, "src")
    lib_dir = os.path.join(project_dir, "lib")
    mysql_jar = os.path.join(lib_dir, "mysql-connector-j-9.6.0.jar")
    servlet_jar = os.path.join(lib_dir, "servlet-api.jar")
    
    print("\n" + "="*60)
    print("  Multi-Tenant Inventory System - Compile & Run")
    print("="*60 + "\n")
    
    # Step 1: Compile
    print("[Step 1/2] Compiling Java files...\n")
    print(f"Project Dir: {project_dir}")
    print(f"Source Dir:  {src_dir}")
    print(f"Classpath:   {mysql_jar};{servlet_jar}\n")
    
    os.chdir(src_dir)
    
    # Collect all Java files
    java_files = []
    for root, dirs, files in os.walk("."):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    
    java_files.sort()
    print(f"Java files to compile: {len(java_files)}")
    for jf in java_files:
        print(f"  {jf}")
    print()
    
    # Build classpath
    classpath = f"{mysql_jar};{servlet_jar};."
    
    # Compile command
    compile_cmd = ["javac", "-cp", classpath, "-d", "."] + java_files
    
    print("Compiling...")
    print("-" * 60)
    
    result = subprocess.run(compile_cmd)
    
    if result.returncode != 0:
        print(f"\n⚠ Warning: Compilation exited with code {result.returncode}")
        print("Attempting to run anyway...\n")
    else:
        print("\n✓ Compilation successful!\n")
    
    # Step 2: Run
    print("="*60)
    print("[Step 2/2] Running MainApp...\n")
    print("="*60)
    print()
    
    os.chdir(project_dir)
    
    # Build run classpath
    run_classpath = f"{project_dir};{src_dir};{mysql_jar};{servlet_jar}"
    
    # Run command
    run_cmd = ["java", "-cp", run_classpath, "main.MainApp"]
    
    print("Executing: java main.MainApp")
    print("-" * 60)
    print()
    
    result = subprocess.run(run_cmd)
    
    print()
    print("-" * 60)
    print(f"Application exited with code: {result.returncode}")
    print("="*60)
    
    return result.returncode

if __name__ == "__main__":
    try:
        exit_code = run_compilation_and_app()
        sys.exit(exit_code)
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
