# Render Database Setup Script
# Healthcare Management System deployment on Render

param(
    [switch]$PlainPassword = $false,
    [string]$DatabaseUrl = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"
)

Write-Host "=== Healthcare System - Render Setup ===" -ForegroundColor Green
Write-Host ""

if ($PlainPassword) {
    Write-Host "Using PLAIN TEXT passwords for testing" -ForegroundColor Yellow
} else {
    Write-Host "Using HASHED passwords (production ready)" -ForegroundColor Green
}

Write-Host ""
Write-Host "Database URL: $DatabaseUrl" -ForegroundColor Cyan

# Parse connection string
if ($DatabaseUrl -match "postgresql://([^:]+):([^@]+)@([^:]+):?(\d*)/(.+)") {
    $dbUser = $matches[1]
    $dbPassword = $matches[2]
    $dbHost = $matches[3]
    $dbPort = if ($matches[4]) { $matches[4] } else { "5432" }
    $dbName = $matches[5]
    
    Write-Host ""
    Write-Host "Parsed Database Info:" -ForegroundColor Yellow
    Write-Host "  Host: $dbHost" -ForegroundColor White
    Write-Host "  Port: $dbPort" -ForegroundColor White
    Write-Host "  Database: $dbName" -ForegroundColor White
    Write-Host "  User: $dbUser" -ForegroundColor White
} else {
    Write-Host "❌ Invalid database URL format" -ForegroundColor Red
    exit 1
}

# Check PostgreSQL client
Write-Host ""
Write-Host "Checking PostgreSQL client..." -ForegroundColor Yellow
$psqlAvailable = $false
try {
    psql --version | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ PostgreSQL client available" -ForegroundColor Green
        $psqlAvailable = $true
    }
} catch {
    Write-Host "❌ psql not found" -ForegroundColor Red
}

if ($psqlAvailable) {
    # Test connection
    Write-Host ""
    Write-Host "Testing database connection..." -ForegroundColor Yellow
    $env:PGPASSWORD = $dbPassword
    
    psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT version();" > $null 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Database connection successful!" -ForegroundColor Green
        
        # Import schema
        Write-Host ""
        Write-Host "Importing database schema..." -ForegroundColor Yellow
        if (Test-Path "database/postgresql-schema.sql") {
            psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f "database/postgresql-schema.sql"
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ Schema imported successfully!" -ForegroundColor Green
            } else {
                Write-Host "⚠️ Schema import had issues (tables might exist)" -ForegroundColor Yellow
            }
        }
        
        # Import sample data
        Write-Host ""
        Write-Host "Importing sample data..." -ForegroundColor Yellow
        
        if ($PlainPassword) {
            $sampleFile = "database/sample-data-plain.sql"
        } else {
            $sampleFile = "database/sample-data.sql"
        }
        
        if (Test-Path $sampleFile) {
            psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $sampleFile
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ Sample data imported successfully!" -ForegroundColor Green
            } else {
                Write-Host "⚠️ Sample data import had issues" -ForegroundColor Yellow
            }
        }
        
    } else {
        Write-Host "❌ Database connection failed!" -ForegroundColor Red
        $psqlAvailable = $false
    }
}

if (-not $psqlAvailable) {
    Write-Host ""
    Write-Host "📋 MANUAL SETUP INSTRUCTIONS:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Go to your Render dashboard: https://dashboard.render.com" -ForegroundColor White
    Write-Host "2. Click on your PostgreSQL database" -ForegroundColor White
    Write-Host "3. Go to 'Connect' tab" -ForegroundColor White
    Write-Host "4. Click 'Open External Console' or use external tool" -ForegroundColor White
    Write-Host ""
    Write-Host "5. Copy and paste this schema file:" -ForegroundColor Yellow
    Write-Host "   database/postgresql-schema.sql" -ForegroundColor White
    Write-Host ""
    Write-Host "6. Copy and paste sample data:" -ForegroundColor Yellow
    if ($PlainPassword) {
        Write-Host "   database/sample-data-plain.sql" -ForegroundColor White
    } else {
        Write-Host "   database/sample-data.sql" -ForegroundColor White
    }
}

# Create environment files
Write-Host ""
Write-Host "Creating environment configuration..." -ForegroundColor Yellow

# Update .env file
$envContent = @"
# Render Environment Configuration
ENV=production
DATABASE_URL=$DatabaseUrl
PORT=8080

# Application Settings
APP_ENV=production

# VNPay Configuration
VNPAY_RETURN_URL=https://your-app.onrender.com/api/vnpay/return
VNPAY_NOTIFY_URL=https://your-app.onrender.com/api/vnpay/notify
"@

$envContent | Out-File -FilePath ".env" -Encoding UTF8
Write-Host "✅ .env file updated" -ForegroundColor Green

# Create render.yaml for easier deployment
$renderYaml = @"
services:
  - type: web
    name: healthcare-management-system
    env: docker
    dockerfilePath: ./Dockerfile
    envVars:
      - key: ENV
        value: production
      - key: DATABASE_URL
        value: $DatabaseUrl
      - key: PORT
        value: 8080
"@

$renderYaml | Out-File -FilePath "render.yaml" -Encoding UTF8
Write-Host "✅ render.yaml created for auto-deployment" -ForegroundColor Green

# Show test accounts
Write-Host ""
Write-Host "🔐 TEST ACCOUNTS:" -ForegroundColor Cyan
Write-Host "Password: password123" -ForegroundColor White
Write-Host ""
Write-Host "👤 Patient: patient1@example.com"
Write-Host "👨‍⚕️ Doctor: doctor1@hospital.com"
Write-Host "👩‍⚕️ Nurse: nurse1@hospital.com"
Write-Host "💊 Pharmacist: pharmacist1@hospital.com"
Write-Host "👨‍💼 Admin: admin1@hospital.com"

Write-Host ""
Write-Host "🚀 NEXT STEPS FOR RENDER DEPLOYMENT:" -ForegroundColor Green
Write-Host ""
Write-Host "1. Push to GitHub:" -ForegroundColor Yellow
Write-Host "   git add ." -ForegroundColor White
Write-Host "   git commit -m 'Ready for Render deployment'" -ForegroundColor White
Write-Host "   git push origin main" -ForegroundColor White
Write-Host ""
Write-Host "2. Create Web Service on Render:" -ForegroundColor Yellow
Write-Host "   - Go to https://dashboard.render.com" -ForegroundColor White
Write-Host "   - New + → Web Service" -ForegroundColor White
Write-Host "   - Connect your GitHub repo" -ForegroundColor White
Write-Host "   - Environment: Docker" -ForegroundColor White
Write-Host "   - Add environment variable: DATABASE_URL" -ForegroundColor White
Write-Host ""
Write-Host "3. Your app will be available at:" -ForegroundColor Yellow
Write-Host "   https://your-app-name.onrender.com" -ForegroundColor White

Write-Host ""
Write-Host "✅ Render setup completed!" -ForegroundColor Green 