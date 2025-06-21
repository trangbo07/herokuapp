# Clinical Examination System - Doctor Module

## Tổng quan
Hệ thống khám bệnh lâm sàng cho bác sĩ với workflow hoàn chỉnh: khám bệnh → chỉ định dịch vụ cận lâm sàng → hoàn thành.

## Workflow
1. **Chọn bệnh nhân** - Xem danh sách cuộc hẹn sắp tới
2. **Khám bệnh** - Nhập triệu chứng và chẩn đoán sơ bộ
3. **Chỉ định dịch vụ** - Chọn dịch vụ cận lâm sàng (tùy chọn)
4. **Hoàn thành** - Cập nhật trạng thái cuộc hẹn

## API Endpoints

### Examination API
- `POST /api/doctor/examination` - Lưu kết quả khám bệnh
- `GET /api/doctor/examination?action=getByMedicineRecord&medicineRecordId={id}` - Lấy kết quả khám theo medicine record

### Service Order API
- `GET /api/doctor/service-order?action=getServices` - Lấy danh sách tất cả dịch vụ
- `POST /api/doctor/service-order` - Tạo đơn dịch vụ mới
- `GET /api/doctor/service-order?action=getServiceOrder&serviceOrderId={id}` - Lấy chi tiết đơn dịch vụ với items
- `GET /api/doctor/service-order?action=getServiceOrdersByMedicineRecord&medicineRecordId={id}` - Lấy danh sách đơn dịch vụ theo medicine record
- `GET /api/doctor/service-order?action=getServiceOrdersByDoctor` - Lấy danh sách đơn dịch vụ của bác sĩ hiện tại

### Appointment API
- `POST /api/doctor/appointment` - Lấy danh sách cuộc hẹn theo trạng thái
- `GET /api/doctor/appointment?action=detail&id={id}` - Lấy chi tiết cuộc hẹn
- `PUT /api/doctor/appointment` - Cập nhật trạng thái cuộc hẹn

## Files chính
- `DoctorExaminationServlet.java` - API khám bệnh
- `DoctorServiceOrderServlet.java` - API đơn dịch vụ (đã hoàn thiện với getServiceOrderWithDetails)
- `examination.html` - Giao diện chính
- `examination-doctor.js` - Logic frontend
- `service-order-details.html` - Trang xem chi tiết và lịch sử service orders
- `service-order-details.js` - Logic cho trang service order details

## Cách sử dụng
1. Truy cập `/examination.html` để thực hiện khám bệnh và tạo service orders
2. Truy cập `/service-order-details.html` để xem chi tiết và lịch sử service orders
3. Sử dụng các chức năng tìm kiếm để khám phá dữ liệu

## Trang Service Order Details

### Tính năng chính:
- **Tìm kiếm theo Service Order ID**: Xem chi tiết một đơn dịch vụ cụ thể
- **Tìm kiếm theo Medicine Record ID**: Xem lịch sử đơn dịch vụ của bệnh nhân
- **Lịch sử của bác sĩ**: Xem tất cả đơn dịch vụ do bác sĩ hiện tại tạo
- **Demo API**: Test các API endpoints mới

### Giao diện:
- Hiển thị thông tin chi tiết đơn dịch vụ
- Danh sách các dịch vụ với giá tiền
- Tổng tiền đơn dịch vụ
- Lịch sử với khả năng xem chi tiết từng đơn

## Database Tables
- `ExamResult` - Kết quả khám bệnh
- `ServiceOrder` - Đơn dịch vụ
- `ServiceOrderItem` - Chi tiết dịch vụ
- `ListOfMedicalService` - Danh sách dịch vụ

## Tính năng mới đã hoàn thiện

### Service Order Details API
- **getServiceOrderWithDetails**: Lấy chi tiết đơn dịch vụ bao gồm:
  - Thông tin đơn dịch vụ (ID, ngày tạo, bác sĩ, bệnh nhân)
  - Danh sách các dịch vụ được chọn với giá tiền
  - Tổng tiền đơn dịch vụ

### Service Order History APIs
- **getServiceOrdersByMedicineRecord**: Xem lịch sử đơn dịch vụ của một bệnh nhân
- **getServiceOrdersByDoctor**: Xem lịch sử đơn dịch vụ của bác sĩ hiện tại

### Response Format
```json
{
  "success": true,
  "data": {
    "serviceOrder": {
      "service_order_id": 1,
      "doctor_id": 1,
      "medicineRecord_id": 1,
      "order_date": "2024-01-15 10:30:00",
      "doctor_name": "Dr. John Doe",
      "patient_name": "Jane Smith"
    },
    "items": [
      {
        "service_order_item_id": 1,
        "service_id": 1,
        "service_name": "Blood Test",
        "service_description": "Complete blood count",
        "service_price": 150000.0,
        "doctor_name": "Dr. John Doe"
      }
    ],
    "totalAmount": 150000.0
  },
  "message": "Service order details retrieved successfully"
}
```

