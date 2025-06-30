# 🚀 Hướng dẫn Deploy Healthcare Management System

## Tổng quan dự án
- **Technology Stack**: Java 17, Jakarta Servlet 6.0, Maven, SQL Server
- **Build Output**: WAR file
- **External Services**: VnPay Payment Gateway, Email Service

## 📋 Chuẩn bị trước khi deploy

### 1. Build dự án
```bash
# Chuyển đến thư mục dự án
cd SWP391_Group5

# Clean và build project
mvn clean package

# File WAR sẽ được tạo tại: target/SWP391-1.0-SNAPSHOT.war
```

### 2. Chuẩn bị Database
- **Database**: SQL Server
- **Connection**: Cần cập nhật connection string trong code
- **Tables**: Đảm bảo database đã có đầy đủ bảng và data

### 3. Cấu hình môi trường
- **VnPay**: Cập nhật `vnpay.properties` với domain mới
- **Email**: Cấu hình SMTP settings
- **Database**: Cập nhật connection string

## 🌐 Phương pháp 1: Deploy lên AWS (Khuyến nghị)

### A. Sử dụng AWS Elastic Beanstalk

#### Bước 1: Chuẩn bị AWS
1. Tạo tài khoản AWS (miễn phí 12 tháng)
2. Cài đặt AWS CLI
3. Tạo IAM user với quyền Elastic Beanstalk

#### Bước 2: Deploy
```bash
# Cài đặt EB CLI
pip install awsebcli

# Khởi tạo EB application
eb init

# Chọn:
# - Platform: Java
# - Platform version: Java 17 running on 64bit Amazon Linux 2023
# - Ứng dụng: Healthcare-Management-System

# Deploy
eb create production --elb-type application
```

#### Bước 3: Cấu hình Database
```bash
# Tạo RDS SQL Server
aws rds create-db-instance \
    --db-instance-identifier healthcare-db \
    --db-instance-class db.t3.micro \
    --engine sqlserver-ex \
    --allocated-storage 20 \
    --db-name healthcare \
    --master-username admin \
    --master-user-password YourPassword123!
```

### B. Sử dụng AWS EC2 + Tomcat

#### Bước 1: Tạo EC2 Instance
```bash
# Tạo EC2 instance (Ubuntu 22.04)
# Instance type: t2.micro (free tier)
# Security Group: HTTP (80), HTTPS (443), SSH (22)
```

#### Bước 2: Cài đặt môi trường
```bash
# SSH vào server
ssh -i your-key.pem ubuntu@your-ec2-ip

# Cập nhật system
sudo apt update && sudo apt upgrade -y

# Cài đặt Java 17
sudo apt install openjdk-17-jdk -y

# Cài đặt Tomcat 10
wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.34/bin/apache-tomcat-10.1.34.tar.gz
tar -xzf apache-tomcat-10.1.34.tar.gz
sudo mv apache-tomcat-10.1.34 /opt/tomcat
sudo chown -R ubuntu:ubuntu /opt/tomcat

# Cấu hình Tomcat service
sudo nano /etc/systemd/system/tomcat.service
```

#### Bước 3: Deploy WAR file
```bash
# Copy WAR file lên server
scp -i your-key.pem target/SWP391-1.0-SNAPSHOT.war ubuntu@your-ec2-ip:/opt/tomcat/webapps/

# Restart Tomcat
sudo systemctl restart tomcat
```

## 🌊 Phương pháp 2: Deploy lên Heroku

### Bước 1: Chuẩn bị Heroku
```bash
# Cài đặt Heroku CLI
# Download từ: https://devcenter.heroku.com/articles/heroku-cli

# Login
heroku login
```

### Bước 2: Tạo Procfile
```bash
# Tạo file Procfile trong root directory
echo "web: java \$JAVA_OPTS -jar target/dependency/webapp-runner.jar --port \$PORT target/*.war" > Procfile
```

### Bước 3: Cập nhật pom.xml
```xml
<!-- Thêm vào pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>copy</goal></goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>com.heroku</groupId>
                        <artifactId>webapp-runner</artifactId>
                        <version>9.0.89.0</version>
                        <destFileName>webapp-runner.jar</destFileName>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Bước 4: Deploy
```bash
# Tạo Heroku app
heroku create your-healthcare-app

