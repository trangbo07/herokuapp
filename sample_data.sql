-- Dữ liệu mẫu cho bảng ListOfMedicalService
-- Chạy script này để thêm các dịch vụ cận lâm sàng mẫu

INSERT INTO ListOfMedicalService (name, description, price) VALUES
('Xét nghiệm máu', 'Xét nghiệm công thức máu toàn bộ (CBC)', 150000.00),
('Xét nghiệm nước tiểu', 'Phân tích nước tiểu tổng quát', 80000.00),
('X-quang ngực', 'Chụp X-quang phổi thẳng và nghiêng', 200000.00),
('Siêu âm bụng', 'Siêu âm ổ bụng tổng quát', 300000.00),
('Điện tâm đồ', 'Ghi điện tim (ECG)', 120000.00),
('Nội soi dạ dày', 'Nội soi đường tiêu hóa trên', 500000.00),
('Xét nghiệm đường huyết', 'Định lượng glucose máu', 60000.00),
('Xét nghiệm chức năng gan', 'AST, ALT, Bilirubin', 180000.00),
('Xét nghiệm chức năng thận', 'Ure, Creatinine', 120000.00),
('Chụp CT đầu', 'Chụp cắt lớp vi tính sọ não', 800000.00),
('Chụp MRI', 'Chụp cộng hưởng từ', 1200000.00),
('Xét nghiệm hormone tuyến giáp', 'TSH, T3, T4', 250000.00),
('Xét nghiệm lipid máu', 'Cholesterol, Triglyceride', 150000.00),
('Xét nghiệm đông máu', 'PT, APTT, Fibrinogen', 200000.00),
('Xét nghiệm viêm gan', 'HBsAg, Anti-HCV', 300000.00);

-- Kiểm tra dữ liệu đã được thêm
SELECT * FROM ListOfMedicalService ORDER BY name; 