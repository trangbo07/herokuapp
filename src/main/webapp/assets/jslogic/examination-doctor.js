let allAppointments = [];
let selectedAppointment = null;
let selectedSymptoms = [];
let allServices = [];
let selectedServices = [];

// Hàm tải danh sách appointment
async function loadAppointments() {
    const loadingSpinner = document.getElementById("loadingSpinner");
    const tableContainer = document.getElementById("tableContainer");
    const errorMessage = document.getElementById("errorMessage");
    
    try {
        loadingSpinner.style.display = "flex";
        tableContainer.style.display = "none";
        errorMessage.classList.add("d-none");

        const response = await fetch('/api/doctor/appointment', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({action: 'Upcoming'})
        });

        if (!response.ok) {
            throw new Error('Failed to fetch appointments');
        }

        const appointments = await response.json();
        
        loadingSpinner.style.display = "none";
        
        if (!appointments || appointments.length === 0) {
            errorMessage.classList.remove("d-none");
            errorMessage.innerHTML = '<i class="fas fa-info-circle me-2"></i>No upcoming appointments found.';
            return;
        }

        allAppointments = appointments;
        tableContainer.style.display = "block";
        renderAppointmentsTable();

    } catch (error) {
        loadingSpinner.style.display = "none";
        errorMessage.classList.remove("d-none");
        errorMessage.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Failed to load appointments.';
        console.error("Error loading appointments:", error);
    }
}

