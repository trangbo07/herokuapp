// Global variables
let currentServiceOrderData = null;
let currentHistoryData = null;

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

// Hàm tìm kiếm service order theo ID
async function searchServiceOrder() {
    const serviceOrderId = document.getElementById("serviceOrderIdInput").value;
    
    if (!serviceOrderId) {
        showAlert('Please enter a Service Order ID', 'danger');
        return;
    }
    
    await getServiceOrderDetails(parseInt(serviceOrderId));
}

// Hàm tìm kiếm theo medicine record ID
async function searchByMedicineRecord() {
    const medicineRecordId = document.getElementById("medicineRecordIdInput").value;
    
    if (!medicineRecordId) {
        showAlert('Please enter a Medicine Record ID', 'danger');
        return;
    }
    
    await getServiceOrderHistory(parseInt(medicineRecordId));
}

// Hàm load lịch sử của bác sĩ
async function loadDoctorHistory() {
    await getDoctorServiceOrderHistory();
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
            currentServiceOrderData = result.data;
            displayServiceOrderDetails(result.data);
            showAlert('Service order details loaded successfully!', 'success');
        } else {
            throw new Error(result.message || 'Failed to get service order details');
        }

    } catch (error) {
        console.error("Error getting service order details:", error);
        showAlert('Failed to get service order details. Please try again.', 'danger');
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
    document.getElementById("doctorName").textContent = serviceOrder.doctor_name;
    document.getElementById("patientName").textContent = serviceOrder.patient_name;
    document.getElementById("orderDate").textContent = formatDateTime(serviceOrder.order_date);
    document.getElementById("totalAmount").textContent = totalAmount.toLocaleString();
    
    // Hiển thị danh sách services
    const serviceItemsList = document.getElementById("serviceItemsList");
    serviceItemsList.innerHTML = '';
    
    if (items && items.length > 0) {
        items.forEach(item => {
            const serviceItem = document.createElement("div");
            serviceItem.className = "service-order-item";
            serviceItem.innerHTML = `
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <h6><i class="fas fa-stethoscope me-2"></i>${item.service_name}</h6>
                        <p class="mb-0 text-muted">${item.service_description || 'No description'}</p>
                        <small class="text-muted">Doctor: ${item.doctor_name}</small>
                    </div>
                    <div class="col-md-4 text-end">
                        <span class="price-badge">${item.service_price.toLocaleString()} VND</span>
                    </div>
                </div>
            `;
            serviceItemsList.appendChild(serviceItem);
        });
    } else {
        serviceItemsList.innerHTML = '<div class="alert alert-warning">No services found for this order.</div>';
    }
    
    // Ẩn loading và hiển thị content
    loadingSpinner.style.display = "none";
    content.style.display = "block";
}

// Hàm lấy lịch sử service orders theo medicine record
async function getServiceOrderHistory(medicineRecordId) {
    const historyCard = document.getElementById("serviceOrderHistoryCard");
    const loadingSpinner = document.getElementById("historyLoadingSpinner");
    const content = document.getElementById("serviceOrderHistoryContent");
    
    try {
        // Hiển thị loading
        historyCard.style.display = "block";
        loadingSpinner.style.display = "flex";
        content.style.display = "none";
        
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
            currentHistoryData = result.data;
            displayServiceOrderHistory(result.data, `Patient History (Medicine Record ID: ${medicineRecordId})`);
            showAlert('Service order history loaded successfully!', 'success');
        } else {
            throw new Error(result.message || 'Failed to get service order history');
        }

    } catch (error) {
        console.error("Error getting service order history:", error);
        showAlert('Failed to get service order history. Please try again.', 'danger');
        loadingSpinner.style.display = "none";
    }
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
            currentHistoryData = result.data;
            displayServiceOrderHistory(result.data, 'My Service Order History');
            showAlert('Doctor service order history loaded successfully!', 'success');
        } else {
            throw new Error(result.message || 'Failed to get doctor service order history');
        }

    } catch (error) {
        console.error("Error getting doctor service order history:", error);
        showAlert('Failed to get doctor service order history. Please try again.', 'danger');
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
                        <strong>Patient:</strong> ${order.patient_name}<br>
                        <strong>Doctor:</strong> ${order.doctor_name}
                    </div>
                    <div class="col-md-3">
                        <strong>Services:</strong> ${items.length} items<br>
                        <small class="text-muted">${items.map(item => item.service_name).join(', ')}</small>
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
        historyList.innerHTML = '<div class="alert alert-info">No service orders found.</div>';
    }
    
    // Ẩn loading và hiển thị content
    loadingSpinner.style.display = "none";
    content.style.display = "block";
}

// Hàm xem chi tiết order từ lịch sử
function viewOrderDetails(serviceOrderId) {
    document.getElementById("serviceOrderIdInput").value = serviceOrderId;
    getServiceOrderDetails(serviceOrderId);
    
    // Scroll to details section
    document.getElementById("serviceOrderDetailsCard").scrollIntoView({ behavior: 'smooth' });
}

// Demo functions
function demoGetServiceOrderDetails() {
    // Demo với ID 1 (có thể thay đổi)
    document.getElementById("serviceOrderIdInput").value = "1";
    getServiceOrderDetails(1);
}

function demoGetPatientHistory() {
    // Demo với medicine record ID 1 (có thể thay đổi)
    document.getElementById("medicineRecordIdInput").value = "1";
    getServiceOrderHistory(1);
}

function demoGetDoctorHistory() {
    getDoctorServiceOrderHistory();
}

// Utility functions
function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString('en-GB');
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Enter key handlers
    document.getElementById("serviceOrderIdInput").addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchServiceOrder();
        }
    });
    
    document.getElementById("medicineRecordIdInput").addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchByMedicineRecord();
        }
    });
    
    // Show welcome message
    showAlert('Welcome to Service Order Details page! Use the search functions above to explore service orders.', 'info');
}); 