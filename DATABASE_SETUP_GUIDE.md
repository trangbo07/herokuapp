# 🗄️ Hướng dẫn Thiết lập Cơ sở dữ liệu

## 📋 Các Tùy chọn Database

### 1. 🟢 **Heroku PostgreSQL** (KHUYẾN NGHỊ - MIỄN PHÍ)
- ✅ Miễn phí lên đến 10,000 rows
- ✅ Tự động backup
- ✅ Dễ deploy

### 2. 🔵 **Railway PostgreSQL** (MIỄN PHÍ)
- ✅ Miễn phí $5/tháng credit
- ✅ Dễ sử dụng

### 3. 🟡 **Neon PostgreSQL** (MIỄN PHÍ)
- ✅ Serverless PostgreSQL
- ✅ Miễn phí tier

### 4. 🔴 **Local SQL Server** (Phát triển)
- ⚠️ Chỉ dùng để test local

---

## 🚀 THIẾT LẬP HEROKU POSTGRESQL (KHUYẾN NGHỊ)

### Bước 1: Tạo App Heroku
```bash
# Tạo app mới
heroku create your-healthcare-app-name

# Thêm PostgreSQL addon (MIỄN PHÍ)
heroku addons:create heroku-postgresql:mini -a your-healthcare-app-name
```

### Bước 2: Lấy thông tin Database
```bash
# Xem thông tin database
heroku config -a your-healthcare-app-name

# Output sẽ có dạng:
# DATABASE_URL: postgres://username:password@hostname:port/database_name
```

### Bước 3: Import Schema
```bash
# Connect và import schema
heroku pg:psql -a your-healthcare-app-name < database/postgresql-schema.sql
```

---

## 🌐 THIẾT LẬP RAILWAY (ALTERNATIVE)

### Bước 1: Đăng ký Railway
1. Vào [railway.app](https://railway.app)
2. Đăng nhập bằng GitHub
3. Tạo New Project → Deploy PostgreSQL

### Bước 2: Lấy Connection String
1. Click vào PostgreSQL service
2. Tab "Connect" → Copy connection string
3. Format: `postgresql://username:password@hostname:port/database_name`

### Bước 3: Import Schema
Sử dụng Railway Web Console hoặc psql client

---

## 🌟 THIẾT LẬP NEON (SERVERLESS)

### Bước 1: Đăng ký Neon
1. Vào [neon.tech](https://neon.tech)
2. Tạo tài khoản miễn phí
3. Tạo database mới

### Bước 2: Lấy Connection String
1. Dashboard → Connection Details
2. Copy connection string

---

## 📊 IMPORT DỮ LIỆU MẪU

### Sử dụng psql command line:
```bash
# Kết nối database
psql "your-database-connection-string"

# Import schema
\i database/postgresql-schema.sql

# Import sample data (nếu có)
\i database/sample-data.sql
```

### Sử dụng Web Interface:
1. Truy cập database dashboard
2. Mở SQL console
3. Copy/paste nội dung file `postgresql-schema.sql`
4. Execute

---

## ⚙️ CẤU HÌNH ỨNG DỤNG

### File `DatabaseConfig.java` đã được cấu hình:
```java
// Tự động detect môi trường:
// - Production: Sử dụng DATABASE_URL từ Heroku
// - Local: Sử dụng SQL Server local
```

### Environment Variables cần thiết:
```bash
# Production (Heroku tự động set)
DATABASE_URL=postgresql://user:pass@host:port/dbname

# Local development (tùy chọn)
DB_HOST=localhost
DB_PORT=1433
DB_NAME=HealthCareSystem
DB_USER=sa
DB_PASSWORD=your_password
```

---

## 🔍 KIỂM TRA KẾT NỐI

### Test kết nối database:
```bash
# Heroku
heroku pg:info -a your-app-name

# Railway/Neon - sử dụng psql
psql "your-connection-string" -c "SELECT version();"
```

### Verify tables đã tạo:
```sql
-- List all tables
\dt

-- Check specific table
SELECT * FROM AccountPatient LIMIT 5;
```

---

## 🆘 TROUBLESHOOTING

### Lỗi thường gặp:

#### 1. Connection timeout
```bash
# Kiểm tra firewall/network
telnet hostname port
```

#### 2. Authentication failed
```bash
# Kiểm tra username/password
# Đảm bảo escape special characters trong URL
```

#### 3. Database không tồn tại
```bash
# Tạo database mới
createdb -h hostname -U username database_name
```

#### 4. Permission denied
```bash
# Kiểm tra user permissions
GRANT ALL PRIVILEGES ON DATABASE database_name TO username;
```

---

## 📝 NEXT STEPS

1. ✅ Chọn database provider (Heroku khuyến nghị)
2. ✅ Tạo database instance
3. ✅ Import schema từ `database/postgresql-schema.sql`
4. ✅ Cấu hình DATABASE_URL
5. ✅ Test kết nối
6. ✅ Deploy ứng dụng

**Bạn muốn sử dụng provider nào? Tôi sẽ hướng dẫn chi tiết từng bước!** 