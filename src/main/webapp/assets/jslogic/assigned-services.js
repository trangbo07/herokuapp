let allAssignedServices = [];
let filteredServices = [];
let currentStatusFilter = 'all';
let currentSearchTerm = '';

document.addEventListener('DOMContentLoaded', initializePage);

function initializePage() {
    setupEventListeners();
    fetchAndRenderServices();
}

function setupEventListeners() {
    const filterButtons = document.querySelectorAll('[data-status-filter]');
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            filterButtons.forEach(btn => btn.classList.remove('btn-primary', 'active'));
            filterButtons.forEach(btn => btn.classList.add('btn-outline-primary'));
            button.classList.remove('btn-outline-primary');
            button.classList.add('btn-primary', 'active');
            
            currentStatusFilter = button.getAttribute('data-status-filter');
            applyFiltersAndRender();
        });
    });

    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('keyup', (e) => {
        currentSearchTerm = e.target.value.toLowerCase();
        applyFiltersAndRender();
    });
}

async function fetchAndRenderServices() {
    const servicesList = document.getElementById("servicesList");
    const errorMessage = document.getElementById("errorMessage");
    const noServicesMessage = document.getElementById("noServicesMessage");
    
    try {
        servicesList.style.display = "none";
        errorMessage.classList.add("d-none");
        noServicesMessage.classList.add("d-none");

        const response = await fetch('/api/doctor/service-order?action=getAssignedServices');
        if (!response.ok) throw new Error('Failed to fetch assigned services from server.');
        
        const result = await response.json();
        if (!result.success) throw new Error(result.message || 'Failed to process assigned services data.');
        
        allAssignedServices = result.data || [];
        applyFiltersAndRender();
        
    } catch (error) {
        errorMessage.classList.remove("d-none");
        document.getElementById("errorText").textContent = error.message;
        console.error("Error loading assigned services:", error);
    }
}

// Hàm tiện ích bỏ dấu tiếng Việt
function removeDiacritics(str) {
    if (!str) return '';
    return str
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/đ/g, 'd')
        .replace(/Đ/g, 'D');
}

function applyFiltersAndRender() {
    let servicesToRender = allAssignedServices;

    // 1. Filter by status
    if (currentStatusFilter !== 'all') {
        servicesToRender = allAssignedServices.filter(service => {
            const hasResults = service.result_description && service.result_description.trim() !== '';
            const status = hasResults ? 'completed' : 'pending';
            return status === currentStatusFilter;
        });
    }

    // 2. Filter by search term (case-insensitive and diacritic-insensitive)
    if (currentSearchTerm) {
        const normalizedSearchTerm = removeDiacritics(currentSearchTerm).toLowerCase();
        servicesToRender = servicesToRender.filter(service => {
            const normalizedPatientName = removeDiacritics(service.patient_name).toLowerCase();
            const normalizedServiceName = removeDiacritics(service.service_name).toLowerCase();
            
            return normalizedPatientName.includes(normalizedSearchTerm) || 
                   normalizedServiceName.includes(normalizedSearchTerm);
        });
    }
    
    filteredServices = servicesToRender;
    renderServices();
}

