# Set Environment Variables for Healthcare Management System

Write-Host "Setting up environment variables..." -ForegroundColor Green

# Database Configuration
$env:ENV = "production"
$env:DATABASE_URL = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"
$env:APP_ENV = "production"
$env:PORT = "8080"

# Set for current session
Write-Host "Environment variables set for current session:" -ForegroundColor Yellow
Write-Host "ENV = $env:ENV" -ForegroundColor Cyan
Write-Host "DATABASE_URL = $env:DATABASE_URL" -ForegroundColor Cyan
Write-Host "PORT = $env:PORT" -ForegroundColor Cyan

Write-Host ""
Write-Host "Variables set successfully!" -ForegroundColor Green
Write-Host "Now you can run: mvn clean compile" -ForegroundColor Yellow 