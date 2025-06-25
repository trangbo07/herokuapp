# Assigned Services System - Doctor Module

## Tổng quan
Hệ thống quản lý dịch vụ được giao cho bác sĩ với workflow hoàn chỉnh: chỉ định dịch vụ → giao bác sĩ → nhập kết quả → hoàn thành.

## Workflow mới

### 1. Chỉ định dịch vụ với bác sĩ chịu trách nhiệm
- **Trang**: `examination.html`
- **Quy trình**:
  1. Bác sĩ khám bệnh và hoàn thành examination
  2. Chọn các dịch vụ cận lâm sàng cần thiết
  3. **MỚI**: Chọn bác sĩ chịu trách nhiệm cho từng dịch vụ
  4. Tạo service order với thông tin bác sĩ được chỉ định

### 2. Bác sĩ xem danh sách dịch vụ được giao
- **Trang**: `assigned-services.html`
- **Quy trình**:
  1. Bác sĩ đăng nhập và vào trang "Assigned Services"
  2. Xem danh sách tất cả dịch vụ được giao cho mình
  3. Xem thông tin bệnh nhân và kết quả khám bệnh
  4. Nhập kết quả xét nghiệm cho từng dịch vụ

## Các thay đổi chính

### 1. Cập nhật Service Order Form
- Thêm dropdown chọn bác sĩ cho mỗi service
- Validation: Bắt buộc chọn bác sĩ cho tất cả services
- Hiển thị thông tin bác sĩ được chọn trong summary

### 2. Trang Assigned Services mới
- Giao diện hiện đại và dễ sử dụng
- Hiển thị đầy đủ thông tin bệnh nhân và examination
- Modal nhập kết quả xét nghiệm
- Quản lý trạng thái (Pending/Completed)

### 3. Database Schema
- Sử dụng bảng `ResultsOfParaclinicalServices` có sẵn để lưu kết quả
- Cập nhật `ServiceOrderItem`: Đã có sẵn trường `doctor_id`
- Kết quả được lưu dưới dạng text với format: "Test Results: ...\n\nConclusion: ...\n\nStatus: ..."

## API Endpoints mới

### Service Order API (cập nhật)
- `POST /api/doctor/service-order` - Tạo service order với doctor assignment
- `GET /api/doctor/service-order?action=getDoctors` - Lấy danh sách bác sĩ
- `GET /api/doctor/service-order?action=getAssignedServices` - Lấy services được giao

### Service Result API (mới)
- `POST /api/doctor/service-result` - Lưu/cập nhật kết quả xét nghiệm

## Files đã thêm/sửa đổi

### Backend
- `ServiceResult.java` - Model cho kết quả xét nghiệm (cập nhật để phù hợp với bảng có sẵn)
- `ServiceResultDAO.java` - DAO cho ServiceResult (cập nhật để sử dụng ResultsOfParaclinicalServices)
- `DoctorServiceResultServlet.java` - API endpoint cho service result
- `AccountStaffDAO.java` - Thêm method getAllDoctors()
- `ServiceOrderItemDAO.java` - Cập nhật getAssignedServicesByDoctorId()
- `DoctorServiceOrderServlet.java` - Cập nhật để xử lý doctor assignment

### Frontend
- `assigned-services.html` - Trang mới cho bác sĩ xem assigned services
- `assigned-services.js` - Logic cho trang assigned services
- `examination-doctor.js` - Cập nhật để hỗ trợ chọn bác sĩ

## Hướng dẫn sử dụng

### Cho bác sĩ khám bệnh:
1. Vào trang Examination
2. Chọn bệnh nhân và khám bệnh
3. Chọn services cần thiết
4. **Chọn bác sĩ chịu trách nhiệm cho từng service**
5. Tạo service order

### Cho bác sĩ thực hiện xét nghiệm:
1. Vào trang "Assigned Services"
2. Xem danh sách services được giao
3. Click "Enter Results" cho service cần thực hiện
4. Nhập kết quả xét nghiệm, conclusion và status (tùy chọn)
5. Lưu kết quả

## Lưu ý quan trọng
- Sử dụng bảng `ResultsOfParaclinicalServices` có sẵn trong database
- Tất cả services phải được gán bác sĩ trước khi tạo service order
- Bác sĩ chỉ có thể nhập kết quả cho services được giao cho mình
- Hệ thống hỗ trợ cập nhật kết quả nếu cần thiết
- Kết quả được lưu dưới dạng text với format có cấu trúc để dễ hiển thị 