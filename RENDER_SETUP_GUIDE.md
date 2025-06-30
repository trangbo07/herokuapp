# 🚀 Render Deployment Guide for Healthcare Management System

## 🎯 Tại sao chọn Render?
- ✅ Miễn phí PostgreSQL database
- ✅ Tự động deploy từ GitHub
- ✅ Web interface dễ sử dụng
- ✅ SSL certificates tự động

---

## 📊 BƯỚC 1: THIẾT LẬP DATABASE TRÊN RENDER

### 1.1 Tạo PostgreSQL Database

1. **Đăng nhập Render**
   - Vào [render.com](https://render.com)
   - Đăng nhập bằng GitHub

2. **Tạo Database**
   - Click "New +" → "PostgreSQL"
   - Name: `healthcare-database`
   - User: `healthcare_user` (hoặc để mặc định)
   - Region: `Singapore` (gần Việt Nam nhất)
   - Plan: **Free** (0$ - 1GB storage)

3. **Lấy Connection Info**
   - Sau khi tạo xong, click vào database
   - Tab "Info" → copy **External Database URL**
   - Format: `postgresql://username:password@hostname:port/database_name`

### 1.2 Import Database Schema

1. **Mở Database Dashboard**
   - Trong database page, click "Connect"
   - Chọn "External Connection" 
   - Click "Open psql" (hoặc sử dụng external tool)

2. **Import Schema**
   - Copy toàn bộ nội dung `database/postgresql-schema.sql`
   - Paste và execute

3. **Import Sample Data**
   - **Plain Text Password**: Copy `database/sample-data-plain.sql`
   - **Hashed Password**: Copy `database/sample-data.sql`
   - Paste và execute

---

## 🌐 BƯỚC 2: DEPLOY ỨNG DỤNG LÊN RENDER

### 2.1 Chuẩn bị GitHub Repository

1. **Push code lên GitHub** (nếu chưa có)
   ```bash
   git add .
   git commit -m "Ready for Render deployment"
   git push origin main
   ```

2. **Kiểm tra files cần thiết**
   - ✅ `Dockerfile` (đã có)
   - ✅ `pom.xml` (đã có)
   - ✅ Environment variables setup

### 2.2 Tạo Web Service trên Render

1. **Tạo Service**
   - Render Dashboard → "New +" → "Web Service"
   - Connect GitHub repository
   - Chọn repo của bạn

2. **Cấu hình Build**
   - **Name**: `healthcare-management-system`
   - **Region**: `Singapore`
   - **Branch**: `main`
   - **Runtime**: `Docker`
   - **Build Command**: Để trống (dùng Dockerfile)
   - **Start Command**: Để trống (dùng Dockerfile)

3. **Environment Variables**
   ```bash
   ENV=production
   DATABASE_URL=postgresql://your-database-connection-string
   PORT=8080
   ```

### 2.3 Cấu hình Environment Variables

Trong phần "Environment Variables", thêm:

```bash
# Database Configuration
DATABASE_URL=postgresql://healthcare_user:password@hostname:port/database

# Application Settings  
ENV=production
PORT=8080

# VNPay (nếu cần)
VNPAY_RETURN_URL=https://your-app.onrender.com/api/vnpay/return
```

---

## ⚙️ BƯỚC 3: KIỂM TRA VÀ DEPLOY

### 3.1 Verify Dockerfile

Đảm bảo Dockerfile có cấu hình đúng:
- Port 8080 được expose
- Maven build process
- Java 17 runtime

### 3.2 Deploy Process

1. **Sau khi cấu hình xong**
   - Click "Create Web Service"
   - Render sẽ tự động:
     - Clone repository
     - Build Docker image
     - Deploy ứng dụng

2. **Theo dõi Build**
   - Xem logs trong "Logs" tab
   - Build thường mất 5-10 phút

3. **Test Deployment**
   - URL: `https://your-app-name.onrender.com`
   - Test với test accounts

---

## 🔍 BƯỚC 4: SETUP DATABASE VỚI RENDER WEB CONSOLE

### 4.1 Truy cập Database Console

1. **Vào Database Service**
   - Render Dashboard → PostgreSQL service của bạn
   - Tab "Connect" → "Open External Console"

2. **Import Schema qua Web Interface**
   ```sql
   -- Copy paste toàn bộ nội dung database/postgresql-schema.sql
   -- Execute từng phần hoặc toàn bộ
   ```

3. **Import Sample Data**
   ```sql
   -- Option 1: Plain text passwords (recommended for testing)
   -- Copy paste database/sample-data-plain.sql
   
   -- Option 2: Hashed passwords (production ready)  
   -- Copy paste database/sample-data.sql
   ```

### 4.2 Verify Database

```sql
-- Kiểm tra tables
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Kiểm tra data
SELECT COUNT(*) FROM AccountPatient;
SELECT COUNT(*) FROM Doctor;
SELECT * FROM AccountPatient LIMIT 3;
```

---

## 🔐 TEST ACCOUNTS

**Mật khẩu: `password123`**

- **Patient**: patient1@example.com
- **Doctor**: doctor1@hospital.com  
- **Nurse**: nurse1@hospital.com
- **Pharmacist**: pharmacist1@hospital.com
- **Admin**: admin1@hospital.com

---

## 🆘 TROUBLESHOOTING

### Build Errors
- Kiểm tra Java version trong Dockerfile
- Đảm bảo Maven dependencies đúng

### Database Connection
- Verify DATABASE_URL environment variable
- Kiểm tra database status trên Render

### Application Not Starting
- Xem logs trong Render dashboard
- Kiểm tra port configuration (8080)

---

## 📝 QUICK START CHECKLIST

1. ✅ Tạo Render account
2. ✅ Tạo PostgreSQL database trên Render
3. ✅ Copy connection string của bạn: `postgresql://healthcare_user:...`
4. ✅ Import schema: `database/postgresql-schema.sql`
5. ✅ Import data: `database/sample-data-plain.sql`
6. ✅ Push code lên GitHub
7. ✅ Connect GitHub repo với Render Web Service
8. ✅ Set environment variables (DATABASE_URL, ENV=production)
9. ✅ Deploy và test!

**URL ứng dụng: `https://your-app-name.onrender.com`** 