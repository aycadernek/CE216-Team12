@echo off
echo Building the project with Maven...
call mvn clean package "-Dmaven.test.skip=true"
if %errorlevel% neq 0 (
    echo Maven build failed. Please check the errors above.
    pause
    exit /b %errorlevel%
)

echo.
echo Running the application...
java -jar target\project-team12-1.0-SNAPSHOT.jar
if %errorlevel% neq 0 (
    echo Application exited with an error.
    pause
)

