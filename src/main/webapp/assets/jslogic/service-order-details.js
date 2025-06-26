// Global variables
let currentServiceOrderData = null;
let currentHistoryData = null;
let searchTimeout = null; // For debouncing search
let allServices = [];
let selectedServices = [];
let patientInfo = {};


function removeVietnameseAccents(str) {
    if (!str) return '';
    
    const vietnameseMap = {
        'à': 'a', 'á': 'a', 'ả': 'a', 'ã': 'a', 'ạ': 'a',
        'ă': 'a', 'ằ': 'a', 'ắ': 'a', 'ẳ': 'a', 'ẵ': 'a', 'ặ': 'a',
        'â': 'a', 'ầ': 'a', 'ấ': 'a', 'ẩ': 'a', 'ẫ': 'a', 'ậ': 'a',
        'è': 'e', 'é': 'e', 'ẻ': 'e', 'ẽ': 'e', 'ẹ': 'e',
        'ê': 'e', 'ề': 'e', 'ế': 'e', 'ể': 'e', 'ễ': 'e', 'ệ': 'e',
        'ì': 'i', 'í': 'i', 'ỉ': 'i', 'ĩ': 'i', 'ị': 'i',
        'ò': 'o', 'ó': 'o', 'ỏ': 'o', 'õ': 'o', 'ọ': 'o',
        'ô': 'o', 'ồ': 'o', 'ố': 'o', 'ổ': 'o', 'ỗ': 'o', 'ộ': 'o',
        'ơ': 'o', 'ờ': 'o', 'ớ': 'o', 'ở': 'o', 'ỡ': 'o', 'ợ': 'o',
        'ù': 'u', 'ú': 'u', 'ủ': 'u', 'ũ': 'u', 'ụ': 'u',
        'ư': 'u', 'ừ': 'u', 'ứ': 'u', 'ử': 'u', 'ữ': 'u', 'ự': 'u',
        'ỳ': 'y', 'ý': 'y', 'ỷ': 'y', 'ỹ': 'y', 'ỵ': 'y',
        'đ': 'd',
        'À': 'A', 'Á': 'A', 'Ả': 'A', 'Ã': 'A', 'Ạ': 'A',
        'Ă': 'A', 'Ằ': 'A', 'Ắ': 'A', 'Ẳ': 'A', 'Ẵ': 'A', 'Ặ': 'A',
        'Â': 'A', 'Ầ': 'A', 'Ấ': 'A', 'Ẩ': 'A', 'Ẫ': 'A', 'Ậ': 'A',
        'È': 'E', 'É': 'E', 'Ẻ': 'E', 'Ẽ': 'E', 'Ẹ': 'E',
        'Ê': 'E', 'Ề': 'E', 'Ế': 'E', 'Ể': 'E', 'Ễ': 'E', 'Ệ': 'E',
        'Ì': 'I', 'Í': 'I', 'Ỉ': 'I', 'Ĩ': 'I', 'Ị': 'I',
        'Ò': 'O', 'Ó': 'O', 'Ỏ': 'O', 'Õ': 'O', 'Ọ': 'O',
        'Ô': 'O', 'Ồ': 'O', 'Ố': 'O', 'Ổ': 'O', 'Ỗ': 'O', 'Ộ': 'O',
        'Ơ': 'O', 'Ờ': 'O', 'Ớ': 'O', 'Ở': 'O', 'Ỡ': 'O', 'Ợ': 'O',
        'Ù': 'U', 'Ú': 'U', 'Ủ': 'U', 'Ũ': 'U', 'Ụ': 'U',
        'Ư': 'U', 'Ừ': 'U', 'Ứ': 'U', 'Ử': 'U', 'Ữ': 'U', 'Ự': 'U',
        'Ỳ': 'Y', 'Ý': 'Y', 'Ỷ': 'Y', 'Ỹ': 'Y', 'Ỵ': 'Y',
        'Đ': 'D'
    };
    
    return str.split('').map(char => vietnameseMap[char] || char).join('');
}


