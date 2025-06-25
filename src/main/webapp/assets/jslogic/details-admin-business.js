document.addEventListener('DOMContentLoaded', function() {
    // Load admin business profile data when page loads
    loadAdminBusinessProfile();
});

function loadAdminBusinessProfile() {
    // Get admin business ID from session or URL parameter
    const adminId = getAdminIdFromSession();
    
    // Build URL - if adminId is null, server will use session
    let url = '/SWP391_up/api/admin-business/profile';
    if (adminId) {
        url += `?adminId=${adminId}`;
    }

    // Make AJAX request to get admin business profile data
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                populateAdminBusinessProfile(data.adminBusiness);
            } else {
                console.error('Error loading admin business profile:', data.message);
                showErrorMessage('Failed to load profile data: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorMessage('Error loading profile data');
        });
}

function populateAdminBusinessProfile(adminBusiness) {
    // Update profile image if available
    if (adminBusiness.profileImage) {
        document.getElementById('admin-img').src = adminBusiness.profileImage;
    }
    
    // Update admin name
    document.getElementById('admin-name').textContent = adminBusiness.full_name || 'N/A';
    
    // Update profile details
    document.getElementById('admin-id').textContent = adminBusiness.admin_id || 'N/A';
    document.getElementById('admin-department').textContent = adminBusiness.department || 'N/A';
    document.getElementById('admin-phone').textContent = adminBusiness.phone || 'N/A';
    document.getElementById('admin-account-staff-id').textContent = adminBusiness.account_staff_id || 'N/A';
}

function getAdminIdFromSession() {
    // Since we're using session-based authentication on the server side,
    // we don't need to pass the adminId explicitly in the client.
    // The server will get it from the session automatically.
    // Return null to trigger server-side session lookup
    return null;
}

function showErrorMessage(message) {
    // Create and show error alert
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.setAttribute('role', 'alert');
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    // Insert at the top of the content area
    const contentArea = document.querySelector('.content-inner');
    contentArea.insertBefore(alertDiv, contentArea.firstChild);
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Change password functionality
document.getElementById('show-change-password').addEventListener('click', function() {
    const section = document.getElementById('change-password-section');
    const securitySection = document.getElementById('security-info-section');
    
    section.style.display = (section.style.display === 'none' || section.style.display === '') ? 'block' : 'none';
    securitySection.style.display = 'none';
});

// Security info functionality
document.getElementById('show-security-info').addEventListener('click', function() {
    const section = document.getElementById('security-info-section');
    const passwordSection = document.getElementById('change-password-section');
    
    section.style.display = (section.style.display === 'none' || section.style.display === '') ? 'block' : 'none';
    passwordSection.style.display = 'none';
});

// Profile detail toggle functionality
document.getElementById('toggle-profile-detail').addEventListener('click', function() {
    const section = document.getElementById('profile-detail-section');
    const button = this;
    
    if (section.style.display === 'none' || section.style.display === '') {
        section.style.display = 'block';
        button.innerHTML = '<i class="fas fa-chevron-up me-2"></i>Hide Details';
    } else {
        section.style.display = 'none';
        button.innerHTML = '<i class="fas fa-chevron-down me-2"></i>Show Details';
    }
});

// Change password form submission
document.querySelector('#change-password-section .btn-success').addEventListener('click', function() {
    const newPassword = document.getElementById('change-password').value;
    
    if (!newPassword || newPassword.length < 6) {
        alert('Password must be at least 6 characters long');
        return;
    }
    
    // Get adminId from the populated profile data
    const adminIdElement = document.getElementById('admin-id');
    const adminId = adminIdElement ? adminIdElement.textContent : null;
    
    if (!adminId || adminId === 'N/A') {
        alert('Unable to change password: Admin ID not found');
        return;
    }
    
    fetch('/SWP391_up/api/admin-business/change-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            adminId: parseInt(adminId),
            newPassword: newPassword
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Password changed successfully');
            document.getElementById('change-password').value = '';
            document.getElementById('change-password-section').style.display = 'none';
        } else {
            alert('Error changing password: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error changing password');
    });
}); 