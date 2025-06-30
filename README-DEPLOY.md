# 🏥 Healthcare Management System - Deploy Instructions

## 🚀 Deploy ngay lập tức

### Windows Users
```powershell
.\deploy.ps1
```

### Linux/Mac Users  
```bash
chmod +x deploy.sh
./deploy.sh
```

## 📋 Các tùy chọn Deploy có sẵn

1. **🐳 Docker (Local Testing)** - Khuyến nghị cho development
2. **☁️ Heroku (Cloud)** - Miễn phí với giới hạn  
3. **⚙️ Manual Upload** - Cho VPS/Dedicated server

## 🎯 Cách nhanh nhất

### Option 1: Docker (Recommend)
```bash
# Build và chạy local
mvn clean package
docker build -t healthcare-app .
docker run -p 8080:8080 healthcare-app

# Truy cập: http://localhost:8080
```

### Option 2: Heroku (Free)
```bash
# Cài Heroku CLI, sau đó:
heroku login
heroku create your-app-name
git push heroku main

# App sẽ có URL: https://your-app-name.herokuapp.com
```

## 🔧 Cần cấu hình

- **Database**: SQL Server connection string
- **VnPay**: Update return URL trong `vnpay.properties`  
- **Email**: SMTP configuration

## 📚 Xem thêm

- [Quick Start Guide](QUICK-START.md) - Hướng dẫn chi tiết
- [Deploy Guide](deploy-guide.md) - Tất cả phương pháp deploy
- [Docker Compose](docker-compose.yml) - Deploy với database

## 🆘 Cần hỗ trợ?

Chạy script auto và chọn option phù hợp. Script sẽ hướng dẫn chi tiết từng bước! 