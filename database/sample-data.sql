-- Healthcare Management System - Sample Data
-- This file contains sample data for testing the system

-- ====================================================================
-- Sample Account Patients
INSERT INTO AccountPatient (username, password, email, img, status) VALUES
('patient1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'patient1@example.com', 'default.jpg', 'Enable'),
('patient2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'patient2@example.com', 'default.jpg', 'Enable'),
('patient3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'patient3@example.com', 'default.jpg', 'Enable');

-- Sample Patients
INSERT INTO Patient (full_name, dob, gender, phone, address) VALUES
('Nguyen Van A', '1985-03-15', 'Male', '0123456789', '123 Le Loi, Ho Chi Minh City'),
('Tran Thi B', '1990-07-22', 'Female', '0987654321', '456 Nguyen Hue, Ho Chi Minh City'),
('Le Van C', '1978-12-05', 'Male', '0369852147', '789 Dong Khoi, Ho Chi Minh City');

-- Link Patients with Accounts
INSERT INTO Patient_AccountPatient (patient_id, account_patient_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- ====================================================================
-- Sample Account Staff
INSERT INTO AccountStaff (username, password, role, email, img, status) VALUES
('doctor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'Doctor', 'doctor1@hospital.com', 'doctor1.jpg', 'Enable'),
('doctor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'Doctor', 'doctor2@hospital.com', 'doctor2.jpg', 'Enable'),
('nurse1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'Nurse', 'nurse1@hospital.com', 'nurse1.jpg', 'Enable'),
('reception1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'Receptionist', 'reception1@hospital.com', 'reception1.jpg', 'Enable'),
('admin1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'AdminSys', 'admin1@hospital.com', 'admin1.jpg', 'Enable');

-- Sample Doctors
INSERT INTO Doctor (full_name, department, phone, eduLevel, account_staff_id) VALUES
('Dr. Nguyen Van Duc', 'Cardiology', '0123111111', 'MD, PhD Cardiology', 1),
('Dr. Tran Thi Hoa', 'Pediatrics', '0123222222', 'MD Pediatrics', 2);

-- Sample Nurses
INSERT INTO Nurse (full_name, department, phone, eduLevel, account_staff_id) VALUES
('Nurse Le Thi Mai', 'General', '0123333333', 'Bachelor of Nursing', 3);

-- Sample Receptionist
INSERT INTO Receptionist (full_name, phone, account_staff_id) VALUES
('Receptionist Pham Van Nam', '0123444444', 4);

-- Sample Admin
INSERT INTO AdminSystem (full_name, department, phone, account_staff_id) VALUES
('Admin Vo Thi Lan', 'IT Department', '0123555555', 5);

-- ====================================================================
-- Sample Account Pharmacist
INSERT INTO AccountPharmacist (username, password, email, status, img) VALUES
('pharmacist1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi', 'pharmacist1@hospital.com', 'Enable', 'pharmacist1.jpg');

-- Sample Pharmacist
INSERT INTO Pharmacist (full_name, phone, eduLevel, account_pharmacist_id) VALUES
('Pharmacist Hoang Van Minh', '0123666666', 'PharmD', 1);

-- ====================================================================
-- Sample Medical Services
INSERT INTO ListOfMedicalService (name, description, price) VALUES
('General Consultation', 'Basic medical consultation and examination', 200000.00),
('Blood Test', 'Complete blood count and basic chemistry panel', 150000.00),
('X-Ray Chest', 'Chest X-ray examination', 300000.00),
('ECG', 'Electrocardiogram', 250000.00),
('Ultrasound', 'Abdominal ultrasound examination', 400000.00);

-- ====================================================================
-- Sample Rooms
INSERT INTO Room (room_number, type, capacity, status) VALUES
('101', 'Consultation', 1, 'Available'),
('102', 'Consultation', 1, 'Available'),
('201', 'Emergency', 2, 'Available'),
('301', 'Surgery', 4, 'Available'),
('401', 'ICU', 1, 'Available');

-- ====================================================================
-- Sample Medicine Categories and Units
INSERT INTO Unit (unit_name) VALUES
('Tablet'),
('Capsule'),
('ml'),
('Injection'),
('Tube');

INSERT INTO Category (category_name) VALUES
('Antibiotics'),
('Pain Relief'),
('Cardiovascular'),
('Diabetes'),
('Vitamins');

-- Sample Warehouse
INSERT INTO Warehouse (warehouse_name, location) VALUES
('Main Pharmacy', 'Floor 1, Building A'),
('Emergency Stock', 'Floor 2, Building B');

-- Sample Medicines
INSERT INTO Medicine (medicine_name, description, unit_id, category_id, warehouse_id, quantity_available, price_per_unit) VALUES
('Amoxicillin 500mg', 'Antibiotic for bacterial infections', 1, 1, 1, 100, 5000.00),
('Paracetamol 500mg', 'Pain reliever and fever reducer', 1, 2, 1, 200, 2000.00),
('Aspirin 100mg', 'Blood thinner for cardiovascular health', 1, 3, 1, 150, 3000.00),
('Metformin 500mg', 'Diabetes medication', 1, 4, 1, 80, 8000.00),
('Vitamin C 1000mg', 'Immune system support', 1, 5, 1, 300, 1500.00);

-- ====================================================================
-- Sample Appointments
INSERT INTO Appointment (doctor_id, patient_id, appointment_datetime, receptionist_id, shift, status, note) VALUES
(1, 1, '2024-01-15 09:00:00', 1, 'Morning', 'Confirmed', 'Regular checkup'),
(2, 2, '2024-01-15 14:00:00', 1, 'Afternoon', 'Confirmed', 'Pediatric consultation'),
(1, 3, '2024-01-16 10:30:00', 1, 'Morning', 'Pending', 'Follow-up appointment');

-- ====================================================================
-- Sample Medical Records
INSERT INTO MedicineRecords (patient_id) VALUES
(1),
(2),
(3);

-- Sample Diagnosis
INSERT INTO Diagnosis (doctor_id, medicineRecord_id, conclusion, disease, treatment_plan) VALUES
(1, 1, 'Patient shows signs of hypertension', 'High Blood Pressure', 'Medication and lifestyle changes recommended'),
(2, 2, 'Child has common cold symptoms', 'Common Cold', 'Rest and symptomatic treatment'),
(1, 3, 'Patient recovering well from previous treatment', 'Follow-up', 'Continue current medication');

-- ====================================================================
-- Sample Doctor Schedule
INSERT INTO DoctorSchedule (doctor_id, work_date, shift, is_available) VALUES
(1, '2024-01-15', 'Morning', true),
(1, '2024-01-15', 'Afternoon', true),
(1, '2024-01-16', 'Morning', true),
(2, '2024-01-15', 'Morning', true),
(2, '2024-01-15', 'Afternoon', true),
(2, '2024-01-16', 'Afternoon', true);

-- ====================================================================
-- Default passwords for all accounts: "password123"
-- Password hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdqMSwKNjZr03gdOvnE4r8QsvTi

SELECT 'Sample data inserted successfully!' as message; 