function containsSearchTerm(text, searchTerm) {
    if (!text || !searchTerm) return false;
    
    const normalizedText = removeVietnameseAccents(text.toLowerCase());
    const normalizedSearch = removeVietnameseAccents(searchTerm.toLowerCase());
    
    // Tách search term thành các từ riêng lẻ
    const searchWords = normalizedSearch.split(/\s+/).filter(word => word.length > 0);
    
    // Kiểm tra xem tất cả các từ có xuất hiện trong text không
    return searchWords.every(word => normalizedText.includes(word));
}


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

// Hàm tìm kiếm theo tên bệnh nhân (với debounce)
function searchByPatientName() {
    const patientName = document.getElementById("patientNameInput").value;
    
    if (!patientName) {
        // Nếu input rỗng, ẩn kết quả
        document.getElementById("serviceOrderHistoryCard").style.display = "none";
        return;
    }
    
    if (patientName.trim().length < 2) {
        // Nếu ít hơn 2 ký tự, không tìm kiếm
        return;
    }
    
    // Clear timeout cũ nếu có
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    
    // Set timeout mới (debounce 500ms)
    searchTimeout = setTimeout(() => {
        getServiceOrdersByPatientName(patientName.trim());
    }, 500);
}

// Hàm load lịch sử của bác sĩ
async function loadDoctorHistory() {
    await getDoctorServiceOrderHistory();
}

// Hàm lấy service orders theo tên bệnh nhân
async function getServiceOrdersByPatientName(patientName) {
    const historyCard = document.getElementById("serviceOrderHistoryCard");
    const loadingSpinner = document.getElementById("historyLoadingSpinner");
    const content = document.getElementById("serviceOrderHistoryContent");
    
    try {
        // Hiển thị loading
        historyCard.style.display = "block";
        loadingSpinner.style.display = "flex";
        content.style.display = "none";
        
        // Ẩn details card nếu đang hiển thị
        document.getElementById("serviceOrderDetailsCard").style.display = "none";
        
        const response = await fetch(`/api/doctor/service-order?action=getServiceOrdersByPatientName&patientName=${encodeURIComponent(patientName)}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized access. Please login again.');
            } else {
                throw new Error('Failed to get service orders by patient name');
            }
        }

        const result = await response.json();
        
        if (result.success) {
            // Lọc kết quả theo tên bệnh nhân (client-side filtering để hỗ trợ tìm kiếm linh hoạt)
            const filteredData = result.data.filter(order => {
                const patientNameInOrder = order.patient_name || '';
                return containsSearchTerm(patientNameInOrder, patientName);
            });
            
            currentHistoryData = filteredData;
            displayServiceOrderHistory(filteredData, `Service Orders for Patient: "${patientName}"`);
            
            // Chỉ hiển thị alert nếu có kết quả hoặc không có kết quả
            if (filteredData.length > 0) {
                showAlert(`Found ${filteredData.length} service order(s) for patient "${patientName}"`, 'success');
            } else {
                showAlert(`No service orders found for patient "${patientName}"`, 'info');
            }
        } else {
            throw new Error(result.message || 'Failed to get service orders by patient name');
        }

    } catch (error) {
        console.error("Error getting service orders by patient name:", error);
        showAlert(error.message || 'Failed to get service orders by patient name. Please try again.', 'danger');
        loadingSpinner.style.display = "none";
    }
}

// Hàm lấy chi tiết service order
async function getServiceOrderDetails(serviceOrderId) {
    const detailsCard = document.getElementById("serviceOrderDetailsCard");
    const loadingSpinner = document.getElementById("detailsLoadingSpinner");
    const content = document.getElementById("serviceOrderDetailsContent");
    
    try {
        // Hiển thị loading
        detailsCard.style.display = "block";
        loadingSpinner.style.display = "flex";
        content.style.display = "none";
        
        // Ẩn history card nếu đang hiển thị
        document.getElementById("serviceOrderHistoryCard").style.display = "none";
        
        const response = await fetch(`/api/doctor/service-order?action=getServiceOrder&serviceOrderId=${serviceOrderId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized access. Please login again.');
            } else if (response.status === 404) {
                throw new Error('Service order not found.');
            } else {
                throw new Error('Failed to get service order details');
            }
        }

        const result = await response.json();
        
        if (result.success) {
            currentServiceOrderData = result.data;
            displayServiceOrderDetails(result.data);
            showAlert('Service order details loaded successfully!', 'success');
        } else {
            throw new Error(result.message || 'Failed to get service order details');
        }

    } catch (error) {
        console.error("Error getting service order details:", error);
        showAlert(error.message || 'Failed to get service order details. Please try again.', 'danger');
        loadingSpinner.style.display = "none";
    }
}

