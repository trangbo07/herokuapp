let allAppointments = [];
let selectedAppointment = null;
let selectedSymptoms = [];

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
            <td><i class="fas fa-user-circle me-2"></i>${appointment.patient_name || 'N/A'}</td>
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
function selectPatientForExamination(appointmentId) {
    const appointment = allAppointments.find(a => a.appointment_id == appointmentId);
    
    if (!appointment) {
        showAlert('Appointment not found', 'danger');
        return;
    }

    selectedAppointment = appointment;
    
    // Cập nhật thông tin bệnh nhân
    document.getElementById("patientId").textContent = appointment.patient_id || 'N/A';
    document.getElementById("patientName").textContent = appointment.patient_name || 'N/A';
    document.getElementById("patientAge").textContent = calculateAge(appointment.patient_dob) || 'N/A';
    document.getElementById("patientGender").textContent = appointment.patient_gender || 'N/A';
    document.getElementById("patientPhone").textContent = appointment.patient_phone || 'N/A';
    document.getElementById("appointmentInfo").textContent = `${formatDate(appointment.appointment_datetime)} at ${formatTime(appointment.appointment_datetime)}`;
    
    // Cập nhật hidden input
    document.getElementById("selectedAppointmentId").value = appointment.appointment_id;
    
    // Chuyển sang form khám bệnh
    document.getElementById("patientSelectionCard").style.display = "none";
    document.getElementById("examinationCard").style.display = "block";
    
    // Reset form
    resetExaminationForm();
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
    document.getElementById("patientSelectionCard").style.display = "block";
    selectedAppointment = null;
    selectedSymptoms = [];
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
            appointmentId: formData.get('appointmentId'),
            symptoms: selectedSymptoms,
            symptomsDescription: formData.get('symptomsDescription'),
            bloodPressure: formData.get('bloodPressure'),
            heartRate: formData.get('heartRate'),
            temperature: formData.get('temperature'),
            weight: formData.get('weight'),
            physicalExam: formData.get('physicalExam'),
            preliminaryDiagnosis: formData.get('preliminaryDiagnosis'),
            recommendations: formData.get('recommendations'),
            examinationDate: new Date().toISOString()
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
            
            // Quay lại danh sách bệnh nhân
            setTimeout(() => {
                backToPatientSelection();
                loadAppointments(); // Reload danh sách
            }, 2000);
        } else {
            throw new Error(result.message || 'Failed to save examination');
        }

    } catch (error) {
        console.error("Error saving examination:", error);
        showAlert('Failed to save examination. Please try again.', 'danger');
    }
}

// Hàm cập nhật trạng thái appointment
async function updateAppointmentStatus(appointmentId, status) {
    try {
        const response = await fetch('/api/doctor/appointment/status', {
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
        
        if (!formData.get('physicalExam').trim()) {
            showAlert('Please fill in physical examination findings', 'danger');
            return;
        }
        
        if (!formData.get('preliminaryDiagnosis').trim()) {
            showAlert('Please enter preliminary diagnosis', 'danger');
            return;
        }
        
        // Save examination
        saveExamination(formData);
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
    
    if (!formData.get('physicalExam').trim()) {
        errors.push('Physical examination findings are required');
    }
    
    if (!formData.get('preliminaryDiagnosis').trim()) {
        errors.push('Preliminary diagnosis is required');
    }
    
    return errors;
} 