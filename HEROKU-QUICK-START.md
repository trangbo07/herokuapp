# ⚡ Deploy lên Heroku trong 5 phút

## 🚀 Cách nhanh nhất

### 1. Chạy script tự động
```bash
# Linux/Mac
chmod +x heroku-deploy.sh
./heroku-deploy.sh

# Windows (PowerShell)
.\deploy.ps1
# Sau đó chọn option 2 (Heroku)
```

### 2. Manual Deploy (nếu script không chạy được)

#### Bước 1: Cài đặt Heroku CLI
```bash
# Download từ: https://devcenter.heroku.com/articles/heroku-cli
heroku login
```

#### Bước 2: Tạo app và database
```bash
heroku create your-healthcare-app
heroku addons:create heroku-postgresql:mini --app your-healthcare-app
```

#### Bước 3: Cấu hình environment
```bash
heroku config:set ENV=production --app your-healthcare-app
heroku config:set VNPAY_RETURN_URL=https://your-healthcare-app.herokuapp.com/api/vnpay/return.html --app your-healthcare-app
```

#### Bước 4: Deploy
```bash
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

#### Bước 5: Setup database
```bash
# Tạo database schema
heroku pg:psql --app your-healthcare-app < database/postgresql-schema.sql

# Hoặc chạy script migration
./migrate-to-heroku.sh
```

## 🎯 Kết quả

Sau khi deploy thành công:
- **URL**: `https://your-healthcare-app.herokuapp.com`
- **Database**: PostgreSQL (10,000 rows miễn phí)
- **SSL**: Tự động enabled
- **Monitoring**: Heroku metrics dashboard

## 🔧 Quan trọng

1. **Database**: Tự động chuyển từ SQL Server → PostgreSQL
2. **VnPay URLs**: Tự động cập nhật với domain Heroku
3. **Environment**: Tự động set production mode
4. **Sample Data**: Script sẽ hỏi có muốn insert không

## 🐛 Nếu có lỗi

```bash
# Xem logs
heroku logs --tail --app your-healthcare-app

# Restart app
heroku restart --app your-healthcare-app

# Kiểm tra config
heroku config --app your-healthcare-app
```

## 💡 Tips

- Free tier có giới hạn, app sẽ sleep sau 30 phút không hoạt động
- Upgrade lên Hobby ($7/tháng) để app không sleep
- PostgreSQL free tier: 10,000 rows, 1GB storage

**Thời gian deploy**: ~5-10 phút
**Chi phí**: Miễn phí (có giới hạn) 