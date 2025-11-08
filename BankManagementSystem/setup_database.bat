@echo off
echo 🚀 Bank Management System - Database Setup
echo ==========================================

echo.
echo 📋 Prerequisites Check:
echo - MySQL Server must be running
echo - phpMyAdmin should be accessible
echo - Java 23 must be installed
echo.

echo 🔧 Setting JAVA_HOME...
set JAVA_HOME=C:\Program Files\Java\jdk-23

echo.
echo 🧪 Running Database Setup...
echo.

REM Try to compile and run the setup
echo Compiling project...
call mvnw.cmd clean compile -q

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed!
    echo Please check your Java installation and try again.
    pause
    exit /b 1
)

echo ✅ Compilation successful!

echo.
echo 🚀 Running database setup...
echo.

REM Run the database setup
java -cp "target/classes;target/dependency/*" bankmanagementsystem.model.DatabaseSetupRunner

echo.
echo 📝 Setup completed!
echo.
echo Next steps:
echo 1. Open phpMyAdmin in your browser
echo 2. Select 'bank_management' database
echo 3. Browse the tables to verify setup
echo 4. Update your application code to use DatabaseStorage
echo.
pause
