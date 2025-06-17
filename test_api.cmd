@echo off
echo Testing AML Backend API...
echo.

REM Start the application in background
echo Starting Spring Boot application...
start /B cmd /c "mvn spring-boot:run > app.log 2>&1"

REM Wait for application to start
echo Waiting for application to start (30 seconds)...
timeout /t 30 /nobreak > nul

echo.
echo Testing API endpoints...
echo.

REM Test basic endpoints
echo 1. Testing DERs endpoint:
curl -X GET "http://localhost:8000/api/references/ders" -H "accept: application/json" || echo Failed to connect

echo.
echo 2. Testing Departments endpoint:
curl -X GET "http://localhost:8000/api/references/departments" -H "accept: application/json" || echo Failed to connect

echo.
echo 3. Testing NEW DERs with Departments endpoint:
curl -X GET "http://localhost:8000/api/references/ders-with-departments" -H "accept: application/json" || echo Failed to connect

echo.
echo Testing completed. Check app.log for application logs.
pause
