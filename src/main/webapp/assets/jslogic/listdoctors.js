document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("doctorList");

    try {
        const res = await fetch("/api/doctors");
        const doctors = await res.json();

        let html = "";
        doctors.forEach(d => {
            html += `
            <div class="col-xl-3 col-lg-4 col-sm-6">
                <div class="p-5 card text-center">
                    <div class="mt-5">
                        <img src="${d.avatarUrl}" alt="doctor-img"
                             class="img-fluid rounded-circle p-1 border border-danger avatar"
                             height="100" width="100" loading="lazy">
                    </div>
                    <div class="mt-5 d-inline-block bg-primary-subtle px-3 py-2 rounded-pill">
                        <span class="fw-500">1000+ Appointment Completed</span>
                    </div>
                    <h3 class="mt-4 mb-2">${d.fullName}</h3>
                    <h6 class="text-body fw-normal">${d.department}</h6>
                    <div class="d-flex gap-3 justify-content-center mt-5">
                        <a class="btn btn-primary" href="patient-appointment.html">Book Appointment</a>
                        <a class="btn btn-secondary" href="app/user-profile.html?id=${d.doctorId}">View Profile</a>
                    </div>
                </div>
            </div>`;
        });

        container.innerHTML = html;

    } catch (err) {
        container.innerHTML = `<div class="text-danger">Không thể tải danh sách bác sĩ.</div>`;
        console.error(err);
    }
});