// Hàm render bảng appointment
function renderAppointmentsTable() {
    const tableBody = document.getElementById("appointmentTableBody");
    
    if (!tableBody) return;

    tableBody.innerHTML = '';

    allAppointments.forEach((appointment, index) => {
        const statusClass = getStatusClass(appointment.status);
        const row = document.createElement("tr");
        row.className = "appointment-row";
        
        row.innerHTML = `
            <td>${index + 1}</td>
            <td><i class="fas fa-user me-2"></i>${appointment.patient_id || 'N/A'}</td>
            <td><i class="fas fa-calendar me-2"></i>${formatDate(appointment.appointment_datetime)}</td>
            <td><i class="fas fa-clock me-2"></i>${formatTime(appointment.appointment_datetime)}</td>
            <td><span class="status-badge ${statusClass}">${appointment.status || 'N/A'}</span></td>
            <td><i class="fas fa-sticky-note me-2"></i>${appointment.note || 'No note'}</td>
            <td>
                <button class="btn btn-sm btn-primary select-patient-btn" data-appointment-id="${appointment.appointment_id}">
                    <i class="fas fa-stethoscope me-1"></i>Select for Examination
                </button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

// Hàm lấy class CSS cho status
function getStatusClass(status) {
    switch(status?.toLowerCase()) {
        case 'upcoming':
            return 'status-upcoming';
        case 'completed':
            return 'status-completed';
        case 'cancelled':
            return 'status-cancelled';
        default:
            return 'status-upcoming';
    }
}

// Hàm format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB');
}

// Hàm format time
function formatTime(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
}

// Hàm chọn bệnh nhân để khám
async function selectPatientForExamination(appointmentId) {
    try {
        // Gọi API lấy chi tiết bệnh nhân
        const response = await fetch(`/api/doctor/appointment?action=detail&id=${appointmentId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) throw new Error('Không lấy được thông tin chi tiết bệnh nhân');
        const appointment = await response.json();
        selectedAppointment = appointment;
        // Cập nhật thông tin bệnh nhân dựa vào key mới
        document.getElementById("patientId").textContent = appointment.patient_id || 'N/A';
        document.getElementById("patientName").textContent = appointment.full_name || 'N/A';
        document.getElementById("patientAge").textContent = calculateAge(appointment.dob) || 'N/A';
        document.getElementById("patientGender").textContent = appointment.gender || 'N/A';
        document.getElementById("patientPhone").textContent = appointment.phone || 'N/A';
        document.getElementById("appointmentInfo").textContent = `${formatDate(appointment.appointment_datetime)} at ${formatTime(appointment.appointment_datetime)}`;
        document.getElementById("patientNote").textContent = appointment.note || 'N/A';
        // Cập nhật hidden input
        document.getElementById("selectedAppointmentId").value = appointment.appointment_id;
        // Chuyển sang form khám bệnh
        document.getElementById("patientSelectionCard").style.display = "none";
        document.getElementById("examinationCard").style.display = "block";
        // Reset form
        resetExaminationForm();
    } catch (error) {
        showAlert('Không lấy được thông tin chi tiết bệnh nhân', 'danger');
        // Hiển thị N/A cho tất cả các trường
        document.getElementById("patientId").textContent = 'N/A';
        document.getElementById("patientName").textContent = 'N/A';
        document.getElementById("patientAge").textContent = 'N/A';
        document.getElementById("patientGender").textContent = 'N/A';
        document.getElementById("patientPhone").textContent = 'N/A';
        document.getElementById("appointmentInfo").textContent = 'N/A';
        document.getElementById("patientNote").textContent = 'N/A';
    }
}

// Hàm tính tuổi
function calculateAge(dob) {
    if (!dob) return null;
    const birthDate = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    
    return age;
}

// Hàm reset form khám bệnh
function resetExaminationForm() {
    document.getElementById("examinationForm").reset();
    selectedSymptoms = [];
    
    // Reset symptom tags
    document.querySelectorAll('.symptom-tag').forEach(tag => {
        tag.classList.remove('selected');
    });
}

// Hàm quay lại danh sách bệnh nhân
function backToPatientSelection() {
    document.getElementById("examinationCard").style.display = "none";
    document.getElementById("serviceOrderCard").style.display = "none";
    document.getElementById("patientSelectionCard").style.display = "block";
    selectedAppointment = null;
    selectedSymptoms = [];
    selectedServices = [];
}

// Hàm xử lý symptom tags
function handleSymptomTagClick(tag) {
    const symptom = tag.getAttribute('data-symptom');
    
    if (tag.classList.contains('selected')) {
        tag.classList.remove('selected');
        selectedSymptoms = selectedSymptoms.filter(s => s !== symptom);
    } else {
        tag.classList.add('selected');
        selectedSymptoms.push(symptom);
    }
}

// Hàm lưu thông tin khám bệnh
async function saveExamination(formData) {
    try {
        const examinationData = {
            appointmentId: parseInt(formData.get('appointmentId')),
            symptomsDescription: formData.get('symptomsDescription'),
            preliminaryDiagnosis: formData.get('preliminaryDiagnosis')
        };

        const response = await fetch('/api/doctor/examination', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(examinationData)
        });

        if (!response.ok) {
            throw new Error('Failed to save examination');
        }

        const result = await response.json();
        
        if (result.success) {
            showAlert('Examination saved successfully!', 'success');
            
            // Cập nhật trạng thái appointment thành completed
            await updateAppointmentStatus(examinationData.appointmentId, 'Completed');
            
            // Lưu medicineRecordId để sử dụng cho service order
            if (result.medicineRecordId) {
                document.getElementById("medicineRecordId").value = result.medicineRecordId;
                
                // Chuyển sang form service order
                setTimeout(() => {
                    showServiceOrderForm();
                }, 1500);
            } else {
                // Quay lại danh sách bệnh nhân nếu không có medicineRecordId
                setTimeout(() => {
                    backToPatientSelection();
                    loadAppointments();
                }, 2000);
            }
        } else {
            throw new Error(result.message || 'Failed to save examination');
        }

    } catch (error) {
        console.error("Error saving examination:", error);
        showAlert('Failed to save examination. Please try again.', 'danger');
    }
}

// Hàm hiển thị form service order
async function showServiceOrderForm() {
    try {
        // Ẩn examination form
        document.getElementById("examinationCard").style.display = "none";
        
        // Hiển thị service order form
        document.getElementById("serviceOrderCard").style.display = "block";
        
        // Load danh sách services
        await loadServices();
        
    } catch (error) {
        console.error("Error showing service order form:", error);
        showAlert('Failed to load services. Please try again.', 'danger');
    }
}

// Hàm load danh sách services
async function loadServices() {
    try {
        const response = await fetch('/api/doctor/service-order?action=getServices', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load services');
        }

        const result = await response.json();
        
        if (result.success) {
            allServices = result.data;
            renderServices();
        } else {
            throw new Error(result.message || 'Failed to load services');
        }

    } catch (error) {
        console.error("Error loading services:", error);
        showAlert('Failed to load services. Please try again.', 'danger');
    }
}

