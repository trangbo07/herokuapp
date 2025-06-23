document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const patientId = params.get("patientId");

    if (!patientId) {
        document.getElementById("diagnosisTableBody").innerHTML =
            "<tr><td colspan='6'>Không tìm thấy bệnh nhân.</td></tr>";
        return;
    }

    try {
        const res = await fetch(`/DiagnosisServlet?patientId=${patientId}`);
        if (!res.ok) throw new Error("Lỗi khi gọi servlet");

        const data = await res.json();
        const tbody = document.getElementById("diagnosisTableBody");
        tbody.innerHTML = "";

        // 👉 Ẩn spinner, hiện bảng
        document.getElementById("loadingSpinner").style.display = "none";
        document.getElementById("tableContainer").style.display = "block";

        if (data.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6'>Không có dữ liệu.</td></tr>";
            return;
        }
        document.getElementById("totalDiagnosis").textContent = data.length;
        data.forEach((record, index) => {
            const row = document.createElement("tr");
            row.className = "diagnosis-row";
            row.innerHTML = `
                <td><i class="fas fa-user me-2"></i>${record.fullName || 'N/A'}</td>
                <td><i class="fas fa-calendar me-2"></i>${record.dob ? new Date(record.dob).toLocaleDateString('en-GB') : 'N/A'}</td>
                <td><i class="fas fa-venus-mars me-2"></i>${record.gender || 'N/A'}</td>
                <td><i class="fas fa-disease me-2"></i>${record.disease || 'N/A'}</td>
                <td><i class="fas fa-stethoscope me-2"></i>${record.conclusion || 'N/A'}</td>
                <td><i class="fas fa-pills me-2"></i>${record.treatmentPlan || 'N/A'}</td>
            `;

            row.addEventListener("click", () => {
                document.querySelectorAll('.diagnosis-row').forEach(r => r.classList.remove('active'));
                row.classList.add('active');
                renderDiagnosisDetail(record);
            });

            tbody.appendChild(row);

            if (index === 0) {
                row.classList.add('active');
                renderDiagnosisDetail(record);
            }
        });

    } catch (err) {
        console.error("Lỗi:", err);
        document.getElementById("loadingSpinner").style.display = "none";
        document.getElementById("errorMessage").classList.remove("d-none");
    }
});

function renderDiagnosisDetail(record) {
    document.getElementById("detailPatientName").textContent = record.fullName || "---";
    document.getElementById("detailDOB").textContent = record.dob ? new Date(record.dob).toLocaleDateString('en-GB') : "---";
    document.getElementById("detailGender").textContent = record.gender || "---";
    document.getElementById("detailDisease").textContent = record.disease || "---";
    document.getElementById("detailConclusion").textContent = record.conclusion || "---";
    document.getElementById("detailTreatmentPlan").textContent = record.treatmentPlan || "---";
}