document.addEventListener('DOMContentLoaded', async function () {
    try {
        const response = await fetch('/api/doctor/profile', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('error upload details from api');
        }

        const data = await response.json();


        document.getElementById('doctor-name').textContent = data.fullName;
        document.getElementById('doctor-department').textContent = data.department;
        document.getElementById('doctor-eduLevel').textContent = data.eduLevel;
        document.getElementById('doctor-phone').textContent = data.phone;
        document.getElementById('doctor-email').textContent = data.email;
        document.getElementById('doctor-username').textContent = data.username;


        document.getElementById('doctor-img').src = data.img;

    } catch (err) {
        console.error('error upload details doctor:', err);
    }
});



document.addEventListener('DOMContentLoaded', function() {
    // Account Center toggles
    const btnChangePass = document.getElementById('show-change-password');
    const btnSecurity = document.getElementById('show-security-info');
    const sectionChangePass = document.getElementById('change-password-section');
    const sectionSecurity = document.getElementById('security-info-section');
    btnChangePass.addEventListener('click', function() {
        sectionChangePass.style.display = (sectionChangePass.style.display === 'none' || sectionChangePass.style.display === '') ? 'block' : 'none';
    });
    btnSecurity.addEventListener('click', function() {
        sectionSecurity.style.display = (sectionSecurity.style.display === 'none' || sectionSecurity.style.display === '') ? 'block' : 'none';
    });
    // Profile Card toggle
    const btnProfileDetail = document.getElementById('toggle-profile-detail');
    const sectionProfileDetail = document.getElementById('profile-detail-section');
    let detailOpen = false;
    btnProfileDetail.addEventListener('click', function() {
        detailOpen = !detailOpen;
        if(detailOpen) {
            sectionProfileDetail.style.display = 'block';
            btnProfileDetail.innerHTML = '<i class="fas fa-chevron-up me-2"></i>Hide Details';
        } else {
            sectionProfileDetail.style.display = 'none';
            btnProfileDetail.innerHTML = '<i class="fas fa-chevron-down me-2"></i>Show Details';
        }
    });
});