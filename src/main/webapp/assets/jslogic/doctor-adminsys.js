let allDoctors = [];
let currentPage = 1;
let pageSize = 5;
let selectedDoctorIdForReset = null;

document.addEventListener("DOMContentLoaded", () => {
    loadSelectFilter('status', 'filterStatus');
    loadSelectFilter('eduLevel', 'filterEduLevel');
    loadSelectFilter('department', 'filterDepartment');
    fetchDoctorsWithFilter();

    document.getElementById('filterStatus').addEventListener('change', fetchDoctorsWithFilter);
    document.getElementById('filterEduLevel').addEventListener('change', fetchDoctorsWithFilter);
    document.getElementById('filterDepartment').addEventListener('change', fetchDoctorsWithFilter);
    document.getElementById('searchInput').addEventListener('input', debounce(fetchDoctorsWithFilter, 400));
    document.getElementById('btnPreviousPage').addEventListener('click', e => { e.preventDefault(); changePage(-1); });
    document.getElementById('btnNextPage').addEventListener('click', e => { e.preventDefault(); changePage(1); });
});

document.getElementById('btnAddDoctor').addEventListener('click', () => {
    openDoctorForm('add');
});

document.getElementById('cancelDoctorForm').addEventListener('click', () => {
    document.getElementById('doctorFormCard').style.display = 'none';
    document.getElementById("doctorSelectionCard").style.display = 'block';
});

document.getElementById('doctorForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const formData = new FormData(this);
    const doctorData = Object.fromEntries(formData.entries());

    const isEdit = !!doctorData.doctorId;
    const url = isEdit ? '/api/admin/doctors?action=update' : '/api/admin/doctors?action=add';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctorData),
            credentials: 'include'
        });

        const result = await response.json();

        if (result.success) {
            alert(result.message || (isEdit ? 'Updated successfully!' : 'Added successfully!'));
            document.getElementById('doctorFormCard').style.display = 'none';
            fetchDoctorsWithFilter();
        } else {
            alert(result.message || 'Failed to save doctor.');
        }
    } catch (err) {
        console.error('Error saving doctor:', err);
        alert('Error occurred while saving doctor.');
    }
});

async function fetchDoctorsWithFilter() {
    const status = document.getElementById('filterStatus')?.value || '';
    const eduLevel = document.getElementById('filterEduLevel')?.value || '';
    const department = document.getElementById('filterDepartment')?.value || '';
    const search = document.getElementById('searchInput')?.value || '';
    const tableBody = document.getElementById('waitListTableBody');
    const info = document.getElementById('paginationInfo');

    tableBody.innerHTML = '<tr><td colspan="10">Loading...</td></tr>';

    const params = new URLSearchParams({ action: 'filter' });
    if (status) params.append("status", status);
    if (eduLevel) params.append("eduLevel", eduLevel);
    if (department) params.append("department", department);
    if (search) params.append("search", search);

    try {
        const response = await fetch('/api/admin/doctors?' + params.toString(), { method: 'GET', credentials: 'include' });
        const doctors = await response.json();

        if (!Array.isArray(doctors) || doctors.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="10">No doctors found.</td></tr>';
            if (info) info.innerText = `Showing 0 to 0 of 0 entries`;
            return;
        }

        allDoctors = doctors;
        currentPage = 1;
        paginateDoctors();

    } catch (error) {
        console.error('Error fetching doctors:', error);
        tableBody.innerHTML = '<tr><td colspan="10">Failed to load doctor data.</td></tr>';
    }
}

function paginateDoctors() {
    const tableBody = document.getElementById('waitListTableBody');
    const info = document.getElementById('paginationInfo');
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pageData = allDoctors.slice(start, end);

    if (!pageData.length) {
        tableBody.innerHTML = '<tr><td colspan="10">No doctors available.</td></tr>';
        info.innerText = `Showing 0 to 0 of ${allDoctors.length} entries`;
        return;
    }

    tableBody.innerHTML = pageData.map((d, index) => `
        <tr>
            <td>${start + index + 1}</td>
            <td>${d.fullName}</td>
            <td>${d.username}</td>
            <td>${d.email}</td>
            <td>${d.status}</td>
            <td>${d.phone}</td>
            <td>${d.department}</td>
            <td>${d.eduLevel}</td>
            <td><img src="${d.img}" style="width: 40px; height: 40px; object-fit: cover;"></td>
            <td>
                <button class="btn btn-sm btn-info me-1"
                            data-bs-toggle="offcanvas"
                            data-bs-target="#doctorViewCanvas"
                            onclick="viewDoctor(${d.doctorId})">View</button>
                <button class="btn btn-sm btn-warning me-1" onclick="editDoctor(${d.doctorId})">Edit</button>
                <button class="btn btn-sm ${d.status === 'Enable' ? 'btn-danger' : 'btn-success'}"
                        onclick="toggleDoctorStatus(${d.doctorId}, '${d.status}')">
                    ${d.status === 'Enable' ? 'Disable' : 'Enable'}
                </button>
            </td>
        </tr>
    `).join('');

    const formattedStart = String(start + 1).padStart(2, '0');
    const formattedEnd = String(Math.min(end, allDoctors.length)).padStart(2, '0');
    info.innerText = `Showing ${formattedStart} to ${formattedEnd} of ${allDoctors.length} entries`;
}