function renderServices() {
    const servicesList = document.getElementById("servicesList");
    const noServicesMessage = document.getElementById("noServicesMessage");
    
    servicesList.innerHTML = '';
    
    if (filteredServices.length === 0) {
        servicesList.style.display = "none";
        noServicesMessage.classList.remove("d-none");
        return;
    }
    
    servicesList.style.display = "flex";
    noServicesMessage.classList.add("d-none");

    filteredServices.forEach((service, index) => {
        const hasResults = service.result_description && service.result_description.trim() !== '';
        const status = hasResults ? 'completed' : 'pending';
        const statusInfo = {
            'pending': { text: 'Pending', class: 'warning', icon: 'fas fa-clock' },
            'completed': { text: 'Completed', class: 'success', icon: 'fas fa-check-circle' }
        };

        const serviceCard = document.createElement("div");
        serviceCard.className = "col-xl-6 col-lg-12 mb-4";
        
        const cardId = `service-card-${service.service_order_item_id}`;

        serviceCard.innerHTML = `
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-flask me-2 text-primary"></i>
                        ${service.service_name || 'Unnamed Service'}
                    </h5>
                    <span class="badge bg-${statusInfo[status].class}">
                        <i class="${statusInfo[status].icon} me-1"></i>
                        ${statusInfo[status].text}
                    </span>
                </div>
                <div class="card-body">
                     <p class="mb-3">
                        <strong>Patient:</strong> ${service.patient_name || 'N/A'} <br>
                        <strong>Order Date:</strong> ${formatDateTime(service.order_date)}
                    </p>
                    
                    <nav>
                        <div class="nav nav-tabs" id="nav-tab-${cardId}" role="tablist">
                            <button class="nav-link active" id="nav-exam-tab-${cardId}" data-bs-toggle="tab" data-bs-target="#nav-exam-${cardId}" type="button" role="tab">Exam Info</button>
                            <button class="nav-link" id="nav-result-tab-${cardId}" data-bs-toggle="tab" data-bs-target="#nav-result-${cardId}" type="button" role="tab">Results</button>
                        </div>
                    </nav>
                    <div class="tab-content pt-3" id="nav-tabContent-${cardId}">
                        <div class="tab-pane fade show active" id="nav-exam-${cardId}" role="tabpanel">
                            <h6><i class="fas fa-stethoscope me-2"></i>Examination Details</h6>
                            <p><strong>Symptoms:</strong><br>${service.symptoms || 'Not provided'}</p>
                            <p><strong>Preliminary Diagnosis:</strong><br>${service.preliminary_diagnosis || 'Not provided'}</p>
                        </div>
                        <div class="tab-pane fade" id="nav-result-${cardId}" role="tabpanel">
                             <h6><i class="fas fa-clipboard-check me-2"></i>Test Results</h6>
                             <div id="result-display-${service.service_order_item_id}">
                                <!-- Result content will be rendered here -->
                             </div>
                             <button class="btn btn-${hasResults ? 'success' : 'primary'} mt-3" onclick="openResultModal(${service.service_order_item_id})">
                                <i class="fas fa-edit me-2"></i>${hasResults ? 'Update Results' : 'Enter Results'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        servicesList.appendChild(serviceCard);
        renderResultDisplay(service.service_order_item_id);
    });
}

function renderResultDisplay(serviceOrderItemId) {
    const service = allAssignedServices.find(s => s.service_order_item_id === serviceOrderItemId);
    const resultContainer = document.getElementById(`result-display-${serviceOrderItemId}`);

    if (!resultContainer) return;
    
    if (service && service.result_description) {
         const lines = service.result_description.split('\\n');
         let resultHtml = '<dl class="row">';
         lines.forEach(line => {
             if (line.includes(':')) {
                 const [key, ...valueParts] = line.split(':');
                 const value = valueParts.join(':').trim();
                 resultHtml += `
                    <dt class="col-sm-4">${key.trim()}</dt>
                    <dd class="col-sm-8">${value}</dd>`;
             }
         });
         resultHtml += '</dl>';
         resultContainer.innerHTML = resultHtml;
    } else {
        resultContainer.innerHTML = '<p class="text-muted">No results have been entered yet.</p>';
    }
}

function openResultModal(serviceOrderItemId) {
    const service = allAssignedServices.find(s => s.service_order_item_id === serviceOrderItemId);
    if (!service) return;

    document.getElementById("serviceOrderItemId").value = service.service_order_item_id;
    document.getElementById("modalServiceName").value = service.service_name || '';
    document.getElementById("modalPatientName").value = service.patient_name || '';
    
    // Reset form fields
    document.getElementById("testResults").value = '';
    document.getElementById("conclusion").value = '';
    document.getElementById("resultStatus").value = '';

    // Load existing results into form if available
    if (service.result_description) {
        const lines = service.result_description.split('\\n');
        lines.forEach(line => {
            if (line.startsWith('Test Results:')) {
                document.getElementById("testResults").value = line.replace('Test Results:', '').trim();
            } else if (line.startsWith('Conclusion:')) {
                document.getElementById("conclusion").value = line.replace('Conclusion:', '').trim();
            } else if (line.startsWith('Status:')) {
                document.getElementById("resultStatus").value = line.replace('Status:', '').trim();
            }
        });
    }
    
    const resultModal = new bootstrap.Modal(document.getElementById("resultModal"));
    resultModal.show();
}

async function saveTestResult() {
    const saveButton = document.querySelector('#resultModal .btn-primary');
    try {
        saveButton.disabled = true;
        saveButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving...';

        const serviceOrderItemId = document.getElementById("serviceOrderItemId").value;
        const testResults = document.getElementById("testResults").value;
        const conclusion = document.getElementById("conclusion").value;
        const resultStatus = document.getElementById("resultStatus").value;
        
        if (!testResults.trim() || !resultStatus) {
            showAlert('Please fill in all required fields: Test Results and Status.', 'danger');
            return;
        }
        
        const description = `Test Results: ${testResults}\\nConclusion: ${conclusion}\\nStatus: ${resultStatus}`;

        const response = await fetch('/api/doctor/service-result', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                serviceOrderItemId: parseInt(serviceOrderItemId),
                resultDescription: description
            })
        });

        const result = await response.json();

        if (result.success) {
            showAlert('Test results saved successfully!', 'success');
            
            const modal = bootstrap.Modal.getInstance(document.getElementById("resultModal"));
            modal.hide();
            
            await fetchAndRenderServices();
        } else {
            throw new Error(result.message || 'Failed to save test results');
        }

    } catch (error) {
        console.error("Error saving test result:", error);
        showAlert(error.message, 'danger');
    } finally {
        saveButton.disabled = false;
        saveButton.innerHTML = '<i class="fas fa-save me-2"></i>Save Results';
    }
}

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.main-content .container-fluid');
    container.insertBefore(alertDiv, container.firstChild);
    
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'N/A';
    
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        return dateTimeString;
    }
}

function logout() {
    if (confirm('Are you sure you want to logout?')) {
        window.location.href = '../index.html';
    }
} 