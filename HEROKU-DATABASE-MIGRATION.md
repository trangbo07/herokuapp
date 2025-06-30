# 🔄 Database Migration Guide: SQL Server → PostgreSQL

## Tổng quan

Khi deploy lên Heroku, bạn cần chuyển từ SQL Server (local) sang PostgreSQL (cloud). Đây là hướng dẫn chi tiết migration.

## 📋 Bước 1: Chuẩn bị Migration

### Backup dữ liệu SQL Server (nếu có)
```sql
-- Export data từ SQL Server Management Studio
-- Chọn Database > Tasks > Generate Scripts
-- Chọn "Script Data Only" hoặc "Schema and Data"
-- Lưu thành file .sql
```

### Kiểm tra schema differences
| SQL Server | PostgreSQL | Ghi chú |
|------------|------------|---------|
| `IDENTITY(1,1)` | `SERIAL` | Auto increment |
| `NVARCHAR(MAX)` | `TEXT` | Large text |
| `NVARCHAR(100)` | `VARCHAR(100)` | String |
| `DATETIME` | `TIMESTAMP` | Date/time |
| `BIT` | `BOOLEAN` | Boolean |
| `GETDATE()` | `CURRENT_TIMESTAMP` | Current time |

## 🗄️ Bước 2: Schema Conversion

### Automated Script
Chúng ta đã tạo PostgreSQL schema tương thích:
```bash
# File schema đã được convert
database/postgresql-schema.sql
```

### Manual Migration (nếu cần)
```bash
# Chạy migration script
./migrate-to-heroku.sh

# Hoặc manual
heroku pg:psql --app your-app-name < database/postgresql-schema.sql
```

## 📊 Bước 3: Data Migration

### Export từ SQL Server
```sql
-- Script export data cho từng bảng
-- Ví dụ: AccountPatient
SELECT 
    'INSERT INTO AccountPatient (username, password, email, img, status) VALUES (' +
    '''' + username + '''' + ', ' +
    '''' + password + '''' + ', ' +
    '''' + email + '''' + ', ' +
    ISNULL('''' + img + '''', 'NULL') + ', ' +
    '''' + status + '''' + ');'
FROM AccountPatient;
```

### Convert syntax
```bash
# Thay thế SQL Server syntax với PostgreSQL
sed 's/GETDATE()/CURRENT_TIMESTAMP/g' data.sql > data_postgresql.sql
sed 's/NVARCHAR/VARCHAR/g' data_postgresql.sql > data_final.sql
```

### Import vào PostgreSQL
```bash
# Import data
heroku pg:psql --app your-app-name < data_final.sql
```

## 🔧 Bước 4: Code Updates

### Update DatabaseConfig
File `DatabaseConfig.java` đã được cập nhật để handle cả 2 database:
```java
// Tự động detect environment
if ("production".equals(env)) {
    return getHerokuPostgreSQLConnection(); // PostgreSQL
} else {
    return getLocalSQLServerConnection();  // SQL Server
}
```

### Update DAO Classes
Nếu có SQL queries sử dụng syntax specific:
```java
// SQL Server
String sql = "SELECT TOP 10 * FROM Patient";

// PostgreSQL  
String sql = "SELECT * FROM Patient LIMIT 10";

// Solution: Use DatabaseConfig helper
String sql = "SELECT " + DatabaseConfig.getLimitSyntax(10) + " * FROM Patient";
```

## 🚀 Bước 5: Deploy với Migration

### Automated Deploy
```bash
# Chạy script deploy tự động (đã include migration)
./heroku-deploy.sh

# Script sẽ:
# 1. Create Heroku app
# 2. Add PostgreSQL
# 3. Deploy code  
# 4. Run database migration
# 5. Insert sample data
```

### Manual Steps
```bash
# 1. Deploy app
git push heroku main

# 2. Create database schema  
heroku pg:psql --app your-app-name < database/postgresql-schema.sql

# 3. Import your data (if any)
heroku pg:psql --app your-app-name < your-data.sql

# 4. Verify tables
heroku pg:psql --app your-app-name -c "\dt"
```

## ✅ Bước 6: Verification

