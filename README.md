# 🏥 Healthcare Management System

Hệ thống quản lý y tế toàn diện được xây dựng với Java, Jakarta EE, và SQL Server/PostgreSQL.

## ✨ Tính năng chính

- 👥 **Quản lý tài khoản**: Bệnh nhân, Bác sĩ, Dược sĩ, Admin
- 📅 **Đặt lịch khám**: Hệ thống booking và quản lý appointment
- 🔬 **Khám bệnh**: Ghi nhận triệu chứng, chẩn đoán, kê đơn thuốc
- 🧪 **Dịch vụ cận lâm sàng**: Xét nghiệm, chụp chiếu, giao việc cho bác sĩ
- 💊 **Quản lý thuốc**: Kê đơn, phát thuốc, quản lý kho
- 💳 **Thanh toán**: VnPay integration, quản lý hóa đơn
- 📊 **Báo cáo**: Dashboard, thống kê, audit logs

## 🚀 Deploy nhanh

### Option 1: Docker (Khuyến nghị cho testing)
```bash
# Windows
.\deploy.ps1

# Linux/Mac  
chmod +x deploy.sh && ./deploy.sh

# Chọn option 1 (Docker)
```

### Option 2: Heroku (Cloud miễn phí)
```bash
# Tự động deploy toàn bộ
.\deploy.ps1  # Windows
./heroku-deploy.sh  # Linux/Mac

# Manual
heroku create your-app-name
git push heroku main
```

### Option 3: VPS/Server
```bash
# Build WAR file
mvn clean package

# Upload target/SWP391-1.0-SNAPSHOT.war lên Tomcat server
```

## 📋 Yêu cầu hệ thống

- **Java 17+**
- **Maven 3.6+**
- **Database**: SQL Server (local) hoặc PostgreSQL (Heroku)
- **Application Server**: Tomcat 10+ hoặc compatible Jakarta EE server

## 🔧 Cấu hình

### Database
- **Local**: SQL Server với connection string trong code
- **Production**: PostgreSQL (tự động chuyển đổi)

### VnPay Payment
```properties
# src/main/webapp/WEB-INF/vnpay.properties
vnp_TmnCode=DIMMABD6
vnp_HashSecret=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA
vnp_Returnurl=https://your-domain.com/api/vnpay/return.html
```

### Email Service
Cấu hình SMTP trong `EmailService.java` hoặc environment variables.

## 📚 Hướng dẫn chi tiết

- 📖 [QUICK-START.md](QUICK-START.md) - Hướng dẫn deploy nhanh
- 🚀 [deploy-guide.md](deploy-guide.md) - Tất cả phương pháp deploy  
- ☁️ [HEROKU-DEPLOY.md](HEROKU-DEPLOY.md) - Deploy lên Heroku với database
- ⚡ [HEROKU-QUICK-START.md](HEROKU-QUICK-START.md) - Deploy Heroku trong 5 phút
- 🏥 [ASSIGNED_SERVICES_README.md](ASSIGNED_SERVICES_README.md) - Hệ thống giao việc bác sĩ
- 🔬 [EXAMINATION_SYSTEM_README.md](EXAMINATION_SYSTEM_README.md) - Hệ thống khám bệnh

## 🌐 Demo URLs

| Platform | URL Example | Chi phí |
|----------|-------------|---------|
| Local | http://localhost:8080 | Miễn phí |
| Heroku | https://your-app.herokuapp.com | Miễn phí (có giới hạn) |
| AWS | https://your-app.region.elasticbeanstalk.com | ~$10/tháng |
| VPS | https://your-domain.com | ~$5/tháng |

## 🛠️ Tech Stack

- **Backend**: Java 17, Jakarta Servlet 6.0, Maven
- **Database**: SQL Server / PostgreSQL  
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap
- **Payment**: VnPay Gateway
- **Email**: JavaMail API
- **Security**: BCrypt password hashing
- **Deploy**: Docker, Heroku, AWS Elastic Beanstalk

## 🏗️ Kiến trúc

```
├── src/main/java/
│   ├── controller/     # Servlet controllers
│   ├── dao/           # Database access objects  
│   ├── dto/           # Data transfer objects
│   ├── model/         # Entity models
│   ├── util/          # Utilities (Email, VnPay)
│   └── config/        # Configuration classes
├── src/main/webapp/
│   ├── assets/        # CSS, JS, Images
│   ├── view/          # HTML pages
│   └── WEB-INF/       # Web config, properties
└── database/          # SQL scripts
```

## 🔐 Bảo mật

- ✅ Password hashing với BCrypt
- ✅ SQL injection prevention
- ✅ XSS protection
- ✅ CSRF protection
- ✅ Input validation
- ✅ Audit logging
- ✅ Role-based access control

## 📊 Monitoring

### Local Development
```bash
# View application logs
tail -f logs/application.log

# Database queries
# Check SQL Server Management Studio
```

### Production (Heroku)
```bash
# Real-time logs
heroku logs --tail --app your-app-name

# Database metrics
heroku pg:info --app your-app-name

# App metrics
heroku apps:info your-app-name
```

## 🤝 Đóng góp

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

Dự án này được phát triển cho mục đích giáo dục trong khóa học SWP391.

## 🆘 Hỗ trợ

- 📧 Email: support@healthcare-system.com
- 📱 Issues: [GitHub Issues](https://github.com/your-repo/issues)
- 📚 Wiki: [Project Wiki](https://github.com/your-repo/wiki)

## 🎯 Roadmap

- [ ] Mobile responsive design
- [ ] REST API documentation  
- [ ] Unit tests coverage
- [ ] Performance optimization
- [ ] Multi-language support
- [ ] Real-time notifications
- [ ] Advanced reporting dashboard

---

**⚡ Bắt đầu ngay**: Chạy `.\deploy.ps1` (Windows) hoặc `./deploy.sh` (Linux/Mac) để deploy trong 5 phút! 