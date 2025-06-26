# Service Order Separation - Hướng dẫn sử dụng

## Tổng quan
Đã tách phần Service Order ra khỏi trang Examination và tạo thành một trang riêng biệt để cải thiện trải nghiệm người dùng và dễ bảo trì hơn.

## Các thay đổi chính

### 1. Trang mới được tạo
- **File HTML**: `src/main/webapp/view/service-order.html` - Trang danh sách waitlist cần chỉ định dịch vụ
- **File HTML**: `src/main/webapp/view/service-order-details.html` - Trang chọn dịch vụ cho bệnh nhân cụ thể
- **File JavaScript**: `src/main/webapp/assets/jslogic/service-order.js` - Logic cho trang danh sách
- **File JavaScript**: `src/main/webapp/assets/jslogic/service-order-details.js` - Logic cho trang chi tiết

### 2. Trang Examination được cập nhật
- **File HTML**: `src/main/webapp/view/examination.html` - Loại bỏ phần Service Order Section
- **File JavaScript**: `src/main/webapp/assets/jslogic/examination-doctor.js` - Cập nhật để không chuyển hướng

## Luồng hoạt động mới

### Bước 1: Khám bệnh (Examination)
1. Bác sĩ chọn bệnh nhân từ danh sách waitlist
2. Nhập thông tin khám bệnh (triệu chứng, chẩn đoán sơ bộ)
3. Lưu thông tin khám bệnh
4. Hệ thống tự động cập nhật trạng thái waitlist từ "Initial" thành "InProgress"
5. Quay lại danh sách bệnh nhân

### Bước 2: Quản lý Service Order
1. Bác sĩ truy cập trang Service Order Management
2. Hệ thống hiển thị danh sách waitlist có status = "InProgress" và visittype = "Initial"
3. Bác sĩ nhấn nút "Chỉ định" cho bệnh nhân cần chỉ định dịch vụ
4. Chuyển sang trang Service Order Details

### Bước 3: Chỉ định dịch vụ (Service Order Details)
1. Trang Service Order Details hiển thị thông tin bệnh nhân
2. Bác sĩ chọn các dịch vụ cần thiết
3. Gán bác sĩ phụ trách cho từng dịch vụ
4. Tạo Service Order hoặc bỏ qua
5. Quay lại trang Service Order Management

## Cấu trúc URL và Parameters

### Chuyển hướng từ Service Order sang Service Order Details
```
service-order-details.html?waitlistId={id}&patientId={patientId}
```

### Parameters
- `waitlistId`: ID của waitlist (để lấy thông tin bệnh nhân và medicine record)
- `patientId`: ID của bệnh nhân

## Tính năng của trang Service Order Management

### 1. Hiển thị danh sách waitlist
- Chỉ hiển thị waitlist có status = "InProgress" và visittype = "Initial"
- Thông tin bệnh nhân: ID, Name, Age, Gender
- Thông tin waitlist: Room, Registered time, Estimated time, Appointment, Status, Visit type, Note, Shift

### 2. Nút hành động
- **Chỉ định**: Chuyển sang trang chọn dịch vụ cho bệnh nhân cụ thể

## Tính năng của trang Service Order Details

### 1. Hiển thị thông tin bệnh nhân
- Patient ID, Name, Age, Gender, Phone
- Medicine Record ID

### 2. Chọn dịch vụ
- Danh sách các dịch vụ có sẵn
- Mô tả và giá của từng dịch vụ
- Checkbox để chọn dịch vụ

### 3. Gán bác sĩ
- Dropdown để chọn bác sĩ cho từng dịch vụ
- Validation: Tất cả dịch vụ phải có bác sĩ được gán

### 4. Tổng kết
- Hiển thị danh sách dịch vụ đã chọn
- Tổng tiền
- Thông tin bác sĩ được gán

### 5. Hành động
- **Create Service Order**: Tạo đơn chỉ định dịch vụ
- **Skip Services**: Bỏ qua việc chỉ định dịch vụ
- **Back to Service Order**: Quay lại trang danh sách

