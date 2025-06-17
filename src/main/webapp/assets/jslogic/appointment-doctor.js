document.addEventListener('DOMContentLoaded', async function () {
    const tableBody = document.getElementById('appointmentTableBody');
    tableBody.innerHTML = '<tr><td colspan="6">Đang tải...</td></tr>';

    try {
        const response = await fetch('/api/doctor/appointment', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            tableBody.innerHTML = '<tr><td colspan="6">Không thể tải dữ liệu</td></tr>';
            return;
        }

        const appointments = await response.json();

        if (appointments.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="6">Không có lịch hẹn nào.</td></tr>';
            return;
        }

        tableBody.innerHTML = '';
        appointments.forEach((a, index) => {
            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td>${a.patient_id}</td>
                    <td>${a.appointment_datetime}</td>
                    <td>${a.shift}</td>
                    <td>${a.status}</td>
                    <td>${a.note || ''}</td>
                </tr>
            `;
            tableBody.insertAdjacentHTML('beforeend', row);
        });

    } catch (err) {
        console.error(err);
        tableBody.innerHTML = '<tr><td colspan="6">Lỗi khi tải dữ liệu</td></tr>';
    }
});
