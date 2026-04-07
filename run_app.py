import subprocess
import os
import sys

project_dir = r"c:\Users\PRACHI\OneDrive\Documents\PRACHI\SEM-4\Flexi Project\DBMS-AND-FLEXI"
src_dir = os.path.join(project_dir, "src")
lib_dir = os.path.join(project_dir, "lib")
mysql_jar = os.path.join(lib_dir, "mysql-connector-j-9.6.0.jar")
servlet_jar = os.path.join(lib_dir, "servlet-api.jar")

print("\n" + "="*50)
print("Multi-Tenant Inventory System - Compile & Run")
print("="*50)

# Change to src directory
os.chdir(src_dir)

# Compile all Java files
classpath = f".;{mysql_jar};{servlet_jar}"

print("\n[1/2] Compiling Java files...")
print(f"Classpath: {classpath}")

compile_cmd = [
    "javac",
    "-cp", classpath,
    "-d", ".",
]

# Find all Java files
java_files = []
for root, dirs, files in os.walk("."):
    for file in files:
        if file.endswith(".java"):
            java_files.append(os.path.join(root, file))

compile_cmd.extend(java_files)

print(f"Files to compile: {len(java_files)}")
result = subprocess.run(compile_cmd, capture_output=True, text=True)

if result.returncode != 0:
    print("Compilation errors:")
    print(result.stderr)
    print("\nAttempting to run anyway...")
else:
    print("Compilation successful!")

print("\n[2/2] Running MainApp...")
print("="*50)
print()

# Run the application
os.chdir(project_dir)
run_cmd = [
    "java",
    "-cp", f".{os.pathsep}{src_dir}{os.pathsep}{mysql_jar}{os.pathsep}{servlet_jar}",
    "main.MainApp"
]

result = subprocess.run(run_cmd, capture_output=False, text=True)
sys.exit(result.returncode)
