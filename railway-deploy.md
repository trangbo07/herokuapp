# 🚄 Deploy FREE qua Railway.app

## Bước 1: Tạo tài khoản Railway
1. 🌐 Vào: https://railway.app/
2. 🔑 Sign up với GitHub
3. ✅ Authorize Railway access

## Bước 2: Deploy từ GitHub
1. 📱 Click "New Project"
2. 🔗 "Deploy from GitHub repo"
3. 🔍 Chọn repo: `trangbo07/herokuapp`
4. ✅ Click "Deploy Now"

## Bước 3: Add PostgreSQL Database
1. ➕ Click "New" > "Database" > "Add PostgreSQL"
2. 🗄️ Database sẽ tự động connect với app
3. ⚙️ Environment variables tự động setup

## Bước 4: Configure Environment
1. 🔧 Click vào app > "Variables"
2. ➕ Add:
   ```
   ENV=production
   TZ=Asia/Ho_Chi_Minh
   ```

## Bước 5: Access Website
- 🌐 URL: `https://your-app-name.up.railway.app`
- 👤 Login: admin / 123456
- 🗄️ Database: PostgreSQL (auto-configured)

## Custom Domain (Optional)
1. 🌍 Mua domain miễn phí tại Freenom.com
2. 🔗 Railway > Settings > Custom Domain
3. 📝 Add your domain
4. ⚙️ Update DNS records theo hướng dẫn

## ✅ HOÀN TOÀN MIỄN PHÍ!
- 🚄 Railway: 500h/month free
- 🗄️ PostgreSQL: Free tier
- 🌐 Subdomain: Free included
- 🌍 Custom domain: Free từ Freenom 