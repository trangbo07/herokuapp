document.addEventListener('DOMContentLoaded', async function () {
    const loadingSpinner = document.getElementById("loadingSpinner");
    const tableContainer = document.getElementById("tableContainer");
    const errorMessage = document.getElementById("errorMessage");

    try {
        const response = await fetch("/api/doctor/diagnosis", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("Failed to fetch diagnosis records");
        }

        const data = await response.json();

        // Hide loading spinner
        loadingSpinner.style.display = "none";

        if (!data || data.length === 0) {
            errorMessage.classList.remove("d-none");
            errorMessage.innerHTML = '<i class="fas fa-info-circle me-2"></i>No diagnosis records found.';
            return;
        }

        // Show table container
        tableContainer.style.display = "block";

        // Update stats
        updateStats(data);

        // Render table
        const tableBody = document.getElementById("diagnosisTableBody");
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
                // Remove active class from all rows
                document.querySelectorAll('.diagnosis-row').forEach(r => r.classList.remove('active'));
                // Add active class to clicked row
                row.classList.add('active');
                renderDiagnosisDetail(record);
            });

            tableBody.appendChild(row);

            // Load first record by default
            if (index === 0) {
                row.classList.add('active');
                renderDiagnosisDetail(record);
            }
        });

    } catch (error) {
        loadingSpinner.style.display = "none";
        errorMessage.classList.remove("d-none");
        errorMessage.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Failed to load diagnosis records.';
        console.error("Error fetching diagnosis data:", error);
    }
});

function updateStats(data) {
    const uniquePatients = new Set(data.map(record => record.fullName)).size;
    document.getElementById("totalPatients").textContent = uniquePatients;
    document.getElementById("totalDiagnosis").textContent = data.length;
}

function renderDiagnosisDetail(record) {
    document.getElementById("detailPatientName").textContent = record.fullName || "---";
    document.getElementById("detailDOB").textContent = record.dob ? new Date(record.dob).toLocaleDateString('en-GB') : "---";
    document.getElementById("detailGender").textContent = record.gender || "---";
    document.getElementById("detailDisease").textContent = record.disease || "---";
    document.getElementById("detailConclusion").textContent = record.conclusion || "---";
    document.getElementById("detailTreatmentPlan").textContent = record.treatmentPlan || "---";
}