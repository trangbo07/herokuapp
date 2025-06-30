-- Healthcare Management System - PostgreSQL Schema for Heroku
-- This script creates the database schema compatible with PostgreSQL
-- Converted from SQL Server schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ====================================================================
-- Account benh nhan 
CREATE TABLE IF NOT EXISTS AccountPatient (
    account_patient_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    img VARCHAR(200),
    status VARCHAR(30) CHECK (status IN ('Enable', 'Disable'))
);

CREATE TABLE IF NOT EXISTS Patient (
    patient_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    dob DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Patient_AccountPatient (
    patient_id INTEGER,
    account_patient_id INTEGER,
    PRIMARY KEY (patient_id, account_patient_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (account_patient_id) REFERENCES AccountPatient(account_patient_id)
);

-- ====================================================================
-- Account cua nhan vien
CREATE TABLE IF NOT EXISTS AccountStaff (
    account_staff_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(30) CHECK (role IN ('Doctor', 'Nurse', 'Receptionist', 'AdminSys', 'AdminBusiness')),
    email VARCHAR(100) UNIQUE NOT NULL,
    img VARCHAR(200),
    status VARCHAR(30) CHECK (status IN ('Enable', 'Disable'))
);

CREATE TABLE IF NOT EXISTS Doctor (
    doctor_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    eduLevel VARCHAR(50),
    account_staff_id INTEGER UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE IF NOT EXISTS Nurse (
    nurse_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    eduLevel VARCHAR(50),
    account_staff_id INTEGER UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE IF NOT EXISTS Receptionist (
    receptionist_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    account_staff_id INTEGER UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE IF NOT EXISTS AdminBusiness (
    admin_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    account_staff_id INTEGER UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE IF NOT EXISTS AdminSystem (
    admin_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    account_staff_id INTEGER UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

-- ====================================================================
-- Account cua duoc si
CREATE TABLE IF NOT EXISTS AccountPharmacist (
    account_pharmacist_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(30) CHECK (status IN ('Enable', 'Disable')),
    img VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS Pharmacist (
    pharmacist_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    eduLevel VARCHAR(50),
    account_pharmacist_id INTEGER UNIQUE,
    FOREIGN KEY (account_pharmacist_id) REFERENCES AccountPharmacist(account_pharmacist_id)
);

-- ====================================================================
-- Quan ly ho so benh an
CREATE TABLE IF NOT EXISTS MedicineRecords (
    medicineRecord_id SERIAL PRIMARY KEY,
    patient_id INTEGER,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE IF NOT EXISTS Appointment (
    appointment_id SERIAL PRIMARY KEY,
    doctor_id INTEGER,
    patient_id INTEGER,
    appointment_datetime TIMESTAMP,
    receptionist_id INTEGER,
    shift VARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL DEFAULT 'Morning',
    status VARCHAR(50) CHECK (status IN ('Pending', 'Confirmed', 'Completed', 'Cancelled')),
    note TEXT,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (receptionist_id) REFERENCES Receptionist(receptionist_id)
);

CREATE TABLE IF NOT EXISTS Diagnosis (
    diagnosis_id SERIAL PRIMARY KEY,
    doctor_id INTEGER,
    medicineRecord_id INTEGER,
    conclusion TEXT,
    disease TEXT,
    treatment_plan TEXT,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE IF NOT EXISTS ExamResult (
    exam_result_id SERIAL PRIMARY KEY,
    medicineRecord_id INTEGER,
    symptoms TEXT,
    preliminary_diagnosis TEXT,
    doctor_id INTEGER,
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE IF NOT EXISTS ListOfMedicalService (
    service_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2)
);

-- ====================================================================
-- Don thuoc
CREATE TABLE IF NOT EXISTS Prescription (
    prescription_id SERIAL PRIMARY KEY,
    medicineRecord_id INTEGER,
    doctor_id INTEGER,
    prescription_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(30) CHECK (status IN ('Pending', 'Dispensed', 'Cancelled')),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE IF NOT EXISTS Invoice (
    invoice_id SERIAL PRIMARY KEY,
    patient_id INTEGER,
    medicineRecord_id INTEGER,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12,2),
    status VARCHAR(30) CHECK (status IN ('Pending', 'Paid', 'Cancelled')),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE IF NOT EXISTS PrescriptionInvoice (
    prescription_invoice_id SERIAL PRIMARY KEY,
    invoice_id INTEGER UNIQUE,
    pharmacist_id INTEGER,
    prescription_id INTEGER,
    FOREIGN KEY (prescription_id) REFERENCES Prescription(prescription_id),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (pharmacist_id) REFERENCES Pharmacist(pharmacist_id)
);

CREATE TABLE IF NOT EXISTS ServiceOrder (
    service_order_id SERIAL PRIMARY KEY,
    doctor_id INTEGER,
    order_date DATE,
    medicineRecord_id INTEGER,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE IF NOT EXISTS ServiceOrderItem (
    service_order_item_id SERIAL PRIMARY KEY,
    service_order_id INTEGER,
    service_id INTEGER,
    doctor_id INTEGER,
    FOREIGN KEY (service_id) REFERENCES ListOfMedicalService(service_id),
    FOREIGN KEY (service_order_id) REFERENCES ServiceOrder(service_order_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE IF NOT EXISTS ServiceInvoice (
    service_invoice_id SERIAL PRIMARY KEY,
    invoice_id INTEGER,
    service_order_item_id INTEGER NOT NULL,
    quantity INTEGER DEFAULT 1,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (service_order_item_id) REFERENCES ServiceOrderItem(service_order_item_id)
);

CREATE TABLE IF NOT EXISTS ResultsOfParaclinicalServices (
    result_id SERIAL PRIMARY KEY,
    service_order_item_id INTEGER UNIQUE,
    result_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_order_item_id) REFERENCES ServiceOrderItem(service_order_item_id)
);

-- ====================================================================
-- Kho va thuoc
CREATE TABLE IF NOT EXISTS Warehouse (
    warehouse_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Unit (
    unit_id INTEGER PRIMARY KEY NOT NULL,
    unitName VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS Category (
    category_id INTEGER PRIMARY KEY NOT NULL,
    categoryName VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS Medicine (
    medicine_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_id INTEGER,
    category_id INTEGER,
    ingredient TEXT,
    usage TEXT,
    preservation TEXT,
    manuDate DATE,
    expDate DATE,
    quantity INTEGER,
    price DECIMAL(10,2),
    warehouse_id INTEGER,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouse(warehouse_id),
    FOREIGN KEY (unit_id) REFERENCES Unit(unit_id),
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE IF NOT EXISTS Medicines (
    prescription_invoice_id INTEGER,
    medicine_id INTEGER,
    quantity INTEGER,
    dosage VARCHAR(100),
    PRIMARY KEY (prescription_invoice_id, medicine_id),
    FOREIGN KEY (prescription_invoice_id) REFERENCES PrescriptionInvoice(prescription_invoice_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- ====================================================================
-- Thanh toan
CREATE TABLE IF NOT EXISTS Payment (
    payment_id SERIAL PRIMARY KEY,
    amount DECIMAL(12,2),
    payment_type VARCHAR(30) CHECK (payment_type IN ('Service', 'Prescription')),
    invoice_id INTEGER,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) DEFAULT 'Pending',
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id)
);

-- ====================================================================
-- Phan hoi tu benh nhan
CREATE TABLE IF NOT EXISTS Feedback (
    feedback_id SERIAL PRIMARY KEY,
    patient_id INTEGER,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

-- ====================================================================
-- Thong bao noi bo
CREATE TABLE IF NOT EXISTS InternalAnnouncement (
    announcement_id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    content TEXT,
    created_by_admin_business INTEGER,
    created_by_admin_system INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_admin_business) REFERENCES AdminBusiness(admin_id),
    FOREIGN KEY (created_by_admin_system) REFERENCES AdminSystem(admin_id)
);

-- ====================================================================
-- Lich su hanh dong he thong
CREATE TABLE IF NOT EXISTS SystemLog_Staff (
    log_id SERIAL PRIMARY KEY,
    account_staff_id INTEGER,
    action VARCHAR(255),
    action_type VARCHAR(50),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE IF NOT EXISTS SystemLog_Patient (
    log_id SERIAL PRIMARY KEY,
    account_patient_id INTEGER,
    action VARCHAR(255),
    action_type VARCHAR(50),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_patient_id) REFERENCES AccountPatient(account_patient_id)
);

CREATE TABLE IF NOT EXISTS SystemLog_Pharmacist (
    log_id SERIAL PRIMARY KEY,
    account_pharmacist_id INTEGER,
    action VARCHAR(255),
    action_type VARCHAR(50),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_pharmacist_id) REFERENCES AccountPharmacist(account_pharmacist_id)
);

-- ====================================================================
-- Phong va lich lam viec
CREATE TABLE IF NOT EXISTS Room (
    room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    status VARCHAR(30) CHECK (status IN ('Available', 'Occupied', 'Maintenance')) DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS Waitlist (
    waitlist_id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    doctor_id INTEGER NOT NULL,
    room_id INTEGER,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_time TIMESTAMP,
    visittype VARCHAR(30) CHECK (visittype IN ('Initial', 'Result')),
    status VARCHAR(30) CHECK (status IN ('Waiting', 'InProgress', 'Skipped', 'Completed')) DEFAULT 'Waiting',
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE IF NOT EXISTS DoctorRoomAssignment (
    assignment_id SERIAL PRIMARY KEY,
    doctor_id INTEGER NOT NULL,
    room_id INTEGER NOT NULL,
    assignment_date DATE NOT NULL,
    shift VARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL,
    UNIQUE (doctor_id, assignment_date, shift),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE IF NOT EXISTS DoctorSchedule (
    schedule_id SERIAL PRIMARY KEY,
    doctor_id INTEGER NOT NULL,
    working_date DATE NOT NULL,
    shift VARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL,
    room_id INTEGER,
    is_available BOOLEAN DEFAULT TRUE,
    note VARCHAR(255),
    UNIQUE (doctor_id, working_date, shift),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

-- ====================================================================
-- Nha cung cap va nhap kho
CREATE TABLE IF NOT EXISTS Distributor (
    DistributorID INTEGER PRIMARY KEY NOT NULL,
    DistributorName VARCHAR(255),
    Address TEXT,
    DistributorEmail VARCHAR(255),
    DistributorPhone VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS ImportInfo (
    ImportID INTEGER PRIMARY KEY NOT NULL,
    DistributorID INTEGER,
    medicine_id INTEGER,
    ImportDate DATE,
    ImportAmount INTEGER,
    FOREIGN KEY (DistributorID) REFERENCES Distributor(DistributorID),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- ====================================================================
-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_accountpatient_username ON AccountPatient(username);
CREATE INDEX IF NOT EXISTS idx_accountpatient_email ON AccountPatient(email);
CREATE INDEX IF NOT EXISTS idx_accountstaff_username ON AccountStaff(username);
CREATE INDEX IF NOT EXISTS idx_accountstaff_email ON AccountStaff(email);
CREATE INDEX IF NOT EXISTS idx_accountpharmacist_username ON AccountPharmacist(username);
CREATE INDEX IF NOT EXISTS idx_accountpharmacist_email ON AccountPharmacist(email);
CREATE INDEX IF NOT EXISTS idx_appointment_patient_id ON Appointment(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_id ON Appointment(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointment_datetime ON Appointment(appointment_datetime);
CREATE INDEX IF NOT EXISTS idx_medicinerecords_patient_id ON MedicineRecords(patient_id);
CREATE INDEX IF NOT EXISTS idx_diagnosis_doctor_id ON Diagnosis(doctor_id);
CREATE INDEX IF NOT EXISTS idx_serviceorder_medicinerecord_id ON ServiceOrder(medicineRecord_id);
CREATE INDEX IF NOT EXISTS idx_prescription_medicinerecord_id ON Prescription(medicineRecord_id);
CREATE INDEX IF NOT EXISTS idx_invoice_patient_id ON Invoice(patient_id);
CREATE INDEX IF NOT EXISTS idx_payment_invoice_id ON Payment(invoice_id);
CREATE INDEX IF NOT EXISTS idx_medicine_warehouse_id ON Medicine(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_waitlist_patient_id ON Waitlist(patient_id);
CREATE INDEX IF NOT EXISTS idx_waitlist_doctor_id ON Waitlist(doctor_id);

-- ====================================================================
-- Insert default data

-- Insert default units
INSERT INTO Unit (unit_id, unitName) VALUES
(1, 'Viên'),
(2, 'Vỉ'),
(3, 'Hộp'),
(4, 'Chai'),
(5, 'Tuýp'),
(6, 'Gói'),
(7, 'ML'),
(8, 'MG')
ON CONFLICT (unit_id) DO NOTHING;

-- Insert default categories
INSERT INTO Category (category_id, categoryName) VALUES
(1, 'Thuốc giảm đau'),
(2, 'Kháng sinh'),
(3, 'Vitamin & khoáng chất'),
(4, 'Thuốc tim mạch'),
(5, 'Thuốc tiêu hóa'),
(6, 'Thuốc hô hấp'),
(7, 'Thuốc da liễu'),
(8, 'Thuốc thần kinh')
ON CONFLICT (category_id) DO NOTHING;

-- Insert default warehouse
INSERT INTO Warehouse (warehouse_id, name, location) VALUES
(1, 'Kho thuốc chính', 'Tầng 1, Bệnh viện')
ON CONFLICT (warehouse_id) DO NOTHING;

-- Insert default medical services
INSERT INTO ListOfMedicalService (service_id, name, description, price) VALUES
(1, 'Khám tổng quát', 'Khám sức khỏe tổng quát', 200000),
(2, 'Xét nghiệm máu', 'Xét nghiệm máu tổng quát', 150000),
(3, 'Chụp X-quang', 'Chụp X-quang phổi', 300000),
(4, 'Siêu âm', 'Siêu âm bụng tổng quát', 400000),
(5, 'Điện tim', 'Điện tim đồ', 100000),
(6, 'Xét nghiệm nước tiểu', 'Xét nghiệm nước tiểu tổng quát', 80000),
(7, 'Chụp CT', 'Chụp cắt lớp vi tính', 800000),
(8, 'MRI', 'Chụp cộng hưởng từ', 1200000)
ON CONFLICT (service_id) DO NOTHING;

-- Insert default rooms
INSERT INTO Room (room_id, room_name, department, status) VALUES
(1, 'P.101', 'Khoa Nội', 'Available'),
(2, 'P.102', 'Khoa Nội', 'Available'),
(3, 'P.201', 'Khoa Ngoại', 'Available'),
(4, 'P.202', 'Khoa Ngoại', 'Available'),
(5, 'P.301', 'Khoa Nhi', 'Available'),
(6, 'P.401', 'Khoa Phụ sản', 'Available')
ON CONFLICT (room_id) DO NOTHING;

-- Insert default medicines
INSERT INTO Medicine (medicine_id, name, unit_id, category_id, ingredient, usage, preservation, manuDate, expDate, quantity, price, warehouse_id) VALUES
(1, 'Paracetamol 500mg', 1, 1, 'Paracetamol', 'Giảm đau, hạ sốt', 'Nơi khô ráo, thoáng mát', '2024-01-01', '2026-01-01', 1000, 5000, 1),
(2, 'Amoxicillin 250mg', 1, 2, 'Amoxicillin', 'Kháng sinh', 'Nơi khô ráo, dưới 25°C', '2024-01-01', '2025-12-31', 500, 8000, 1),
(3, 'Vitamin C 1000mg', 1, 3, 'Acid ascorbic', 'Bổ sung vitamin C', 'Nơi khô ráo, tránh ánh sáng', '2024-01-01', '2026-06-30', 800, 3000, 1),
(4, 'Ibuprofen 400mg', 1, 1, 'Ibuprofen', 'Chống viêm, giảm đau', 'Nơi khô ráo, dưới 30°C', '2024-01-01', '2025-08-15', 600, 6000, 1),
(5, 'Omeprazole 20mg', 1, 5, 'Omeprazole', 'Điều trị loét dạ dày', 'Nơi khô ráo, tránh ẩm', '2024-01-01', '2025-11-20', 400, 12000, 1)
ON CONFLICT (medicine_id) DO NOTHING;

-- Insert default admin user
INSERT INTO AccountStaff (account_staff_id, username, password, role, email, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'AdminSys', 'admin@healthcare.com', 'Enable')
ON CONFLICT (account_staff_id) DO NOTHING;

INSERT INTO AdminSystem (admin_id, full_name, department, phone, account_staff_id) VALUES
(1, 'System Administrator', 'IT Department', '0123456789', 1)
ON CONFLICT (admin_id) DO NOTHING;

-- Insert sample doctor
INSERT INTO AccountStaff (account_staff_id, username, password, role, email, status) VALUES
(2, 'doctor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Doctor', 'doctor1@healthcare.com', 'Enable')
ON CONFLICT (account_staff_id) DO NOTHING;

INSERT INTO Doctor (doctor_id, full_name, department, phone, eduLevel, account_staff_id) VALUES
(1, 'Bác sĩ Nguyễn Văn A', 'Khoa Nội', '0987654321', 'Thạc sĩ Y khoa', 2)
ON CONFLICT (doctor_id) DO NOTHING;

-- Insert sample patient account
INSERT INTO AccountPatient (account_patient_id, username, password, email, status) VALUES
(1, 'patient1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'patient1@example.com', 'Enable')
ON CONFLICT (account_patient_id) DO NOTHING;

INSERT INTO Patient (patient_id, full_name, dob, gender, phone, address) VALUES
(1, 'Nguyễn Thị B', '1990-01-15', 'Nữ', '0123456789', '123 Đường ABC, Quận 1, TP.HCM')
ON CONFLICT (patient_id) DO NOTHING;

INSERT INTO Patient_AccountPatient (patient_id, account_patient_id) VALUES
(1, 1)
ON CONFLICT (patient_id, account_patient_id) DO NOTHING;

COMMIT; 