// Hàm hiển thị chi tiết service order
function displayServiceOrderDetails(data) {
    const loadingSpinner = document.getElementById("detailsLoadingSpinner");
    const content = document.getElementById("serviceOrderDetailsContent");
    
    const serviceOrder = data.serviceOrder;
    const items = data.items;
    const totalAmount = data.totalAmount;
    
    // Cập nhật thông tin cơ bản
    document.getElementById("orderId").textContent = serviceOrder.service_order_id;
    document.getElementById("doctorName").textContent = serviceOrder.doctor_name || 'N/A';
    document.getElementById("patientName").textContent = serviceOrder.patient_name || 'N/A';
    document.getElementById("orderDate").textContent = formatDateTime(serviceOrder.order_date);
    document.getElementById("totalAmount").textContent = totalAmount.toLocaleString();
    
    // Hiển thị danh sách services
    const serviceItemsList = document.getElementById("serviceItemsList");
    serviceItemsList.innerHTML = '';
    
    if (items && items.length > 0) {
        items.forEach((item, index) => {
            const serviceItem = document.createElement("div");
            serviceItem.className = "service-order-item";
            serviceItem.innerHTML = `
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <h6><i class="fas fa-stethoscope me-2"></i>${item.service_name || 'Unknown Service'}</h6>
                        <p class="mb-0 text-muted">${item.service_description || 'No description available'}</p>
                        <small class="text-muted">Doctor: ${item.doctor_name || 'N/A'}</small>
                    </div>
                    <div class="col-md-4 text-end">
                        <span class="price-badge">${(item.service_price || 0).toLocaleString()} VND</span>
                    </div>
                </div>
            `;
            serviceItemsList.appendChild(serviceItem);
        });
    } else {
        serviceItemsList.innerHTML = '<div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>No services found for this order.</div>';
    }
    
    // Ẩn loading và hiển thị content
    loadingSpinner.style.display = "none";
    content.style.display = "block";
}

// Hàm lấy lịch sử service orders của bác sĩ
async function getDoctorServiceOrderHistory() {
    const historyCard = document.getElementById("serviceOrderHistoryCard");
    const loadingSpinner = document.getElementById("historyLoadingSpinner");
    const content = document.getElementById("serviceOrderHistoryContent");
    
    try {
        // Hiển thị loading
        historyCard.style.display = "block";
        loadingSpinner.style.display = "flex";
        content.style.display = "none";
        
        // Ẩn details card nếu đang hiển thị
        document.getElementById("serviceOrderDetailsCard").style.display = "none";
        
        const response = await fetch('/api/doctor/service-order?action=getServiceOrdersByDoctor', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized access. Please login again.');
            } else if (response.status === 403) {
                throw new Error('Access denied. Doctor role required.');
            } else {
                throw new Error('Failed to get doctor service order history');
            }
        }

        const result = await response.json();
        
        if (result.success) {
            currentHistoryData = result.data;
            displayServiceOrderHistory(result.data, 'My Service Order History');
          
        } else {
            throw new Error(result.message || 'Failed to get doctor service order history');
        }

    } catch (error) {
        console.error("Error getting doctor service order history:", error);
        showAlert(error.message || 'Failed to get doctor service order history. Please try again.', 'danger');
        loadingSpinner.style.display = "none";
    }
}