# Set Java version
echo "java.runtime.version=17" > system.properties

# Deploy
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

## 🐳 Phương pháp 3: Docker Deployment

### Bước 1: Tạo Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

# Cài đặt Tomcat
RUN apt-get update && apt-get install -y wget
RUN wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.34/bin/apache-tomcat-10.1.34.tar.gz
RUN tar -xzf apache-tomcat-10.1.34.tar.gz
RUN mv apache-tomcat-10.1.34 /usr/local/tomcat

# Copy WAR file
COPY target/SWP391-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
```

### Bước 2: Build và Run
```bash
# Build Docker image
docker build -t healthcare-app .

# Run container
docker run -p 8080:8080 healthcare-app
```

### Bước 3: Deploy lên Docker Hub
```bash
# Tag image
docker tag healthcare-app your-dockerhub-username/healthcare-app

# Push to Docker Hub
docker push your-dockerhub-username/healthcare-app
```

## 🖥️ Phương pháp 4: VPS Deployment

### Bước 1: Chọn VPS Provider
- **DigitalOcean**: $5/tháng
- **Vultr**: $2.50/tháng
- **Linode**: $5/tháng
- **Hostinger VPS**: $3.99/tháng

### Bước 2: Cài đặt môi trường
```bash
# Cài đặt Java 17
sudo apt update
sudo apt install openjdk-17-jdk nginx -y

# Cài đặt Tomcat
wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.34/bin/apache-tomcat-10.1.34.tar.gz
tar -xzf apache-tomcat-10.1.34.tar.gz
sudo mv apache-tomcat-10.1.34 /opt/tomcat
```

### Bước 3: Cấu hình Nginx (Reverse Proxy)
```nginx
# /etc/nginx/sites-available/healthcare
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 📊 Database Setup

### SQL Server trên Azure
```bash
# Tạo Azure SQL Database
az sql server create \
  --name healthcare-sql-server \
  --resource-group healthcare-rg \
  --location eastus \
  --admin-user sqladmin \
  --admin-password YourPassword123!

az sql db create \
  --resource-group healthcare-rg \
  --server healthcare-sql-server \
  --name healthcare-db \
  --service-objective Basic
```

### Cập nhật Connection String
```java
// Trong code Java, cập nhật database connection
String connectionUrl = "jdbc:sqlserver://your-server.database.windows.net:1433;" +
                      "database=healthcare-db;" +
                      "user=sqladmin;" +
                      "password=YourPassword123!;" +
                      "encrypt=true;" +
                      "trustServerCertificate=false;" +
                      "hostNameInCertificate=*.database.windows.net;" +
                      "loginTimeout=30;";
```

## 🔧 Cấu hình Production

### 1. Cập nhật VnPay URLs
```properties
# src/main/webapp/WEB-INF/vnpay.properties
vnp_TmnCode=DIMMABD6
vnp_HashSecret=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA
vnp_Url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnp_Returnurl=https://your-domain.com/api/vnpay/return.html
```

### 2. SSL Certificate (Let's Encrypt)
```bash
# Cài đặt Certbot
sudo apt install certbot python3-certbot-nginx

# Tạo SSL certificate
sudo certbot --nginx -d your-domain.com
```

### 3. Environment Variables
```bash
# Tạo file .env
DB_HOST=your-sql-server.database.windows.net
DB_NAME=healthcare-db
DB_USER=sqladmin
DB_PASSWORD=YourPassword123!
VNPAY_TMN_CODE=DIMMABD6
VNPAY_HASH_SECRET=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA
```

## 🚀 Khuyến nghị Deploy nhanh nhất

**Cho người mới bắt đầu**: Sử dụng **Heroku** (miễn phí với giới hạn)
**Cho production**: Sử dụng **AWS Elastic Beanstalk**
**Cho budget thấp**: Sử dụng **VPS + Docker**

## 📞 Hỗ trợ
- Nếu gặp lỗi trong quá trình deploy, hãy check logs
- Đảm bảo firewall mở port cần thiết
- Test kỹ trước khi deploy production

## 🔍 Monitoring & Logs
```bash
# Xem logs Tomcat
tail -f /opt/tomcat/logs/catalina.out

# Xem logs Nginx
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
``` 