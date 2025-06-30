# 🚀 Deploy lên Heroku với Database

## Tổng quan
Heroku không hỗ trợ SQL Server miễn phí, nên chúng ta sẽ:
1. Chuyển từ SQL Server sang PostgreSQL
2. Deploy ứng dụng + database lên Heroku
3. Cấu hình environment variables

## 📋 Bước 1: Chuẩn bị Heroku

### Cài đặt Heroku CLI
```bash
# Download từ: https://devcenter.heroku.com/articles/heroku-cli
# Hoặc
npm install -g heroku

# Login
heroku login
```

### Tạo Heroku App
```bash
# Tạo app mới
heroku create your-healthcare-app

# Thêm PostgreSQL database (miễn phí)
heroku addons:create heroku-postgresql:mini --app your-healthcare-app

# Thêm các addons khác (optional)
heroku addons:create papertrail:choklad --app your-healthcare-app  # Logging
```

## 🗄️ Bước 2: Chuyển đổi Database

### Cập nhật dependencies trong pom.xml
Thêm PostgreSQL driver:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

### Tạo Database Configuration cho Production
Tạo file `src/main/java/config/DatabaseConfig.java`:
```java
public class DatabaseConfig {
    public static String getConnectionUrl() {
        String env = System.getenv("ENV");
        if ("production".equals(env)) {
            return System.getenv("DATABASE_URL");
        } else {
            // Local SQL Server
            return "jdbc:sqlserver://localhost:1433;databaseName=healthcare;encrypt=false";
        }
    }
}
```

## 🔧 Bước 3: Cấu hình Environment Variables

```bash
# Set environment variables
heroku config:set ENV=production --app your-healthcare-app
heroku config:set JAVA_TOOL_OPTIONS="-Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8" --app your-healthcare-app

# VnPay configuration
heroku config:set VNPAY_TMN_CODE=DIMMABD6 --app your-healthcare-app
heroku config:set VNPAY_HASH_SECRET=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA --app your-healthcare-app
heroku config:set VNPAY_RETURN_URL=https://your-healthcare-app.herokuapp.com/api/vnpay/return.html --app your-healthcare-app

# Email configuration (example with Gmail)
heroku config:set SMTP_HOST=smtp.gmail.com --app your-healthcare-app
heroku config:set SMTP_PORT=587 --app your-healthcare-app
heroku config:set SMTP_USERNAME=your-email@gmail.com --app your-healthcare-app
heroku config:set SMTP_PASSWORD=your-app-password --app your-healthcare-app
```

## 📊 Bước 4: Migration Database Schema

### Tạo file SQL cho PostgreSQL
Tạo file `database/postgresql-schema.sql`:
```sql
-- Example schema conversion from SQL Server to PostgreSQL
CREATE TABLE IF NOT EXISTS account_patient (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    gender VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add other tables here...
-- Convert SQL Server data types to PostgreSQL equivalents
-- IDENTITY -> SERIAL
-- NVARCHAR -> VARCHAR
-- DATETIME -> TIMESTAMP
-- BIT -> BOOLEAN
```

### Script migration data
Tạo file `migrate-to-heroku.sh`:
```bash
#!/bin/bash
# Export data từ SQL Server và import vào Heroku PostgreSQL

echo "🔄 Migrating database to Heroku PostgreSQL..."

# Get database URL from Heroku
DATABASE_URL=$(heroku config:get DATABASE_URL --app your-healthcare-app)

# Create tables
psql "$DATABASE_URL" < database/postgresql-schema.sql

echo "✅ Database migration completed!"
```

## 🚀 Bước 5: Deploy Application

### Chuẩn bị Git repository
```bash
# Initialize git (nếu chưa có)
git init
git add .
git commit -m "Initial commit for Heroku deploy"

# Add Heroku remote
heroku git:remote -a your-healthcare-app
```

### Deploy
```bash
# Deploy to Heroku
git push heroku main

# Check logs
heroku logs --tail --app your-healthcare-app

# Open app in browser
heroku open --app your-healthcare-app
```

## 🛠️ Bước 6: Cấu hình Production

### Scale dyno (optional)
```bash
# Scale to hobby dyno (có phí $7/tháng, nhiều resource hơn)
heroku ps:scale web=1:hobby --app your-healthcare-app
```

### SSL Certificate (tự động)
```bash
# Heroku tự động cung cấp SSL cho custom domain
heroku certs:auto:enable --app your-healthcare-app
```

### Custom Domain (optional)
```bash
# Add custom domain
heroku domains:add www.yourdomain.com --app your-healthcare-app
heroku domains:add yourdomain.com --app your-healthcare-app
```

## 🔍 Troubleshooting

### Xem logs
```bash
heroku logs --tail --app your-healthcare-app
```

### Connect to database
```bash
heroku pg:psql --app your-healthcare-app
```

### Restart app
```bash
heroku restart --app your-healthcare-app
```

### Check config vars
```bash
heroku config --app your-healthcare-app
```

## 📱 Monitoring

### Enable metrics (optional)
```bash
heroku labs:enable runtime-dyno-metadata --app your-healthcare-app
```

### View app metrics
```bash
heroku logs --tail --app your-healthcare-app | grep "heroku"
```

## 💰 Chi phí Heroku

### Free Tier (có giới hạn)
- 550-1000 dyno hours/tháng
- App sleep sau 30 phút không hoạt động
- PostgreSQL: 10,000 rows
- Không custom domain SSL

### Hobby Tier ($7/tháng)
- App không sleep
- PostgreSQL: 10 million rows
- Custom domain với SSL
- Metrics dashboard

## 🔄 Update Application

```bash
# Sau khi có thay đổi code
git add .
git commit -m "Update description"
git push heroku main
```

## 🌐 URLs sau khi deploy

- **App URL**: `https://your-healthcare-app.herokuapp.com`
- **Admin**: `https://your-healthcare-app.herokuapp.com/admin`
- **API**: `https://your-healthcare-app.herokuapp.com/api/*`

## 🔒 Security

### Environment Variables
- Không commit sensitive data vào Git
- Sử dụng `heroku config:set` cho passwords, API keys
- Enable 2FA cho Heroku account

### Database Security
```bash
# Backup database
heroku pg:backups:capture --app your-healthcare-app
heroku pg:backups --app your-healthcare-app
```

## 📞 Support

Nếu gặp vấn đề:
1. Check `heroku logs --tail`
2. Verify config vars: `heroku config`
3. Test database connection: `heroku pg:psql`
4. Restart app: `heroku restart`

## 🚨 Lưu ý quan trọng

1. **Free tier có giới hạn**: App sẽ sleep nếu không có traffic
2. **Database size**: Free PostgreSQL chỉ 10,000 rows
3. **Monthly hours**: Free tier có giới hạn giờ chạy/tháng
4. **Performance**: Free tier performance thấp, upgrade lên Hobby nếu cần

Sau khi deploy thành công, app sẽ có URL: `https://your-healthcare-app.herokuapp.com` 