// Hàm hiển thị lịch sử service orders
function displayServiceOrderHistory(historyData, title) {
    const loadingSpinner = document.getElementById("historyLoadingSpinner");
    const content = document.getElementById("serviceOrderHistoryContent");
    const historyList = document.getElementById("historyList");
    
    // Cập nhật title
    const cardTitle = document.querySelector("#serviceOrderHistoryCard .card-title");
    cardTitle.innerHTML = `<i class="fas fa-history me-2"></i>${title}`;
    
    historyList.innerHTML = '';
    
    if (historyData && historyData.length > 0) {
        historyData.forEach((order, index) => {
            const historyItem = document.createElement("div");
            historyItem.className = "history-item";
            
            const items = order.items || [];
            const totalAmount = order.totalAmount || 0;
            
            historyItem.innerHTML = `
                <div class="row align-items-center">
                    <div class="col-md-3">
                        <h6><i class="fas fa-file-medical me-2"></i>Order #${order.service_order_id}</h6>
                        <small class="text-muted">${formatDateTime(order.order_date)}</small>
                    </div>
                    <div class="col-md-3">
                        <strong>Patient:</strong> ${order.patient_name || 'N/A'}<br>
                        <strong>Doctor:</strong> ${order.doctor_name || 'N/A'}
                    </div>
                    <div class="col-md-3">
                        <strong>Services:</strong> ${items.length} items<br>
                        <small class="text-muted">${items.length > 0 ? items.map(item => item.service_name || 'Unknown').join(', ') : 'No services'}</small>
                    </div>
                    <div class="col-md-3 text-end">
                        <div class="price-badge">${totalAmount.toLocaleString()} VND</div>
                        <button class="btn btn-sm btn-outline-primary mt-2" onclick="viewOrderDetails(${order.service_order_id})">
                            <i class="fas fa-eye me-1"></i>View Details
                        </button>
                    </div>
                </div>
            `;
            historyList.appendChild(historyItem);
        });
    } else {
        historyList.innerHTML = '<div class="alert alert-info"><i class="fas fa-info-circle me-2"></i>No service orders found.</div>';
    }
    
    // Ẩn loading và hiển thị content
    loadingSpinner.style.display = "none";
    content.style.display = "block";
}

// Hàm xem chi tiết order từ lịch sử
function viewOrderDetails(serviceOrderId) {
    getServiceOrderDetails(serviceOrderId);
    
    // Scroll to details section
    document.getElementById("serviceOrderDetailsCard").scrollIntoView({ behavior: 'smooth' });
}

// Demo functions
function demoGetServiceOrderDetails() {
    // Demo với ID 1 (có thể thay đổi)
    getServiceOrderDetails(1);
}

function demoGetPatientHistory() {
    // Demo với tên bệnh nhân
    document.getElementById("patientNameInput").value = "Nguyen";
    getServiceOrdersByPatientName("Nguyen");
}

function demoGetDoctorHistory() {
    getDoctorServiceOrderHistory();
}

// Utility functions
function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return 'Invalid Date';
        return date.toLocaleString('en-GB');
    } catch (error) {
        return 'Invalid Date';
    }
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Auto-search khi gõ (với debounce)
    document.getElementById("patientNameInput").addEventListener('input', function() {
        searchByPatientName();
    });
    
    // Enter key handlers (vẫn giữ lại để tìm kiếm ngay lập tức)
    document.getElementById("patientNameInput").addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            // Clear timeout và tìm kiếm ngay lập tức
            if (searchTimeout) {
                clearTimeout(searchTimeout);
            }
            const patientName = this.value.trim();
            if (patientName.length >= 2) {
                getServiceOrdersByPatientName(patientName);
            }
        }
    });
    

    loadDoctorHistory();
    
    
});

