# Simple Render Setup Script for Healthcare Management System

param(
    [switch]$PlainPassword = $false
)

$DatabaseUrl = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"

Write-Host "=== Healthcare System - Render Setup ===" -ForegroundColor Green
Write-Host ""

if ($PlainPassword) {
    Write-Host "Using PLAIN TEXT passwords" -ForegroundColor Yellow
} else {
    Write-Host "Using HASHED passwords" -ForegroundColor Green
}

# Parse database URL
if ($DatabaseUrl -match "postgresql://([^:]+):([^@]+)@([^:]+):?(\d*)/(.+)") {
    $dbUser = $matches[1]
    $dbPassword = $matches[2]
    $dbHost = $matches[3]
    $dbPort = if ($matches[4]) { $matches[4] } else { "5432" }
    $dbName = $matches[5]
    
    Write-Host ""
    Write-Host "Database Info:" -ForegroundColor Yellow
    Write-Host "  Host: $dbHost"
    Write-Host "  Port: $dbPort"
    Write-Host "  Database: $dbName"
    Write-Host "  User: $dbUser"
} else {
    Write-Host "Error: Invalid database URL" -ForegroundColor Red
    exit 1
}

# Check PostgreSQL client
Write-Host ""
Write-Host "Checking PostgreSQL client..." -ForegroundColor Yellow
$psqlAvailable = $false

try {
    $null = psql --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "PostgreSQL client found!" -ForegroundColor Green
        $psqlAvailable = $true
    }
} catch {
    Write-Host "PostgreSQL client not found" -ForegroundColor Red
}

if ($psqlAvailable) {
    # Test connection
    Write-Host ""
    Write-Host "Testing database connection..." -ForegroundColor Yellow
    $env:PGPASSWORD = $dbPassword
    
    $null = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT version();" 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database connection successful!" -ForegroundColor Green
        
        # Import schema
        Write-Host ""
        Write-Host "Importing schema..." -ForegroundColor Yellow
        if (Test-Path "database/postgresql-schema.sql") {
            psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f "database/postgresql-schema.sql"
            Write-Host "Schema import completed"
        }
        
        # Import data
        Write-Host ""
        Write-Host "Importing sample data..." -ForegroundColor Yellow
        
        if ($PlainPassword) {
            $dataFile = "database/sample-data-plain.sql"
        } else {
            $dataFile = "database/sample-data.sql"
        }
        
        if (Test-Path $dataFile) {
            psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $dataFile
            Write-Host "Sample data import completed"
        }
        
    } else {
        Write-Host "Database connection failed!" -ForegroundColor Red
        $psqlAvailable = $false
    }
}

# Manual instructions if psql not available
if (-not $psqlAvailable) {
    Write-Host ""
    Write-Host "MANUAL SETUP INSTRUCTIONS:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Go to Render dashboard: https://dashboard.render.com"
    Write-Host "2. Click on your PostgreSQL database"
    Write-Host "3. Go to Connect tab"
    Write-Host "4. Open External Console"
    Write-Host ""
    Write-Host "5. Copy and paste schema file content:"
    Write-Host "   database/postgresql-schema.sql"
    Write-Host ""
    Write-Host "6. Copy and paste sample data:"
    if ($PlainPassword) {
        Write-Host "   database/sample-data-plain.sql"
    } else {
        Write-Host "   database/sample-data.sql"
    }
}

# Create .env file
Write-Host ""
Write-Host "Creating .env file..." -ForegroundColor Yellow

$envLines = @(
    "ENV=production",
    "DATABASE_URL=$DatabaseUrl",
    "PORT=8080",
    "APP_ENV=production"
)

$envLines | Out-File -FilePath ".env" -Encoding ASCII
Write-Host ".env file created!"

# Create render.yaml
Write-Host ""
Write-Host "Creating render.yaml..." -ForegroundColor Yellow

$renderLines = @(
    "services:",
    "  - type: web",
    "    name: healthcare-management-system", 
    "    env: docker",
    "    dockerfilePath: ./Dockerfile",
    "    envVars:",
    "      - key: ENV",
    "        value: production",
    "      - key: DATABASE_URL", 
    "        value: $DatabaseUrl",
    "      - key: PORT",
    "        value: 8080"
)

$renderLines | Out-File -FilePath "render.yaml" -Encoding ASCII
Write-Host "render.yaml created!"

# Show test accounts
Write-Host ""
Write-Host "TEST ACCOUNTS:" -ForegroundColor Cyan
Write-Host "Password: password123"
Write-Host ""
Write-Host "Patient: patient1@example.com"
Write-Host "Doctor: doctor1@hospital.com"
Write-Host "Nurse: nurse1@hospital.com"
Write-Host "Pharmacist: pharmacist1@hospital.com"
Write-Host "Admin: admin1@hospital.com"

Write-Host ""
Write-Host "NEXT STEPS:" -ForegroundColor Green
Write-Host ""
Write-Host "1. Push to GitHub:"
Write-Host "   git add ."
Write-Host "   git commit -m 'Ready for Render'"
Write-Host "   git push origin main"
Write-Host ""
Write-Host "2. Create Web Service on Render:"
Write-Host "   - Go to https://dashboard.render.com"
Write-Host "   - New + -> Web Service"
Write-Host "   - Connect GitHub repo"
Write-Host "   - Use Docker environment"
Write-Host "   - Set DATABASE_URL environment variable"
Write-Host ""
Write-Host "3. App URL: https://your-app-name.onrender.com"

Write-Host ""
Write-Host "Setup completed!" -ForegroundColor Green 