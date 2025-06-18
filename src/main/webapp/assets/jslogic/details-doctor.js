
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


    document.getElementById('doctor-name').textContent = data.full_name;
    document.getElementById('doctor-department').textContent = data.department;
    document.getElementById('doctor-eduLevel').textContent = data.eduLevel;
    document.getElementById('doctor-phone').textContent = data.phone;




} catch (err) {
    console.error('error upload details doctor:', err);

}
});