// Hàm render danh sách services
function renderServices() {
    const servicesContainer = document.getElementById("servicesContainer");
    
    if (!servicesContainer) {
        console.error('Services container not found');
        return;
    }

    servicesContainer.innerHTML = '';


    if (!allServices || allServices.length === 0) {
        servicesContainer.innerHTML = `
            <div class="col-12">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i>
                    No services available at the moment. Please check the database or contact administrator.
                </div>
            </div>
        `;
        return;
    }

    allServices.forEach(service => {
        const serviceCard = document.createElement("div");
        serviceCard.className = "col-md-6 col-lg-4 mb-3";
        
        const serviceName = service.name || 'Unknown Service';
        const serviceDescription = service.description || 'No description available';
        const servicePrice = service.price || 0;
        const serviceId = service.service_id || 0;
        
        serviceCard.innerHTML = `
            <div class="card service-card h-100">
                <div class="card-body">
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" 
                               value="${serviceId}" 
                               id="service_${serviceId}"
                               onchange="handleServiceSelection(this)">
                        <label class="form-check-label" for="service_${serviceId}">
                            <h6 class="card-title">${serviceName}</h6>
                        </label>
                    </div>
                     <p class="card-text small text-muted">${serviceDescription}</p>
                     <strong class="text-primary">${servicePrice.toLocaleString()} VND</strong>
                    <div class="doctor-selection mt-3" id="doctor_selection_${serviceId}" style="display: none;">
                        <label class="form-label small">Assign Doctor:</label>
                        <select class="form-select form-select-sm" id="doctor_${serviceId}" onchange="handleDoctorSelection(${serviceId})">
                            <option value="">Select a doctor...</option>
                            <!-- Doctors will be loaded dynamically -->
                        </select>
                    </div>
                </div>
            </div>
        `;

        servicesContainer.appendChild(serviceCard);
    });
    
    // Load danh sách bác sĩ sau khi render services
    loadDoctors();
}

// Hàm xử lý việc chọn service
function handleServiceSelection(checkbox) {
    const serviceId = parseInt(checkbox.value);
    const service = allServices.find(s => s.service_id === serviceId);
    const doctorSelection = document.getElementById(`doctor_selection_${serviceId}`);
    
    if (checkbox.checked) {
        selectedServices.push(service);
        if(doctorSelection) doctorSelection.style.display = "block";
    } else {
        selectedServices = selectedServices.filter(s => s.service_id !== serviceId);
        if(doctorSelection) {
            doctorSelection.style.display = "none";
            // Reset doctor selection
            const doctorSelect = document.getElementById(`doctor_${serviceId}`);
            if(doctorSelect) doctorSelect.value = "";
            // Remove assigned doctor from service object
            const serviceInArray = selectedServices.find(s => s.service_id === serviceId);
            if(serviceInArray) delete serviceInArray.assigned_doctor_id;
        }
    }
    
    updateSelectedServicesDisplay();
}

// Hàm xử lý việc chọn bác sĩ
function handleDoctorSelection(serviceId) {
    const doctorSelect = document.getElementById(`doctor_${serviceId}`);
    const selectedDoctorId = doctorSelect.value;
    
    // Cập nhật service với doctor_id
    const serviceIndex = selectedServices.findIndex(s => s.service_id === serviceId);
    if (serviceIndex !== -1) {
        selectedServices[serviceIndex].assigned_doctor_id = selectedDoctorId ? parseInt(selectedDoctorId) : null;
    }
    
    updateSelectedServicesDisplay();
}

// Hàm cập nhật hiển thị services đã chọn
function updateSelectedServicesDisplay() {
    const selectedServicesSection = document.getElementById("selectedServicesSection");
    const selectedServicesList = document.getElementById("selectedServicesList");
    const totalAmountSpan = document.getElementById("totalAmount");
    
    if (selectedServices.length === 0) {
        selectedServicesSection.style.display = "none";
        return;
    }
    
    selectedServicesSection.style.display = "block";
    
    let totalAmount = 0;
    let servicesHtml = '';
    
    selectedServices.forEach(service => {
        totalAmount += service.price;
        
        // Lấy tên bác sĩ được chọn
        let doctorName = '<span class="text-danger">Not assigned</span>';
        if (service.assigned_doctor_id) {
            const doctorSelect = document.getElementById(`doctor_${service.service_id}`);
            if(doctorSelect) {
                const selectedOption = doctorSelect.options[doctorSelect.selectedIndex];
                if(selectedOption && selectedOption.value) {
                     doctorName = selectedOption.textContent;
                }
            }
        }
        
        servicesHtml += `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                    <span><i class="fas fa-check-circle text-success me-2"></i>${service.name}</span>
                    <br>
                    <small class="text-muted">Assigned to: ${doctorName}</small>
                </div>
                <span class="text-primary fw-bold">${service.price.toLocaleString()} VND</span>
            </div>
        `;
    });
    
    selectedServicesList.innerHTML = servicesHtml;
    totalAmountSpan.textContent = totalAmount.toLocaleString();
}

