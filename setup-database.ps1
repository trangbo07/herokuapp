# 🗄️ Database Setup Script for Healthcare Management System
# Sử dụng connection string để thiết lập database

param(
    [string]$ConnectionString = "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"
)

Write-Host "🏥 Healthcare Management System - Database Setup" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Kiểm tra psql có sẵn không
Write-Host "📋 Kiểm tra PostgreSQL client..." -ForegroundColor Yellow
$psqlExists = $false
try {
    $version = psql --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ PostgreSQL client đã sẵn sàng!" -ForegroundColor Green
        $psqlExists = $true
    }
} catch {
    Write-Host "❌ PostgreSQL client chưa được cài đặt!" -ForegroundColor Red
}

if (-not $psqlExists) {
    Write-Host "📥 Đang tải xuống PostgreSQL portable..." -ForegroundColor Yellow
    
    # Download portable PostgreSQL tools
    $pgUrl = "https://get.enterprisedb.com/postgresql/postgresql-15.4-1-windows-x64-binaries.zip"
    $pgZip = "$env:TEMP\postgresql.zip"
    $pgDir = "$env:TEMP\postgresql"
    
    try {
        Invoke-WebRequest -Uri $pgUrl -OutFile $pgZip
        Expand-Archive -Path $pgZip -DestinationPath $pgDir -Force
        $env:PATH += ";$pgDir\pgsql\bin"
        Write-Host "✅ PostgreSQL client đã được tải xuống!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Không thể tải PostgreSQL client. Vui lòng cài đặt thủ công." -ForegroundColor Red
        Write-Host "💡 Tải từ: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
        exit 1
    }
}

Write-Host ""
Write-Host "🔗 Connection String:" -ForegroundColor Cyan
Write-Host $ConnectionString -ForegroundColor White

# Test kết nối
Write-Host ""
Write-Host "🔍 Kiểm tra kết nối database..." -ForegroundColor Yellow
try {
    $testCmd = "psql `"$ConnectionString`" -c `"SELECT version();`""
    $testResult = Invoke-Expression $testCmd
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Kết nối database thành công!" -ForegroundColor Green
        Write-Host "📊 PostgreSQL version: $($testResult[2])" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Lỗi kết nối: $testResult" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Không thể kết nối database: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Import schema
Write-Host ""
Write-Host "📊 Import database schema..." -ForegroundColor Yellow
try {
    $schemaFile = "database/postgresql-schema.sql"
    if (Test-Path $schemaFile) {
        $importCmd = "psql `"$ConnectionString`" -f `"$schemaFile`""
        Invoke-Expression $importCmd
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Schema đã được import thành công!" -ForegroundColor Green
        } else {
            Write-Host "⚠️ Có lỗi khi import schema, nhưng có thể do tables đã tồn tại" -ForegroundColor Yellow
        }
    } else {
        Write-Host "❌ Không tìm thấy file schema: $schemaFile" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Lỗi import schema: $($_.Exception.Message)" -ForegroundColor Red
}

# Import sample data
Write-Host ""
Write-Host "📝 Import sample data..." -ForegroundColor Yellow
try {
    $sampleFile = "database/sample-data.sql"
    if (Test-Path $sampleFile) {
        $sampleCmd = "psql `"$ConnectionString`" -f `"$sampleFile`""
        Invoke-Expression $sampleCmd
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Sample data đã được import thành công!" -ForegroundColor Green
        } else {
            Write-Host "⚠️ Có lỗi khi import sample data" -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️ Không tìm thấy file sample data: $sampleFile" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️ Lỗi import sample data: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Verify tables
Write-Host ""
Write-Host "🔍 Kiểm tra tables đã tạo..." -ForegroundColor Yellow
try {
    $tablesCmd = "psql `"$ConnectionString`" -c `"\dt`""
    $tables = Invoke-Expression $tablesCmd
    $tableCount = ($tables | Where-Object {$_.Trim() -ne ""}).Count
    Write-Host "✅ Đã tạo $tableCount tables trong database!" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Không thể kiểm tra tables" -ForegroundColor Yellow
}

# Cấu hình environment variable
Write-Host ""
Write-Host "⚙️ Cấu hình environment variables..." -ForegroundColor Yellow

# Tạo .env file cho local development
$envContent = @"
# Database Configuration
DATABASE_URL=$ConnectionString

# Application Configuration
APP_ENV=production
PORT=8080

# VNPay Configuration (nếu cần)
VNPAY_RETURN_URL=https://your-app.herokuapp.com/api/vnpay/return
VNPAY_NOTIFY_URL=https://your-app.herokuapp.com/api/vnpay/notify
"@

$envContent | Out-File -FilePath ".env" -Encoding UTF8
Write-Host "✅ File .env đã được tạo!" -ForegroundColor Green

# Hiển thị thông tin accounts để test
Write-Host ""
Write-Host "🔑 Test Accounts (password: password123):" -ForegroundColor Cyan
Write-Host "👤 Patient: patient1@example.com" -ForegroundColor White
Write-Host "👨‍⚕️ Doctor: doctor1@hospital.com" -ForegroundColor White
Write-Host "👩‍⚕️ Nurse: nurse1@hospital.com" -ForegroundColor White
Write-Host "💊 Pharmacist: pharmacist1@hospital.com" -ForegroundColor White
Write-Host "👨‍💼 Admin: admin1@hospital.com" -ForegroundColor White

Write-Host ""
Write-Host "🎉 Database setup hoàn tất!" -ForegroundColor Green
Write-Host "📝 Next steps:" -ForegroundColor Yellow
Write-Host "   1. Chạy: mvn clean compile" -ForegroundColor White
Write-Host "   2. Deploy lên Heroku hoặc Railway" -ForegroundColor White
Write-Host "   3. Test ứng dụng với các account trên" -ForegroundColor White

Write-Host ""
Write-Host "🔗 Database Info:" -ForegroundColor Cyan
Write-Host "   Host: dpg-d1h425idbo4c739tq50g-a" -ForegroundColor White
Write-Host "   Database: healthcare_system" -ForegroundColor White
Write-Host "   User: healthcare_user" -ForegroundColor White
Write-Host "" 