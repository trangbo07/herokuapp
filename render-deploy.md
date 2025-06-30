# 🎨 Deploy FREE qua Render.com

## Bước 1: Tạo tài khoản
1. 🌐 Vào: https://render.com/
2. 🔑 Sign up với GitHub
3. ✅ Authorize Render access

## Bước 2: Deploy Web Service
1. 📱 Click "New +" > "Web Service"
2. 🔗 Connect repository: `trangbo07/herokuapp`
3. ⚙️ Settings:
   ```
   Name: healthcare-system
   Region: Oregon (US West)
   Branch: main
   Build Command: mvn clean package -DskipTests
   Start Command: java -jar target/dependency/webapp-runner.jar --port $PORT target/*.war
   ```

## Bước 3: Add Environment Variables
```
ENV=production
TZ=Asia/Ho_Chi_Minh
```

## Bước 4: Add PostgreSQL Database
1. 📱 "New +" > "PostgreSQL"
2. 🗄️ Name: healthcare-db
3. 🔗 Link với Web Service

## Bước 5: Setup Database Schema
1. 💻 Connect via psql
2. 📋 Copy nội dung `database/postgresql-schema.sql`
3. ✅ Execute script

## ✅ HOÀN TOÀN MIỄN PHÍ!
- 🎨 Render: Free tier
- 🗄️ PostgreSQL: 90 days free
- 🌐 URL: `https://healthcare-system.onrender.com`
- ⚠️ App sleep sau 15 phút không dùng 