// Hàm tạo service order
async function createServiceOrder() {
    try {
        const medicineRecordId = document.getElementById("medicineRecordId").value;
        
        if (!medicineRecordId) {
            showAlert('Missing medicine record ID', 'danger');
            return;
        }
        
        if (selectedServices.length === 0) {
            // This case should be handled by the skip button, but as a safeguard:
             showAlert('No services selected. Click "Skip Services" to complete the examination.', 'info');
            return;
        }
        
        // Validation: Check if all selected services have a doctor assigned
        const unassignedServices = selectedServices.filter(s => !s.assigned_doctor_id);
        if (unassignedServices.length > 0) {
            showAlert(`Please assign a doctor for all selected services. Missing for: ${unassignedServices.map(s => s.name).join(', ')}`, 'danger');
            return;
        }
        
        const servicesData = selectedServices.map(service => ({
            serviceId: service.service_id,
            doctorId: service.assigned_doctor_id
        }));
        
        const response = await fetch('/api/doctor/service-order', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                medicineRecordId: parseInt(medicineRecordId),
                services: servicesData
            })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: 'Failed to create service order' }));
            throw new Error(errorData.message);
        }

        const result = await response.json();
        
        if (result.success) {
            showAlert('Service order created successfully!', 'success');
            
            // Quay lại danh sách bệnh nhân
            setTimeout(() => {
                backToPatientSelection();
                loadAppointments();
            }, 2000);
        } else {
            throw new Error(result.message || 'Failed to create service order');
        }

    } catch (error) {
        console.error("Error creating service order:", error);
        showAlert(error.message || 'Failed to create service order. Please try again.', 'danger');
    }
}

// Hàm skip services
function skipServices() {
    showAlert('Services skipped. Examination completed.', 'info');
    
    setTimeout(() => {
        backToPatientSelection();
        loadAppointments();
    }, 1500);
}

// Hàm cập nhật trạng thái appointment
async function updateAppointmentStatus(appointmentId, status) {
    try {
        const response = await fetch('/api/doctor/appointment', {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                appointmentId: appointmentId,
                status: status
            })
        });

        if (!response.ok) {
            console.error('Failed to update appointment status');
        }
    } catch (error) {
        console.error("Error updating appointment status:", error);
    }
}

// Hàm hiển thị alert
function showAlert(message, type) {
    const alertContainer = document.getElementById("alertContainer");
    
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    alertContainer.innerHTML = alertHtml;
    
    // Tự động ẩn alert sau 5 giây
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Load appointments khi trang được load
    loadAppointments();
    
    // Event listener cho việc chọn bệnh nhân
    document.getElementById("appointmentTableBody").addEventListener('click', function(e) {
        const selectBtn = e.target.closest('.select-patient-btn');
        if (selectBtn) {
            const appointmentId = selectBtn.getAttribute('data-appointment-id');
            selectPatientForExamination(appointmentId);
        }
    });
    
    // Event listener cho symptom tags
    document.getElementById("symptomsContainer").addEventListener('click', function(e) {
        if (e.target.classList.contains('symptom-tag')) {
            handleSymptomTagClick(e.target);
        }
    });
    
    // Event listener cho nút back
    document.getElementById("backToSelection").addEventListener('click', function() {
        backToPatientSelection();
    });
    
    // Event listener cho form submission
    document.getElementById("examinationForm").addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        
        // Validation
        if (!formData.get('symptomsDescription').trim()) {
            showAlert('Please describe the symptoms', 'danger');
            return;
        }
        
        if (!formData.get('preliminaryDiagnosis').trim()) {
            showAlert('Please enter preliminary diagnosis', 'danger');
            return;
        }
        
        // Save examination
        saveExamination(formData);
    });
    
    // Event listener cho service order form
    document.getElementById("serviceOrderForm").addEventListener('submit', function(e) {
        e.preventDefault();
        createServiceOrder();
    });
    
    // Event listener cho nút skip services
    document.getElementById("skipServices").addEventListener('click', function() {
        skipServices();
    });
    
    // Event listener cho việc refresh appointments
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'r') {
            e.preventDefault();
            loadAppointments();
        }
    });
});

