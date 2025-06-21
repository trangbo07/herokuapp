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
    
    if (!servicesContainer) return;

    servicesContainer.innerHTML = '';

    allServices.forEach(service => {
        const serviceCard = document.createElement("div");
        serviceCard.className = "col-md-6 col-lg-4 mb-3";
        
        serviceCard.innerHTML = `
            <div class="card service-card h-100">
                <div class="card-body">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" 
                               value="${service.service_id}" 
                               id="service_${service.service_id}"
                               onchange="handleServiceSelection(this)">
                        <label class="form-check-label" for="service_${service.service_id}">
                            <h6 class="card-title">${service.name}</h6>
                            <p class="card-text">${service.description || 'No description'}</p>
                            <strong class="text-primary">${service.price.toLocaleString()} VND</strong>
                        </label>
                    </div>
                </div>
            </div>
        `;

        servicesContainer.appendChild(serviceCard);
    });
}

// Hàm xử lý việc chọn service
function handleServiceSelection(checkbox) {
    const serviceId = parseInt(checkbox.value);
    const service = allServices.find(s => s.service_id === serviceId);
    
    if (checkbox.checked) {
        selectedServices.push(service);
    } else {
        selectedServices = selectedServices.filter(s => s.service_id !== serviceId);
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
        servicesHtml += `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span><i class="fas fa-check-circle text-success me-2"></i>${service.name}</span>
                <span class="text-primary">${service.price.toLocaleString()} VND</span>
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
            showAlert('Please select at least one service', 'danger');
            return;
        }
        
        const serviceIds = selectedServices.map(service => service.service_id);
        
        const response = await fetch('/api/doctor/service-order', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                medicineRecordId: parseInt(medicineRecordId),
                serviceIds: serviceIds
            })
        });

        if (!response.ok) {
            throw new Error('Failed to create service order');
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
        showAlert('Failed to create service order. Please try again.', 'danger');
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