document.getElementById('selectPageSize').addEventListener('change', (e) => {
    pageSize = parseInt(e.target.value);
    currentPage = 1;
    paginateDoctors();
});

function changePage(direction) {
    const newPage = currentPage + direction;
    const maxPage = Math.ceil(allDoctors.length / pageSize);
    if (newPage >= 1 && newPage <= maxPage) {
        currentPage = newPage;
        paginateDoctors();
    }
}

function loadSelectFilter(field, selectId) {
    fetch(`/api/admin/doctors?action=distinct&field=${field}`)
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById(selectId);
            if (!select) return;
            select.innerHTML = `<option value="">All ${capitalize(field)}</option>`;
            (data?.values || []).forEach(val => {
                select.innerHTML += `<option value="${val}">${val}</option>`;
            });
        })
        .catch(err => console.error(`Error loading ${field}:`, err));
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

function debounce(func, delay) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

function editDoctor(doctorId) {
    const doctor = allDoctors.find(d => d.doctorId === doctorId);
    if (doctor) {
        openDoctorForm('edit', doctor);
    }
}

function openDoctorForm(mode, doctor = null) {
    const formCard = document.getElementById('doctorFormCard');
    const selectionCard = document.getElementById('doctorSelectionCard');
    const form = document.getElementById('doctorForm');
    const title = document.getElementById('doctorFormTitle');
    formCard.style.display = 'block';
    selectionCard.style.display = 'none';
    form.reset();

    if (mode === 'edit' && doctor) {
        title.innerHTML = '<i class="fas fa-edit me-2"></i>Edit Doctor';

        // Chờ load xong trước khi gán selected
        loadSelectForForm('department', 'department');
        loadSelectForForm('eduLevel', 'eduLevel');

        document.getElementById('doctorId').value = doctor.doctorId;
        document.getElementById('fullName').value = doctor.fullName;
        document.getElementById('username').value = doctor.username;
        document.getElementById('email').value = doctor.email;
        document.getElementById('phone').value = doctor.phone;
        document.getElementById('department').value = doctor.department;
        document.getElementById('eduLevel').value = doctor.eduLevel;
        document.getElementById('status').value = doctor.status;
        document.getElementById('img').value = '';
        document.getElementById('previewImg').classList.add('d-none');
    } else {
        loadSelectForForm('department', 'department'),
        loadSelectForForm('eduLevel', 'eduLevel')
        title.innerHTML = '<i class="fas fa-plus me-2"></i>Add Doctor';
        document.getElementById('doctorId').value = '';
    }
}

function viewDoctor(doctorId) {
    const d = allDoctors.find(doc => doc.doctorId === doctorId);
    if (!d) return;

    selectedDoctorIdForReset = doctorId; // lưu để dùng cho reset

    document.getElementById('viewDoctorId').textContent = d.doctorId;
    document.getElementById('viewAccountStaffId').textContent = d.accountStaffId;
    document.getElementById('viewUsername').textContent = d.username;
    document.getElementById('viewEmail').textContent = d.email;
    document.getElementById('viewFullName').textContent = d.fullName;
    document.getElementById('viewDepartment').textContent = d.department;
    document.getElementById('viewPhone').textContent = d.phone;
    document.getElementById('viewEduLevel').textContent = d.eduLevel;
    document.getElementById('viewStatus').textContent = d.status;
    document.getElementById('viewRole').textContent = d.role;
    document.getElementById('viewImg').src = d.img || '';
}

async function loadSelectForForm(field, selectId) {
    try {
        const response = await fetch(`/api/admin/doctors?action=distinct&field=${field}`);
        const data = await response.json();
        const select = document.getElementById(selectId);

        if (select) {
            select.innerHTML = ''; // Không thêm option "All ..."
            (data?.values || []).forEach(val => {
                select.innerHTML += `<option value="${val}">${val}</option>`;
            });
        }
    } catch (error) {
        console.error(`Error loading options for ${field}:`, error);
    }
}