// Hàm utility để format datetime
function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString('en-GB');
}

// Hàm utility để validate form
function validateExaminationForm(formData) {
    const errors = [];
    
    if (!formData.get('symptomsDescription').trim()) {
        errors.push('Symptoms description is required');
    }
    
    if (!formData.get('preliminaryDiagnosis').trim()) {
        errors.push('Preliminary diagnosis is required');
    }
    
    return errors;
}

// Hàm lấy chi tiết service order (ví dụ sử dụng API mới)
async function getServiceOrderDetails(serviceOrderId) {
    try {
        const response = await fetch(`/api/doctor/service-order?action=getServiceOrder&serviceOrderId=${serviceOrderId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to get service order details');
        }

        const result = await response.json();
        
        if (result.success) {
            const data = result.data;
            console.log('Service Order Details:', data);
            
            // Hiển thị thông tin chi tiết
            const serviceOrder = data.serviceOrder;
            const items = data.items;
            const totalAmount = data.totalAmount;
            
            console.log(`Service Order #${serviceOrder.service_order_id}`);
            console.log(`Doctor: ${serviceOrder.doctor_name}`);
            console.log(`Patient: ${serviceOrder.patient_name}`);
            console.log(`Date: ${serviceOrder.order_date}`);
            console.log(`Total Amount: ${totalAmount.toLocaleString()} VND`);
            
            console.log('Services:');
            items.forEach(item => {
                console.log(`- ${item.service_name}: ${item.service_price.toLocaleString()} VND`);
            });
            
            return data;
        } else {
            throw new Error(result.message || 'Failed to get service order details');
        }

    } catch (error) {
        console.error("Error getting service order details:", error);
        showAlert('Failed to get service order details. Please try again.', 'danger');
        return null;
    }
}

// Hàm lấy lịch sử service orders theo medicine record
async function getServiceOrderHistory(medicineRecordId) {
    try {
        const response = await fetch(`/api/doctor/service-order?action=getServiceOrdersByMedicineRecord&medicineRecordId=${medicineRecordId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to get service order history');
        }

        const result = await response.json();
        
        if (result.success) {
            console.log('Service Order History:', result.data);
            return result.data;
        } else {
            throw new Error(result.message || 'Failed to get service order history');
        }

    } catch (error) {
        console.error("Error getting service order history:", error);
        showAlert('Failed to get service order history. Please try again.', 'danger');
        return null;
    }
}

// Hàm lấy lịch sử service orders của bác sĩ
async function getDoctorServiceOrderHistory() {
    try {
        const response = await fetch('/api/doctor/service-order?action=getServiceOrdersByDoctor', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to get doctor service order history');
        }

        const result = await response.json();
        
        if (result.success) {
            console.log('Doctor Service Order History:', result.data);
            return result.data;
        } else {
            throw new Error(result.message || 'Failed to get doctor service order history');
        }

    } catch (error) {
        console.error("Error getting doctor service order history:", error);
        showAlert('Failed to get doctor service order history. Please try again.', 'danger');
        return null;
    }
}

// Hàm load danh sách bác sĩ
async function loadDoctors() {
    try {
        const response = await fetch('/api/doctor/service-order?action=getDoctors', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load doctors');
        }

        const result = await response.json();
        
        if (result.success) {
            const doctors = result.data;
            const doctorSelects = document.querySelectorAll('.doctor-selection select');
            
            doctorSelects.forEach(select => {
                // Clear existing options except the first one
                while (select.options.length > 1) {
                    select.remove(1);
                }
                // Add new options
                doctors.forEach(doctor => {
                    const option = document.createElement('option');
                    option.value = doctor.doctor_id;
                    option.textContent = `${doctor.full_name} - ${doctor.department || 'General'}`;
                    select.appendChild(option);
                });
            });
        } else {
            throw new Error(result.message || 'Failed to load doctors');
        }

    } catch (error) {
        console.error("Error loading doctors:", error);
        showAlert('Failed to load doctors. Please try again.', 'danger');
    }
} 