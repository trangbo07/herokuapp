// Services Management JavaScript

class ServicesManager {
    constructor() {
        this.servicesData = [];
        this.init();
    }

    init() {
        this.loadServices();
    }

    async loadServices() {
        try {
            // Build URL relative to current page location
            const currentPath = window.location.pathname;
            const basePath = currentPath.substring(0, currentPath.lastIndexOf('/'));
            const url = `${basePath}/../services?action=api`;
            
            console.log('Loading services from:', url); // Debug log
            
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            this.servicesData = await response.json();
            this.renderServicesTable();
        } catch (error) {
            console.error('Error loading services:', error);
            this.showError('Failed to load services. Please try again later.');
        }
    }

    renderServicesTable() {
        const tbody = document.getElementById('servicesTableBody');
        if (!tbody) {
            console.error('Services table body not found');
            return;
        }

        if (this.servicesData.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">
                        <div class="alert alert-info mb-0">
                            <i class="icon-20" data-bs-toggle="tooltip" title="No Services">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0z"/>
                                    <path d="M8 4a.905.905 0 0 0-.9.995l.35 3.507a.552.552 0 0 0 1.1 0l.35-3.507A.905.905 0 0 0 8 4zm.002 6a1 1 0 1 0 0 2 1 1 0 0 0 0-2z"/>
                                </svg>
                            </i>
                            No medical services found
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        let tableRows = '';
        this.servicesData.forEach((service, index) => {
            const formattedPrice = this.formatPrice(service.price);
            tableRows += `
                <tr data-item="list">
                    <th scope="row">${index + 1}</th>
                    <td>#${service.service_id}</td>
                    <td>
                        <h6 class="mb-0 text-body fw-normal">${this.escapeHtml(service.name)}</h6>
                    </td>
                    <td>
                        <span class="text-muted small">${this.escapeHtml(service.description || 'No description')}</span>
                    </td>
                    <td>
                        <span class="text-success fw-bold">${formattedPrice}</span>
                    </td>
                    <td>
                        <div class="dropdown">
                            <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" 
                                    data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="icon-20">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z"/>
                                    </svg>
                                </i>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#" onclick="servicesManager.viewService(${service.service_id})">
                                    <i class="icon-16 me-2">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                            <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                        </svg>
                                    </i>
                                    View Details
                                </a></li>
                                <li><a class="dropdown-item" href="#" onclick="servicesManager.editService(${service.service_id})">
                                    <i class="icon-16 me-2 text-warning">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708L14.5 5.207l-8 8L2 14l.793-4.5 8-8L12.146.146zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293L12.793 5.5zM9.854 8.146a.5.5 0 0 0-.708.708l.647.646a.5.5 0 0 0 .708-.708l-.647-.646z"/>
                                        </svg>
                                    </i>
                                    Edit Service
                                </a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            `;
        });

        tbody.innerHTML = tableRows;
    }

    formatPrice(price) {
        if (typeof price === 'number') {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(price);
        }
        return price;
    }

    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showError(message) {
        const tbody = document.getElementById('servicesTableBody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">
                        <div class="alert alert-danger mb-0">
                            <i class="icon-20 me-2">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
                                </svg>
                            </i>
                            ${message}
                        </div>
                    </td>
                </tr>
            `;
        }
    }

    viewService(serviceId) {
        const service = this.servicesData.find(s => s.service_id === serviceId);
        if (service) {
            // Show service details in a modal or alert
            alert(`Service Details:\n\nID: ${service.service_id}\nName: ${service.name}\nDescription: ${service.description}\nPrice: ${this.formatPrice(service.price)}`);
        }
    }

    editService(serviceId) {
        // Future implementation for editing services
        alert(`Edit functionality for service ID ${serviceId} will be implemented in the future.`);
    }

    refreshServices() {
        this.loadServices();
    }
}

// Initialize services manager when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.servicesManager = new ServicesManager();
});

// Refresh function for external use
function refreshServicesTable() {
    if (window.servicesManager) {
        window.servicesManager.refreshServices();
    }
} 