// Hàm khởi tạo trang
async function initializeServiceOrderDetailsPage() {
    try {
        // Lấy thông tin từ URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const waitlistId = urlParams.get('waitlistId');
        const patientId = urlParams.get('patientId');
        
        if (!waitlistId || !patientId) {
            showAlert('Missing required parameters', 'danger');
            return;
        }
        
        // Load thông tin bệnh nhân
        await loadPatientInfo(waitlistId, patientId);
        
        // Load danh sách services
        await loadServices();
        
    } catch (error) {
        console.error("Error initializing service order details page:", error);
        showAlert('Failed to initialize page. Please try again.', 'danger');
    }
}

// Hàm load thông tin bệnh nhân
async function loadPatientInfo(waitlistId, patientId) {
    try {
        const response = await fetch(`/api/doctor/waitlist?action=detail&id=${waitlistId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Failed to load patient information');

        const waitlist = await response.json();
        patientInfo = waitlist;

        // Hiển thị thông tin bệnh nhân
        document.getElementById("patientId").textContent = waitlist.patient_id ?? 'N/A';
        document.getElementById("patientName").textContent = waitlist.full_name ?? 'N/A';
        document.getElementById("patientAge").textContent = waitlist.dob ? calculateAge(waitlist.dob) : 'N/A';
        document.getElementById("patientGender").textContent = waitlist.gender ?? 'N/A';
        document.getElementById("patientPhone").textContent = waitlist.phone ?? 'N/A';
        
        // Hiển thị Medicine Record ID (cần lấy từ examination record)
        await loadMedicineRecordId(waitlistId);
        
    } catch (error) {
        console.error("Error loading patient info:", error);
        showAlert('Failed to load patient information', 'danger');
    }
}

// Hàm load Medicine Record ID
async function loadMedicineRecordId(waitlistId) {
    try {
        // Gọi API để lấy medicine record ID từ waitlist
        const response = await fetch(`/api/doctor/examination?action=getMedicineRecord&waitlistId=${waitlistId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            if (result.success && result.medicineRecordId) {
                document.getElementById("medicineRecordId").textContent = result.medicineRecordId;
            } else {
                document.getElementById("medicineRecordId").textContent = 'Not found';
            }
        } else {
            document.getElementById("medicineRecordId").textContent = 'Error loading';
        }
    } catch (error) {
        console.error("Error loading medicine record ID:", error);
        document.getElementById("medicineRecordId").textContent = 'Error';
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

// Hàm load danh sách services
async function loadServices() {
    const loadingSpinner = document.getElementById("loadingSpinner");
    const servicesContainer = document.getElementById("servicesContainer");
    
    try {
        loadingSpinner.style.display = "flex";
        servicesContainer.style.display = "none";

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
            loadingSpinner.style.display = "none";
            servicesContainer.style.display = "block";
        } else {
            throw new Error(result.message || 'Failed to load services');
        }

    } catch (error) {
        console.error("Error loading services:", error);
        loadingSpinner.style.display = "none";
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
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="service-price">${servicePrice.toLocaleString()} VND</span>
                    </div>
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

// Hàm tạo service order
async function createServiceOrder() {
    try {
        const medicineRecordId = document.getElementById("medicineRecordId").textContent;
        
        if (medicineRecordId === 'Loading...' || medicineRecordId === 'Not found' || medicineRecordId === 'Error' || medicineRecordId === 'Error loading') {
            showAlert('Missing medicine record ID', 'danger');
            return;
        }
        
        if (selectedServices.length === 0) {
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
            
            // Quay lại trang service order
            setTimeout(() => {
                window.location.href = 'service-order.html';
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
        window.location.href = 'service-order.html';
    }, 1500);
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo trang khi load
    initializeServiceOrderDetailsPage();
}); 