### Test Database Connection
```bash
# Connect to PostgreSQL
heroku pg:psql --app your-app-name

# Check tables
\dt

# Check sample data
SELECT * FROM AccountStaff LIMIT 5;
SELECT * FROM Patient LIMIT 5;
SELECT * FROM ListOfMedicalService LIMIT 5;
```

### Test Application
```bash
# Check logs
heroku logs --tail --app your-app-name

# Test endpoints
curl https://your-app-name.herokuapp.com/

# Test login functionality
```

## 📋 Data Mapping Reference

### AccountPatient
```sql
-- SQL Server
account_patient_id INT IDENTITY(1,1) PRIMARY KEY
username NVARCHAR(50)
password NVARCHAR(100)  
email NVARCHAR(100)
img NVARCHAR(200)
status NVARCHAR(30)

-- PostgreSQL  
account_patient_id SERIAL PRIMARY KEY
username VARCHAR(50)
password VARCHAR(100)
email VARCHAR(100) 
img VARCHAR(200)
status VARCHAR(30)
```

### Medicine (Complex Example)
```sql
-- SQL Server
medicine_id INT IDENTITY(1,1) PRIMARY KEY
name NVARCHAR(100) NOT NULL
ingredient NVARCHAR(MAX)
usage NVARCHAR(MAX)
preservation NVARCHAR(MAX)
manuDate DATE
expDate DATE
quantity INT
price DECIMAL(10,2)

-- PostgreSQL
medicine_id SERIAL PRIMARY KEY
name VARCHAR(100) NOT NULL
ingredient TEXT
usage TEXT
preservation TEXT
manuDate DATE
expDate DATE  
quantity INTEGER
price DECIMAL(10,2)
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. Foreign Key Constraints
```sql
-- Nếu có lỗi foreign key, tạm thời disable
SET session_replication_role = replica;
-- Import data
SET session_replication_role = DEFAULT;
```

#### 2. Sequence Issues
```sql
-- Reset sequence sau import
SELECT setval('accountpatient_account_patient_id_seq', 
    (SELECT MAX(account_patient_id) FROM AccountPatient));
```

#### 3. Data Type Mismatches
```sql
-- Check data types
\d AccountPatient

-- Cast if needed
UPDATE Patient SET dob = dob::DATE WHERE dob IS NOT NULL;
```

### Migration Validation
```bash
# Compare record counts
echo "SQL Server counts:"
# Run on SQL Server
SELECT 
    'AccountPatient' as TableName, COUNT(*) as Count FROM AccountPatient
UNION ALL
SELECT 'Patient', COUNT(*) FROM Patient
UNION ALL  
SELECT 'Doctor', COUNT(*) FROM Doctor;

echo "PostgreSQL counts:"
heroku pg:psql --app your-app-name -c "
SELECT 'AccountPatient' as TableName, COUNT(*) as Count FROM AccountPatient
UNION ALL
SELECT 'Patient', COUNT(*) FROM Patient  
UNION ALL
SELECT 'Doctor', COUNT(*) FROM Doctor;"
```

## 📝 Migration Checklist

- [ ] ✅ Backup original SQL Server database
- [ ] ✅ Convert schema to PostgreSQL format
- [ ] ✅ Export data from SQL Server  
- [ ] ✅ Convert data syntax for PostgreSQL
- [ ] ✅ Create Heroku app with PostgreSQL addon
- [ ] ✅ Deploy application code
- [ ] ✅ Create database schema
- [ ] ✅ Import converted data
- [ ] ✅ Test database connections
- [ ] ✅ Verify all tables and data
- [ ] ✅ Test application functionality
- [ ] ✅ Update DNS/domain settings

## 🔗 Useful Commands

```bash
# Database info
heroku pg:info --app your-app-name

# Database size  
heroku pg:psql --app your-app-name -c "SELECT pg_size_pretty(pg_database_size('your-db-name'));"

# Table sizes
heroku pg:psql --app your-app-name -c "
SELECT schemaname,tablename,pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size 
FROM pg_tables 
WHERE schemaname='public' 
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;"

# Backup database
heroku pg:backups:capture --app your-app-name

# Restore backup  
heroku pg:backups:restore b001 --app your-app-name
```

Migration hoàn tất! 🎉 App của bạn đã chạy với PostgreSQL trên Heroku. 