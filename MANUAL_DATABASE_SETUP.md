# 🗄️ HƯỚNG DẪN THIẾT LẬP DATABASE THỦ CÔNG

Vì máy bạn chưa có PostgreSQL client, bạn có thể setup database bằng web interface.

## 🔗 Thông tin Database của bạn:
```
Host: dpg-d1h425idbo4c739tq50g-a
Database: healthcare_system
User: healthcare_user
Password: qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1
Connection String: postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system
```

## 📊 CÁCH 1: Sử dụng Railway Web Console

1. **Truy cập Railway Dashboard**
   - Vào [railway.app](https://railway.app)
   - Đăng nhập và tìm project PostgreSQL của bạn

2. **Mở Database Console**
   - Click vào PostgreSQL service
   - Tab "Data" hoặc "Query"
   - Sẽ mở SQL console

3. **Import Schema**
   - Copy toàn bộ nội dung file `database/postgresql-schema.sql`
   - Paste vào SQL console
   - Click Execute/Run

4. **Import Sample Data** (Chọn 1 trong 2)
   
   **Option A: Hashed Passwords (Khuyến nghị)**
   - Copy toàn bộ nội dung file `database/sample-data.sql`
   - Paste vào SQL console
   - Click Execute/Run
   
   **Option B: Plain Text Passwords (Dễ test)**
   - Copy toàn bộ nội dung file `database/sample-data-plain.sql`
   - Paste vào SQL console
   - Click Execute/Run

## 🌐 CÁCH 2: Sử dụng Online PostgreSQL Client

1. **Truy cập pgAdmin Web**
   - Vào: [adminer.org](https://www.adminer.org/)
   - Hoặc bất kỳ online PostgreSQL client nào

2. **Kết nối Database**
   - System: PostgreSQL
   - Server: dpg-d1h425idbo4c739tq50g-a
   - Username: healthcare_user
   - Password: qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1
   - Database: healthcare_system

3. **Import Schema và Data**
   - Copy/paste nội dung file SQL như cách 1

## 💻 CÁCH 3: Cài đặt PostgreSQL Client (Khuyến nghị)

1. **Tải PostgreSQL**
   - Vào: https://www.postgresql.org/download/windows/
   - Tải bản installer
   - Cài đặt (chỉ cần psql client)

2. **Chạy Script**
   ```powershell
   .\setup-database-simple.ps1
   ```

## 🔧 CÁCH 4: Sử dụng Docker (Nếu có Docker)

```bash
# Pull PostgreSQL client image
docker run --rm -it postgres:15 psql "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system"

# Import schema
docker run --rm -v ${PWD}:/workspace postgres:15 psql "postgresql://healthcare_user:qeHJQMpooeNMjBqVZkuKWL8VPnO2IdV1@dpg-d1h425idbo4c739tq50g-a/healthcare_system" -f /workspace/database/postgresql-schema.sql
```

## ✅ KIỂM TRA DATABASE ĐÃ SETUP

Sau khi import xong, chạy câu lệnh SQL này để kiểm tra:

```sql
-- Kiểm tra tables đã tạo
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Kiểm tra sample data
SELECT COUNT(*) as patient_count FROM AccountPatient;
SELECT COUNT(*) as doctor_count FROM Doctor;
SELECT COUNT(*) as medicine_count FROM Medicine;
```

Kết quả mong đợi:
- Khoảng 40+ tables
- 3 patients, 2 doctors, 5 medicines

## 🔐 TEST ACCOUNTS

Sau khi setup xong, bạn có thể test với các account sau:

**Mật khẩu cho tất cả: `password123`**

- **Patient**: patient1@example.com
- **Doctor**: doctor1@hospital.com  
- **Nurse**: nurse1@hospital.com
- **Pharmacist**: pharmacist1@hospital.com
- **Admin**: admin1@hospital.com

## 🚀 BƯỚC TIẾP THEO

1. ✅ Setup database (bạn đang làm)
2. ✅ Cấu hình DATABASE_URL trong ứng dụng
3. ✅ Build và deploy ứng dụng
4. ✅ Test với các account trên

## 🆘 TROUBLESHOOTING

**Lỗi kết nối:**
- Kiểm tra internet connection
- Đảm bảo database service đang chạy
- Kiểm tra firewall

**Tables đã tồn tại:**
- Bình thường, có thể bỏ qua lỗi này

**Permission denied:**
- Kiểm tra username/password
- Đảm bảo user có quyền CREATE tables

---

**Bạn muốn dùng cách nào? Tôi khuyến nghị Cách 1 (Railway Web Console) vì đơn giản nhất!** 