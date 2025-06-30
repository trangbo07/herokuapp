# Simple Database Setup Script
# Connection string for Healthcare Management System

param(
    [switch]$PlainPassword = $false
)

$ConnectionString = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"

Write-Host "=== Healthcare Database Setup ===" -ForegroundColor Green
Write-Host ""

if ($PlainPassword) {
    Write-Host "Using PLAIN TEXT passwords for testing" -ForegroundColor Yellow
} else {
    Write-Host "Using HASHED passwords (recommended)" -ForegroundColor Green
}

# Check if psql is available
Write-Host ""
Write-Host "Checking PostgreSQL client..." -ForegroundColor Yellow
try {
    psql --version | Out-Null
    Write-Host "PostgreSQL client is ready!" -ForegroundColor Green
} catch {
    Write-Host "PostgreSQL client not found!" -ForegroundColor Red
    Write-Host "Please install from: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
    Write-Host "Or use database web console to import schema manually." -ForegroundColor Yellow
    exit 1
}

# Test connection
Write-Host ""
Write-Host "Testing database connection..." -ForegroundColor Yellow
$env:PGPASSWORD = "qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1"
psql -h "dpg-d1h425idbo4c739tq50g-a" -U "healthcare_user" -d "healthcare_system" -c "SELECT version();" > $null 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database connection successful!" -ForegroundColor Green
} else {
    Write-Host "Database connection failed!" -ForegroundColor Red
    exit 1
}

# Import schema
Write-Host ""
Write-Host "Importing database schema..." -ForegroundColor Yellow
if (Test-Path "database/postgresql-schema.sql") {
    psql -h "dpg-d1h425idbo4c739tq50g-a" -U "healthcare_user" -d "healthcare_system" -f "database/postgresql-schema.sql"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Schema imported successfully!" -ForegroundColor Green
    } else {
        Write-Host "Schema import had issues (tables might already exist)" -ForegroundColor Yellow
    }
} else {
    Write-Host "Schema file not found!" -ForegroundColor Red
}

# Import sample data
Write-Host ""
Write-Host "Importing sample data..." -ForegroundColor Yellow

if ($PlainPassword) {
    $sampleFile = "database/sample-data-plain.sql"
    Write-Host "Using plain text password file: $sampleFile" -ForegroundColor Yellow
} else {
    $sampleFile = "database/sample-data.sql" 
    Write-Host "Using hashed password file: $sampleFile" -ForegroundColor Yellow
}

if (Test-Path $sampleFile) {
    psql -h "dpg-d1h425idbo4c739tq50g-a" -U "healthcare_user" -d "healthcare_system" -f $sampleFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Sample data imported successfully!" -ForegroundColor Green
    } else {
        Write-Host "Sample data import had issues" -ForegroundColor Yellow
    }
} else {
    Write-Host "Sample data file not found: $sampleFile" -ForegroundColor Yellow
}

# Create .env file
Write-Host ""
Write-Host "Creating .env file..." -ForegroundColor Yellow
$envContent = "DATABASE_URL=$ConnectionString`nAPP_ENV=production`nPORT=8080"
$envContent | Out-File -FilePath ".env" -Encoding ASCII
Write-Host ".env file created!" -ForegroundColor Green

# Show test accounts
Write-Host ""
Write-Host "=== Test Accounts ===" -ForegroundColor Cyan
Write-Host "Password: password123" -ForegroundColor White
Write-Host ""
Write-Host "Patient: patient1@example.com"
Write-Host "Doctor: doctor1@hospital.com"
Write-Host "Nurse: nurse1@hospital.com"
Write-Host "Pharmacist: pharmacist1@hospital.com"
Write-Host "Admin: admin1@hospital.com"

Write-Host ""
Write-Host "=== Database Setup Complete! ===" -ForegroundColor Green
Write-Host "Usage examples:" -ForegroundColor Yellow
Write-Host "  .\setup-database-simple.ps1              # Use hashed passwords"
Write-Host "  .\setup-database-simple.ps1 -PlainPassword # Use plain text passwords"
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Run: mvn clean compile"
Write-Host "2. Deploy to Heroku or Railway"
Write-Host "3. Test with accounts above" 