@echo off
echo Testing Public User Courses API...
echo.

REM Set test IIN - replace with actual IIN for testing
set TEST_IIN=123456789012

echo Testing endpoint: GET /api/public/%TEST_IIN%
echo.

REM Test the new public endpoint
echo Testing public user courses endpoint:
curl -X GET "http://localhost:8000/api/public/%TEST_IIN%" -H "accept: application/json" || echo Failed to connect

echo.
echo.

REM Test with invalid IIN format
echo Testing with invalid IIN format:
curl -X GET "http://localhost:8000/api/public/invalid_iin" -H "accept: application/json" || echo Failed to connect

echo.
echo.

REM Test with non-existent IIN
echo Testing with non-existent IIN:
curl -X GET "http://localhost:8000/api/public/999999999999" -H "accept: application/json" || echo Failed to connect

echo.
echo Testing completed.
pause
