let serviceOrderWaitlist = [];

// Hàm khởi tạo trang
async function initializeServiceOrderPage() {
    try {
        // Load danh sách waitlist có status = InProgress và visittype = Initial
        await loadServiceOrderWaitlist();
        
    } catch (error) {
        console.error("Error initializing service order page:", error);
        showAlert('Failed to initialize page. Please try again.', 'danger');
    }
}

// Hàm load danh sách waitlist cho service order
async function loadServiceOrderWaitlist() {
    const loadingSpinner = document.getElementById("loadingSpinner");
    const tableContainer = document.getElementById("tableContainer");
    const errorMessage = document.getElementById("errorMessage");

    try {
        loadingSpinner.style.display = "flex";
        tableContainer.style.display = "none";
        errorMessage.classList.add("d-none");

        const response = await fetch('/api/doctor/service-order?action=getServiceOrderWaitlist', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Failed to fetch service order waitlist');

        const responseData = await response.json();
        console.log('Service Order Waitlist:', responseData);
        loadingSpinner.style.display = "none";

        if (!responseData.success) {
            throw new Error(responseData.message || 'Failed to load service order waitlist');
        }

        const waitlists = responseData.data;
        if (!waitlists || waitlists.length === 0) {
            errorMessage.classList.remove("d-none");
            errorMessage.innerHTML = '<i class="fas fa-info-circle me-2"></i>No patients ready for service orders.';
            return;
        }

        serviceOrderWaitlist = waitlists;
        tableContainer.style.display = "block";
        renderServiceOrderTable();

    } catch (error) {
        console.error("Error loading service order waitlist:", error);
        loadingSpinner.style.display = "none";
        errorMessage.classList.remove("d-none");
        errorMessage.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Failed to load service order waitlist.';
    }
}

// Hàm render bảng service order waitlist
function renderServiceOrderTable() {
    const tableBody = document.getElementById("serviceOrderTableBody");
    if (!tableBody) return;

    tableBody.innerHTML = '';

    serviceOrderWaitlist.forEach((waitlist, index) => {
        const statusClass = getStatusClass(waitlist.status);

        const row = document.createElement("tr");
        row.className = "service-order-row";

        row.innerHTML = `
            <td>${index + 1}</td>
            <td>
                <div class="patient-info">
                    <div class="patient-name">${waitlist.full_name || 'N/A'}</div>
                    <div class="patient-details">
                        ID: ${waitlist.patient_id || 'N/A'} | 
                        Age: ${waitlist.dob ? calculateAge(waitlist.dob) : 'N/A'} | 
                        Gender: ${waitlist.gender || 'N/A'}
                    </div>
                </div>
            </td>
            <td><i class="fas fa-door-open me-2"></i>Room ${waitlist.room_id || 'N/A'}</td>
            <td><i class="fas fa-calendar-plus me-2"></i>${formatDate(waitlist.registered_at)}</td>
            <td><i class="fas fa-hourglass-start me-2"></i>${formatTime(waitlist.estimated_time)}</td>
            <td><i class="fas fa-calendar-check me-2"></i>${formatDateTime(waitlist.appointment_datetime)}</td>
            <td><span class="status-badge ${statusClass}">${waitlist.status || 'N/A'}</span></td>
            <td><i class="fas fa-vial me-2"></i>${waitlist.visittype || 'N/A'}</td>
            <td><i class="fas fa-sticky-note me-2"></i>${waitlist.note || 'No note'}</td>
            <td><i class="fas fa-clock me-2"></i>${waitlist.shift || 'N/A'}</td>
            <td>
                <button class="btn btn-success assign-service-btn" data-waitlist-id="${waitlist.waitlist_id}" data-patient-id="${waitlist.patient_id}">
                    <i class="fas fa-stethoscope me-1"></i>Chỉ định
                </button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

// Hàm lấy class CSS cho status
function getStatusClass(status) {
    switch (status?.toLowerCase()) {
        case 'waiting':
            return 'status-waiting';
        case 'inprogress':
            return 'status-inprogress';
        case 'skipped':
            return 'status-secondary';
        case 'completed':
            return 'status-completed';
        default:
            return 'status-secondary';
    }
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleDateString();
}

function formatTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleString();
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

// Hàm xử lý nút chỉ định dịch vụ
async function assignServices(waitlistId, patientId) {
    try {
        // Lấy medicine record ID từ waitlist
        const response = await fetch(`/api/doctor/waitlist?action=detail&id=${waitlistId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Failed to get waitlist details');

        const waitlist = await response.json();
        
        // Chuyển hướng sang trang chọn dịch vụ với thông tin cần thiết
        window.location.href = `service-order-details.html?waitlistId=${waitlistId}&patientId=${patientId}`;
        
    } catch (error) {
        console.error("Error getting waitlist details:", error);
        showAlert('Failed to get waitlist details. Please try again.', 'danger');
    }
}

// Hàm hiển thị alert
function showAlert(message, type) {
    const alertContainer = document.getElementById("alertContainer");
    
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
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
    // Khởi tạo trang khi load
    initializeServiceOrderPage();
    
    // Event listener cho việc nhấn nút chỉ định
    document.getElementById("serviceOrderTableBody").addEventListener('click', function(e) {
        const assignBtn = e.target.closest('.assign-service-btn');
        if (assignBtn) {
            const waitlistId = assignBtn.getAttribute('data-waitlist-id');
            const patientId = assignBtn.getAttribute('data-patient-id');
            assignServices(waitlistId, patientId);
        }
    });
    
    // Event listener cho việc refresh
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'r') {
            e.preventDefault();
            loadServiceOrderWaitlist();
        }
    });
}); 