# Connect to Render PostgreSQL Database
# Updated connection string from Render dashboard

param(
    [switch]$PlainPassword = $false
)

# Updated External Database URL from Render
$DatabaseUrl = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a.oregon-postgres.render.com/healthcare_system"

Write-Host "=== Connecting to Render PostgreSQL Database ===" -ForegroundColor Green
Write-Host ""
Write-Host "Database URL: $DatabaseUrl" -ForegroundColor Cyan

# Parse connection string
if ($DatabaseUrl -match "postgresql://([^:]+):([^@]+)@([^/]+)/(.+)") {
    $dbUser = $matches[1]
    $dbPassword = $matches[2]
    $hostPort = $matches[3]
    $dbName = $matches[4]
    
    # Split host and port
    if ($hostPort -match "([^:]+):?(\d*)") {
        $dbHost = $matches[1]
        $dbPort = if ($matches[2]) { $matches[2] } else { "5432" }
    }
    
    Write-Host ""
    Write-Host "Connection Details:" -ForegroundColor Yellow
    Write-Host "  Host: $dbHost"
    Write-Host "  Port: $dbPort"
    Write-Host "  Database: $dbName"
    Write-Host "  User: $dbUser"
} else {
    Write-Host "Error: Invalid database URL format" -ForegroundColor Red
    exit 1
}

# Check for psql
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
    Write-Host "Testing connection..." -ForegroundColor Yellow
    $env:PGPASSWORD = $dbPassword
    
    $null = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT version();" 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Connection successful!" -ForegroundColor Green
        
        # Check if tables exist
        Write-Host ""
        Write-Host "Checking existing tables..." -ForegroundColor Yellow
        $tableCheck = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" -t
        $tableCount = $tableCheck.Trim()
        
        if ($tableCount -gt 0) {
            Write-Host "Found $tableCount existing tables" -ForegroundColor Green
            Write-Host "Database seems to be already set up!" -ForegroundColor Green
        } else {
            Write-Host "No tables found. Setting up database..." -ForegroundColor Yellow
            
            # Import schema
            if (Test-Path "database/postgresql-schema.sql") {
                Write-Host "Importing schema..." -ForegroundColor Yellow
                psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f "database/postgresql-schema.sql"
                Write-Host "Schema imported!" -ForegroundColor Green
            }
            
            # Import sample data
            if ($PlainPassword) {
                $dataFile = "database/sample-data-plain.sql"
                Write-Host "Importing plain text password data..." -ForegroundColor Yellow
            } else {
                $dataFile = "database/sample-data.sql"
                Write-Host "Importing hashed password data..." -ForegroundColor Yellow
            }
            
            if (Test-Path $dataFile) {
                psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $dataFile
                Write-Host "Sample data imported!" -ForegroundColor Green
            }
        }
        
        # Show final status
        Write-Host ""
        Write-Host "Final database status:" -ForegroundColor Cyan
        $finalTableCheck = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" -t
        $finalTableCount = $finalTableCheck.Trim()
        Write-Host "Total tables: $finalTableCount"
        
        # Show sample accounts
        try {
            $patientCount = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT COUNT(*) FROM AccountPatient;" -t 2>$null
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Patient accounts: $($patientCount.Trim())"
            }
        } catch {
            Write-Host "AccountPatient table not found or empty"
        }
        
    } else {
        Write-Host "Connection failed!" -ForegroundColor Red
        Write-Host "Please check your internet connection and database status." -ForegroundColor Yellow
    }
    
} else {
    Write-Host ""
    Write-Host "ALTERNATIVE: Manual Database Setup" -ForegroundColor Cyan
    Write-Host "1. Install PostgreSQL client: https://www.postgresql.org/download/"
    Write-Host "2. Or use online tool: https://adminer.org"
    Write-Host "3. Connect with these details:"
    Write-Host "   Host: $dbHost"
    Write-Host "   Port: $dbPort"
    Write-Host "   Database: $dbName"
    Write-Host "   User: $dbUser"
    Write-Host "   Password: $dbPassword"
}

# Update environment files
Write-Host ""
Write-Host "Updating configuration files..." -ForegroundColor Yellow

# Update .env
$envContent = @(
    "ENV=production",
    "DATABASE_URL=$DatabaseUrl",
    "PORT=8080",
    "APP_ENV=production"
)
$envContent | Out-File -FilePath ".env" -Encoding ASCII
Write-Host ".env updated with new database URL"

# Update render.yaml
$renderContent = @(
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
$renderContent | Out-File -FilePath "render.yaml" -Encoding ASCII
Write-Host "render.yaml updated"

Write-Host ""
Write-Host "Setup completed!" -ForegroundColor Green
Write-Host ""
Write-Host "TEST ACCOUNTS (password: password123):" -ForegroundColor Cyan
Write-Host "  Patient: patient1@example.com"
Write-Host "  Doctor: doctor1@hospital.com" 
Write-Host "  Admin: admin1@hospital.com"
Write-Host ""
Write-Host "NEXT: Deploy to Render with updated config!" -ForegroundColor Yellow 