# 🚀 Quick Start - Deploy Healthcare Management System

## ⚡ Cách nhanh nhất để deploy

### 1. **Deploy Local với Docker** (Khuyến nghị cho testing)
```bash
# Chạy script build và deploy tự động
chmod +x deploy.sh
./deploy.sh

# Hoặc trên Windows
.\deploy.ps1

# Chọn option 1 (Docker) khi được hỏi
```

### 2. **Deploy lên Heroku** (Miễn phí)
```bash
# Cài đặt Heroku CLI từ: https://devcenter.heroku.com/articles/heroku-cli

# Login và tạo app
heroku login
heroku create your-healthcare-app

# Deploy
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

### 3. **Deploy thủ công lên VPS**
```bash
# Build WAR file
mvn clean package

# Upload file target/SWP391-1.0-SNAPSHOT.war lên server
# Copy vào thư mục webapps của Tomcat
```

## 📋 Yêu cầu hệ thống

- **Java 17** hoặc cao hơn
- **Maven 3.6+**
- **SQL Server** (cho database)
- **Docker** (tùy chọn, cho local deployment)

## 🔧 Cấu hình cần thiết

### Database
- Tạo SQL Server database
- Import schema và data từ file SQL
- Cập nhật connection string trong code

### VnPay (Payment Gateway)
- File: `src/main/webapp/WEB-INF/vnpay.properties`
- Cập nhật `vnp_Returnurl` với domain mới của bạn

### Email Service
- Cấu hình SMTP settings trong code
- Thường ở file EmailService.java

## 🌐 URL sau khi deploy

| Platform | URL Example |
|----------|-------------|
| Local | http://localhost:8080 |
| Heroku | https://your-app-name.herokuapp.com |
| AWS | https://your-app.region.elasticbeanstalk.com |
| VPS | https://your-domain.com |

## 🐛 Troubleshooting

### Build thất bại
```bash
# Kiểm tra Java version
java -version

# Clean và build lại
mvn clean install -DskipTests
```

### Docker không chạy
```bash
# Kiểm tra Docker service
docker --version

# Xem logs container
docker logs healthcare-container
```

### Database connection lỗi
- Kiểm tra connection string
- Đảm bảo SQL Server đang chạy
- Kiểm tra firewall settings

## 💡 Tips

1. **Test local trước**: Luôn test ở local trước khi deploy production
2. **Environment variables**: Sử dụng environment variables cho sensitive data
3. **SSL certificate**: Sử dụng HTTPS cho production
4. **Monitoring**: Setup logging và monitoring cho production

## 📞 Hỗ trợ

Nếu gặp vấn đề, check các file log:
- Application logs: `/opt/tomcat/logs/catalina.out`
- Nginx logs: `/var/log/nginx/error.log`
- Docker logs: `docker logs container-name`

Xem thêm chi tiết tại: [deploy-guide.md](deploy-guide.md) 