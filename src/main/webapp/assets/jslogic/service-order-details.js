// Global variables
let currentServiceOrderData = null;
let currentHistoryData = null;
let searchTimeout = null; // For debouncing search


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
    
    // Tự động ẩn alert sau 1 giây
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 1000);
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