## API Endpoints được sử dụng

### Service Order Management Page
- `GET /api/doctor/service-order?action=getServiceOrderWaitlist` - Lấy danh sách waitlist cho service order

### Service Order Details Page
- `GET /api/doctor/waitlist?action=detail&id={id}` - Lấy thông tin bệnh nhân
- `GET /api/doctor/examination?action=getMedicineRecord&waitlistId={id}` - Lấy medicine record ID
- `GET /api/doctor/service-order?action=getServices` - Lấy danh sách dịch vụ
- `GET /api/doctor/service-order?action=getDoctors` - Lấy danh sách bác sĩ
- `POST /api/doctor/service-order` - Tạo service order

### Examination Page
- `GET /api/doctor/waitlist?action=waitlist` - Lấy danh sách waitlist
- `GET /api/doctor/waitlist?action=detail&id={id}` - Lấy chi tiết waitlist
- `POST /api/doctor/examination` - Lưu thông tin khám bệnh
- `PUT /api/doctor/waitlist` - Cập nhật trạng thái waitlist

## Lợi ích của việc tách trang

### 1. Trải nghiệm người dùng tốt hơn
- Giao diện rõ ràng, tập trung vào từng bước
- Không bị rối mắt với quá nhiều thông tin
- Dễ dàng quay lại và chỉnh sửa
- Quản lý service order độc lập với khám bệnh

### 2. Dễ bảo trì
- Code được tách riêng, dễ quản lý
- Mỗi trang có trách nhiệm riêng biệt
- Dễ dàng thêm tính năng mới

### 3. Hiệu suất tốt hơn
- Chỉ load dữ liệu cần thiết cho từng trang
- Giảm thời gian load trang
- Tối ưu hóa memory usage

## Hướng dẫn sử dụng

### Cho bác sĩ
1. **Khám bệnh**: Truy cập trang Examination → Chọn bệnh nhân → Nhập thông tin khám bệnh → Lưu
2. **Quản lý Service Order**: Truy cập trang Service Order Management → Xem danh sách bệnh nhân cần chỉ định dịch vụ
3. **Chỉ định dịch vụ**: Nhấn nút "Chỉ định" → Chọn dịch vụ và gán bác sĩ → Tạo Service Order hoặc bỏ qua

### Cho developer
1. Để thêm tính năng mới vào Service Order Management, chỉnh sửa `service-order.js`
2. Để thay đổi giao diện Service Order Management, chỉnh sửa `service-order.html`
3. Để thêm tính năng mới vào Service Order Details, chỉnh sửa `service-order-details.js`
4. Để thay đổi giao diện Service Order Details, chỉnh sửa `service-order-details.html`
5. Để thay đổi logic khám bệnh, chỉnh sửa `examination-doctor.js`
6. Để thay đổi giao diện khám bệnh, chỉnh sửa `examination.html`

## Lưu ý quan trọng

1. **Filtering**: Trang Service Order Management chỉ hiển thị waitlist có status = "InProgress" và visittype = "Initial"
2. **Validation**: Tất cả dịch vụ được chọn phải có bác sĩ được gán
3. **Error Handling**: Có xử lý lỗi đầy đủ cho các API calls
4. **Loading States**: Có hiển thị loading spinner khi cần thiết
5. **Responsive Design**: Giao diện responsive cho mobile và desktop
6. **Breadcrumb Navigation**: Dễ dàng quay lại trang trước đó

## Troubleshooting

### Lỗi thường gặp
1. **No patients ready for service orders**: Kiểm tra có waitlist nào có status = "InProgress" và visittype = "Initial" không
2. **Missing medicine record ID**: Kiểm tra API examination có trả về medicineRecordId không
3. **Services not loading**: Kiểm tra API getServices có hoạt động không
4. **Doctors not loading**: Kiểm tra API getDoctors có hoạt động không

### Debug
- Mở Developer Tools (F12) để xem console logs
- Kiểm tra Network tab để xem API calls
- Kiểm tra Application tab để xem session storage 