## Cấu trúc Database

### Bảng chính
- `ExamResult` - Lưu kết quả khám bệnh
- `MedicineRecords` - Hồ sơ thuốc của bệnh nhân
- `ServiceOrder` - Đơn dịch vụ
- `ServiceOrderItem` - Chi tiết các dịch vụ trong đơn
- `ListOfMedicalService` - Danh sách các dịch vụ y tế có sẵn

## Files chính

### Backend
- `src/main/java/controller/DoctorExaminationServlet.java` - Servlet xử lý API khám bệnh
- `src/main/java/controller/DoctorServiceOrderServlet.java` - Servlet xử lý API đơn dịch vụ
- `src/main/java/controller/DoctorAppointmentServlet.java` - Servlet xử lý API cuộc hẹn
- `src/main/java/dao/ExamResultDAO.java` - DAO để xử lý thao tác với bảng ExamResult
- `src/main/java/dao/MedicineRecordDAO.java` - DAO để xử lý thao tác với bảng MedicineRecords
- `src/main/java/dao/ServiceOrderDAO.java` - DAO để xử lý thao tác với bảng ServiceOrder
- `src/main/java/dao/ServiceOrderItemDAO.java` - DAO để xử lý thao tác với bảng ServiceOrderItem
- `src/main/java/dao/ListOfMedicalServiceDAO.java` - DAO để xử lý thao tác với bảng ListOfMedicalService

### Models
- `src/main/java/model/ExamResult.java` - Model cho kết quả khám bệnh
- `src/main/java/model/MedicineRecords.java` - Model cho hồ sơ thuốc
- `src/main/java/model/ServiceOrder.java` - Model cho đơn dịch vụ
- `src/main/java/model/ServiceOrderItem.java` - Model cho chi tiết đơn dịch vụ
- `src/main/java/model/ListOfMedicalService.java` - Model cho danh sách dịch vụ y tế

### DTOs
- `src/main/java/dto/ServiceOrderDTO.java` - DTO cho ServiceOrder với thông tin chi tiết
- `src/main/java/dto/ServiceOrderItemDTO.java` - DTO cho ServiceOrderItem với thông tin dịch vụ

### Frontend
- `src/main/webapp/view/examination.html` - Giao diện khám bệnh
- `src/main/webapp/assets/jslogic/examination-doctor.js` - JavaScript xử lý logic khám bệnh

## Cách sử dụng

### 1. Truy cập trang khám bệnh
- Đăng nhập với tài khoản bác sĩ
- Truy cập `/examination.html`

### 2. Chọn bệnh nhân
- Xem danh sách cuộc hẹn sắp tới
- Click "Select for Examination" để chọn bệnh nhân

### 3. Thực hiện khám bệnh
- Điền thông tin triệu chứng
- Đưa ra chẩn đoán sơ bộ
- Click "Save Examination"

### 4. Chỉ định dịch vụ (Tùy chọn)
- Chọn các dịch vụ cận lâm sàng cần thiết
- Xem tổng chi phí
- Click "Create Service Order" hoặc "Skip Services"

### 5. Hoàn thành
- Hệ thống tự động quay lại danh sách bệnh nhân
- Cuộc hẹn được đánh dấu là "Completed"

## Tính năng đặc biệt

### Lưu tiến độ
- Hệ thống tự động lưu medicine record ID
- Có thể quay lại chỉ định dịch vụ sau này nếu cần

### Validation
- Kiểm tra đầy đủ thông tin trước khi lưu
- Hiển thị thông báo lỗi rõ ràng

### Responsive Design
- Giao diện thân thiện với mobile
- Sử dụng Bootstrap cho styling

## Lưu ý kỹ thuật

### Database
- Đảm bảo các bảng đã được tạo với đúng cấu trúc
- Kiểm tra foreign key constraints

### Session Management
- Yêu cầu đăng nhập với role "Doctor"
- Kiểm tra quyền truy cập cho mỗi API

### Error Handling
- Xử lý lỗi database gracefully
- Hiển thị thông báo lỗi user-friendly

## Troubleshooting

### Lỗi thường gặp
1. **Không thể lưu examination**: Kiểm tra kết nối database và quyền truy cập
2. **Không hiển thị danh sách services**: Kiểm tra dữ liệu trong bảng ListOfMedicalService
3. **Lỗi session**: Đảm bảo đã đăng nhập với tài khoản bác sĩ

### Debug
- Kiểm tra console browser cho lỗi JavaScript
- Kiểm tra server logs cho lỗi backend
- Sử dụng browser developer tools để debug API calls 