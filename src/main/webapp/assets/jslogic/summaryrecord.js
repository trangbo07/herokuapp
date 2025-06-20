document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const patientId = urlParams.get("patientId");
    console.log(patientId);
    try {
        const res = await fetch(`/api/records/summary?patientId=${patientId}`);
        if (!res.ok) throw new Error("Server error");

        const records = await res.json();
        const tableBody = document.getElementById("recordTableBody");
        tableBody.innerHTML = "";

        if (records.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">Không có hồ sơ nào.</td></tr>`;
            return;
        }

        records.forEach((r, index) => {
            const imageUrl = r.avatarUrl || "../assets/assets/images/dashboard/default.png";
            const row = `
        <tr data-item="list">
          <th scope="row">${index + 1}</th>
          <td>
            <div class="d-flex align-items-center gap-3">
              <h5 class="mb-0">${r.doctorName}</h5>
            </div>
          </td>
          <td>${r.clinicName}</td>
          <td>${r.reason}</td>
          <td>${r.note || "-------"}</td>
          <td>
            <a href="diagnosis.html?recordId=${r.recordId}" class="d-inline-block pe-2" title="Xem chuẩn đoán">
              <span class="text-info">
                <i class="bi bi-clipboard2-heart"></i>
              </span>
            </a>
            <a href="prescription.html?recordId=${r.recordId}" class="d-inline-block pe-2" title="Xem đơn thuốc">
              <span class="text-success">
                <i class="bi bi-capsule"></i>
              </span>
            </a>
            <a href="invoice.html?recordId=${r.recordId}" class="d-inline-block pe-2" title="Xem hóa đơn">
              <span class="text-warning">
                <i class="bi bi-receipt"></i>
              </span>
            </a>
             <a href="examination.html?recordId=${r.recordId}" class="d-inline-block pe-2" title="Xem kết quả khám">
              <span class="text-primary">
                <i class="bi bi-activity"></i>
              </span>
            </a>
            <a href="service.html?recordId=${r.recordId}" class="d-inline-block pe-2" title="Xem dịch vụ cận lâm sàng">
              <span class="text-secondary">
                <i class="bi bi-gear"></i>
              </span>
            </a>
          </td>
          </td>
        </tr>
      `;
            tableBody.insertAdjacentHTML("beforeend", row);
        });

    } catch (err) {
        console.error("Không thể tải hồ sơ bệnh án:", err);
        document.getElementById("recordTableBody").innerHTML =
            `<tr><td colspan="6" class="text-danger">Không thể tải dữ liệu.</td></tr>`;
    }
});