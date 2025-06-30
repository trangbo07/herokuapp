-- Healthcare Management System - SQL Server Schema
-- Original schema as provided

CREATE DATABASE HealthCareSystem;
GO
USE HealthCareSystem;
GO

-- Account benh nhan 
CREATE TABLE AccountPatient (
    account_patient_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
	img NVARCHAR(200), --Update
    status NVARCHAR(30) CHECK (status IN ('Enable', 'Disable'))
);

CREATE TABLE Patient (
    patient_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    dob DATE,
    gender NVARCHAR(10),
    phone NVARCHAR(20),
    address NVARCHAR(255)
);

CREATE TABLE Patient_AccountPatient (
    patient_id INT,
    account_patient_id INT,
    PRIMARY KEY (patient_id, account_patient_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (account_patient_id) REFERENCES AccountPatient(account_patient_id)
);

-- Account cua nhan vien
CREATE TABLE AccountStaff (
    account_staff_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(100) NOT NULL,
    role NVARCHAR(30) CHECK (role IN ('Doctor', 'Nurse', 'Receptionist', 'AdminSys', 'AdminBusiness')),
    email NVARCHAR(100) UNIQUE NOT NULL,
	img NVARCHAR(200),
    status NVARCHAR(30) CHECK (status IN ('Enable', 'Disable'))
);

CREATE TABLE Doctor (
    doctor_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    department NVARCHAR(100),
    phone NVARCHAR(20),
    eduLevel NVARCHAR(50),
    account_staff_id INT UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE Nurse (
    nurse_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    department NVARCHAR(100),
    phone NVARCHAR(20),
    eduLevel NVARCHAR(50),
    account_staff_id INT UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE Receptionist (
    receptionist_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    phone NVARCHAR(20),
    account_staff_id INT UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE AdminBusiness (
    admin_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    department NVARCHAR(100),
    phone NVARCHAR(20),
    account_staff_id INT UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE AdminSystem (
    admin_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    department NVARCHAR(100),
    phone NVARCHAR(20),
    account_staff_id INT UNIQUE,
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

-- Account cua duoc si
CREATE TABLE AccountPharmacist (
    account_pharmacist_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    status NVARCHAR(30) CHECK (status IN ('Enable', 'Disable')),
	img NVARCHAR(200)
);

CREATE TABLE Pharmacist (
    pharmacist_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100),
    phone NVARCHAR(20),
    eduLevel NVARCHAR(50),
    account_pharmacist_id INT UNIQUE,
    FOREIGN KEY (account_pharmacist_id) REFERENCES AccountPharmacist(account_pharmacist_id)
);

--=========================================================================
-- Quan ly ho so benh an
CREATE TABLE MedicineRecords (
    medicineRecord_id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id INT,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE Appointment (
    appointment_id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT,
    patient_id INT,
    appointment_datetime DATETIME,
	receptionist_id INT,
    shift NVARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL DEFAULT 'Morning',
    status NVARCHAR(50) CHECK (status IN ('Pending', 'Confirmed', 'Completed', 'Cancelled')),
    note NVARCHAR(MAX),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
	FOREIGN KEY (receptionist_id) REFERENCES Receptionist(receptionist_id)
);

CREATE TABLE Diagnosis (
    diagnosis_id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT,
    medicineRecord_id INT,
    conclusion NVARCHAR(MAX),
    disease NVARCHAR(MAX),
    treatment_plan NVARCHAR(MAX),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE ExamResult (
    exam_result_id INT IDENTITY(1,1) PRIMARY KEY,
    medicineRecord_id INT,
    symptoms NVARCHAR(MAX),
    preliminary_diagnosis NVARCHAR(MAX),
    doctor_id INT,
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE ListOfMedicalService (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100),
    description NVARCHAR(MAX),
    price DECIMAL(10,2)
);

-- Đơn thuốc
CREATE TABLE Prescription (
    prescription_id INT IDENTITY(1,1) PRIMARY KEY,
    medicineRecord_id INT,
    doctor_id INT,
    prescription_date DATE DEFAULT GETDATE(),
    status NVARCHAR(30) CHECK (status IN ('Pending', 'Dispensed', 'Cancelled')),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE Invoice (
    invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id INT,
	medicineRecord_id INT,
    issue_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(12,2),
    status NVARCHAR(30) CHECK (status IN ('Pending', 'Paid', 'Cancelled')),
	FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE PrescriptionInvoice (
    prescription_invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    invoice_id INT UNIQUE,
    pharmacist_id INT,
	 prescription_id INT,
    FOREIGN KEY (prescription_id) REFERENCES Prescription(prescription_id),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (pharmacist_id) REFERENCES Pharmacist(pharmacist_id)
);

CREATE TABLE ServiceOrder (
    service_order_id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT,
    order_date DATE,
    medicineRecord_id INT,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (medicineRecord_id) REFERENCES MedicineRecords(medicineRecord_id)
);

CREATE TABLE ServiceOrderItem (
    service_order_item_id INT IDENTITY(1,1) PRIMARY KEY,
	service_order_id INT,
    service_id INT,
    doctor_id INT,
    FOREIGN KEY (service_id) REFERENCES ListOfMedicalService(service_id),
    FOREIGN KEY (service_order_id) REFERENCES ServiceOrder(service_order_id)
);

CREATE TABLE ServiceInvoice (
    service_invoice_id INT PRIMARY KEY IDENTITY(1,1),
    invoice_id INT,
    service_order_item_id INT NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10,2),
    total_price AS (quantity * unit_price) PERSISTED,

    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (service_order_item_id) REFERENCES ServiceOrderItem(service_order_item_id),
);

CREATE TABLE ResultsOfParaclinicalServices (
    result_id INT IDENTITY(1,1) PRIMARY KEY,
    service_order_item_id INT UNIQUE,
    result_description NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (service_order_item_id) REFERENCES ServiceOrderItem(service_order_item_id)
);

--=====================================================================================
-- Kho và thuốc
CREATE TABLE Warehouse (
    warehouse_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100),
    location NVARCHAR(255)

);

CREATE TABLE Unit (
    unit_id INT PRIMARY KEY NOT NULL,
    unitName NVARCHAR(255) UNIQUE
);

CREATE TABLE Category (
    category_id INT PRIMARY KEY NOT NULL,
    categoryName NVARCHAR(255) UNIQUE
);

CREATE TABLE Medicine (
    medicine_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    unit_id INT,
	category_id INT,
	ingredient NVARCHAR(MAX),
	usage NVARCHAR(MAX),
    preservation NVARCHAR(MAX),
	manuDate DATE,
	expDate DATE,
	quantity INT,
    price DECIMAL(10,2),
    warehouse_id INT,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouse(warehouse_id),
	FOREIGN KEY (unit_id) REFERENCES Unit(unit_id),
	FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE Medicines (
    prescription_invoice_id INT,
    medicine_id INT,
    quantity INT,
    dosage NVARCHAR(100),
    PRIMARY KEY (prescription_invoice_id, medicine_id),
    FOREIGN KEY (prescription_invoice_id) REFERENCES PrescriptionInvoice(prescription_invoice_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);


-- Thanh toán
CREATE TABLE Payment (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    amount DECIMAL(12,2),
    payment_type NVARCHAR(30) CHECK (payment_type IN ('Service', 'Prescription')),
    invoice_id INT,
    payment_date DATETIME DEFAULT GETDATE(),
    status NVARCHAR(30) DEFAULT 'Pending',
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id)
);

-- Phản hồi từ bệnh nhân
CREATE TABLE Feedback (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id INT,
    content NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

-- Thông báo nội bộ
CREATE TABLE InternalAnnouncement (
    announcement_id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(100),
    content NVARCHAR(MAX),
    created_by_admin_business INT,
    created_by_admin_system INT,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (created_by_admin_business) REFERENCES AdminBusiness(admin_id),
    FOREIGN KEY (created_by_admin_system) REFERENCES AdminSystem(admin_id)
);

-- Lịch sử hành động hệ thống
CREATE TABLE SystemLog_Staff (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    account_staff_id INT,
    action NVARCHAR(255),
    action_type NVARCHAR(50),
    log_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (account_staff_id) REFERENCES AccountStaff(account_staff_id)
);

CREATE TABLE SystemLog_Patient (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    account_patient_id INT,
    action NVARCHAR(255),
    action_type NVARCHAR(50),
    log_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (account_patient_id) REFERENCES AccountPatient(account_patient_id)
);

CREATE TABLE SystemLog_Pharmacist (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    account_pharmacist_id INT,
    action NVARCHAR(255),
    action_type NVARCHAR(50),
    log_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (account_pharmacist_id) REFERENCES AccountPharmacist(account_pharmacist_id)
);

CREATE TABLE Room (
    room_id INT IDENTITY(1,1) PRIMARY KEY,
    room_name NVARCHAR(100) NOT NULL,
    department NVARCHAR(100), -- Khoa phụ trách
    status NVARCHAR(30) CHECK (status IN ('Available', 'Occupied', 'Maintenance')) DEFAULT 'Available'
);

CREATE TABLE Waitlist (
    waitlist_id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    room_id INT,
    registered_at DATETIME DEFAULT GETDATE(),
    estimated_time DATETIME,
	visittype NVARCHAR(30) CHECK (visittype IN ('Initial', 'Result')),
    status NVARCHAR(30) CHECK (status IN ('Waiting', 'InProgress', 'Skipped', 'Completed')) DEFAULT 'Waiting',
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE DoctorRoomAssignment (
    assignment_id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT NOT NULL,
    room_id INT NOT NULL,
    assignment_date DATE NOT NULL,
    shift NVARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL,

    UNIQUE (doctor_id, assignment_date, shift),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE DoctorSchedule (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT NOT NULL,
    working_date DATE NOT NULL,
    shift NVARCHAR(20) CHECK (shift IN ('Morning', 'Afternoon', 'Evening')) NOT NULL,
    room_id INT,
    is_available BIT DEFAULT 1, -- 1: Có thể đặt lịch, 0: Nghỉ hoặc không nhận
    note NVARCHAR(255),

    UNIQUE (doctor_id, working_date, shift),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE Distributor (
    DistributorID INT PRIMARY KEY NOT NULL,
    DistributorName NVARCHAR(255),
    [Address] NVARCHAR(MAX),
    DistributorEmail NVARCHAR(255),
    DistributorPhone NVARCHAR(15)
);

CREATE TABLE ImportInfo (
    ImportID INT PRIMARY KEY NOT NULL,
    DistributorID INT,
    medicine_id INT,
    ImportDate DATE,
    ImportAmount INT,
    FOREIGN KEY (DistributorID) REFERENCES Distributor(DistributorID),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
); 