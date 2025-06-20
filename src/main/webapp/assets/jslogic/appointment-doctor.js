let allAppointments = [];
let currentPage = 1;
const pageSize = 7;

// Hàm dùng chung để fetch theo action
async function fetchAppointmentsByAction(action) {
    const tableBody = document.getElementById("appointmentTableBody");
    const info = document.getElementById("paginationInfo");
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="7">Đang tải...</td></tr>';

    try {
        const res = await fetch('/api/doctor/appointment', {
            method: 'POST', credentials: 'include', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify({action: action})
        });

        const appointments = await res.json();

        if (!Array.isArray(appointments) || appointments.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7">No appointments available.</td></tr>';
            if (info) info.innerText = `Showing 0 to 0 of 0 entries`;
            return;
        }

        allAppointments = Array.isArray(appointments) ? appointments : [];
        currentPage = 1;
        paginateAppointments();


    } catch (err) {
        console.error(err);
        tableBody.innerHTML = '<tr><td colspan="7">Lỗi khi tải dữ liệu</td></tr>';
    }
}

function paginateAppointments() {
    const tableBody = document.getElementById("appointmentTableBody");
    const info = document.getElementById("paginationInfo");
    if (!tableBody) return;

    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pageData = allAppointments.slice(start, end);

    tableBody.innerHTML = '';

    if (pageData.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7">No appointments available.</td></tr>';
        if (info) info.innerText = `Showing 0 to 0 of ${allAppointments.length} entries`;
        return;
    }

    pageData.forEach((a, index) => {
        const viewBtn = `<button class="btn btn-sm btn-outline-info" data-id="${a.appointment_id}" data-action="view">View</button>`;
        const doneBtn = `<button class="btn btn-sm btn-outline-success" data-id="${a.appointment_id}" data-action="done">Done</button>`;
        const cancelBtn = `<button class="btn btn-sm btn-outline-danger" data-id="${a.appointment_id}" data-action="cancel">Cancel</button>`;

        let actionButtons = viewBtn;

        // Chỉ hiển thị "Hoàn tất" và "Huỷ" nếu trạng thái không phải Cancelled hoặc Completed
        if (a.status !== "Cancelled" && a.status !== "Completed") {
            actionButtons += doneBtn + cancelBtn;
        }

        const row = `
        <tr>
            <td>${start + index + 1}</td>
            <td>${a.patient_id}</td>
            <td>${a.appointment_datetime}</td>
            <td>${a.shift}</td>
            <td>${a.status}</td>
            <td>${a.note || ''}</td>
            <td>${actionButtons}</td>
        </tr>
    `;
        tableBody.insertAdjacentHTML('beforeend', row);
    });

    if (info) {
        const formattedStart = String(start + 1).padStart(2, '0');
        const formattedEnd = String(Math.min(end, allAppointments.length)).padStart(2, '0');
        info.innerText = `Showing ${formattedStart} to ${formattedEnd} of ${allAppointments.length} entries`;
    }
}

async function countAppointmentsToday(action) {
    try {
        const response = await fetch('/api/doctor/appointment', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({action: action})
        });

        if (!response.ok) {
            throw new Error('Failed to fetch appointments');
        }

        const appointments = await response.json();
        return appointments;
    } catch (error) {
        console.error('Error counting appointments:', error);
        return 0;
    }
}

function handleView(id) {
    console.log("View appointment", id);
    // Ví dụ: mở offcanvas hoặc modal
    const canvas = new bootstrap.Offcanvas('#offcanvasAppointmentEdit');
    canvas.show();

    // fetch(`/api/appointment/${id}`)
    //     .then(res => res.json())
    //     .then(data => {
    //         // Gán data vào form
    //         document.getElementById("appointmentTitle").innerText = data.title;
    //         // ... các phần khác
    //     });
}

function handleDone(id) {
    console.log("Mark as done", id);

    // fetch(`/api/appointment/done/${id}`, { method: "POST" })
    //     .then(res => {
    //         if (res.ok) alert("Marked as done!");
    //     });
}

function handleCancel(id) {
    console.log("Cancel appointment", id);
    // if (!confirm("Are you sure you want to cancel this appointment?")) return;
    //
    // fetch(`/api/appointment/cancel/${id}`, { method: "POST" })
    //     .then(res => {
    //         if (res.ok) alert("Cancelled successfully!");
    //     });
}


document.addEventListener('DOMContentLoaded', async () => {
    fetchAppointmentsByAction('Upcoming');

    const NOA = document.getElementById("countappointmenttoday");
    if (NOA) {
        try {
            const noa = await countAppointmentsToday("Count");
            NOA.innerText = `${noa} appointments today`;
        } catch (err) {
            NOA.innerText = `Error loading count`;
        }
    }

    document.getElementById("appointmentTableBody").addEventListener("click", function (e) {
        const target = e.target.closest("button");

        if (!target) return;

        const action = target.getAttribute("data-action");
        const appointmentId = target.getAttribute("data-id");

        switch (action) {
            case "view":
                handleView(appointmentId);
                break;
            case "done":
                handleDone(appointmentId);
                break;
            case "cancel":
                handleCancel(appointmentId);
                break;
        }
    });

    document.getElementById("appointmentUpcoming")?.addEventListener("click", (e) => {
        e.preventDefault();
        fetchAppointmentsByAction('Upcoming');
    });

    document.getElementById("appointmentComplete")?.addEventListener("click", (e) => {
        e.preventDefault();
        fetchAppointmentsByAction('Completed');
    });

    document.getElementById("appointmentClosed")?.addEventListener("click", (e) => {
        e.preventDefault();
        fetchAppointmentsByAction('Cancelled');
    });

    document.getElementById("countappointmenttoday")?.addEventListener("click", async (e) => {
        e.preventDefault();
        const NOA = document.getElementById("countappointmenttoday");
        const noa = await countAppointmentsToday("Count");
        NOA.innerText = `${noa} appointments today`;
    });

    document.querySelector(".card-navigation a:nth-child(1)")?.addEventListener("click", (e) => {
        e.preventDefault();
        if (currentPage > 1) {
            currentPage--;
            paginateAppointments();
        }
    });

    document.querySelector(".card-navigation a:nth-child(2)")?.addEventListener("click", (e) => {
        e.preventDefault();
        const totalPages = Math.ceil(allAppointments.length / pageSize);
        if (currentPage < totalPages) {
            currentPage++;
            paginateAppointments();
        }
    });
});


    function toggleSchedule() {
    const container = document.getElementById("scheduleContainer");
    container.classList.toggle("d-none");
}
