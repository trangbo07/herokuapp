document.getElementById("logoutBtn").addEventListener("click", async (e) => {
    e.preventDefault(); // Ngăn chuyển trang mặc định

    try {
        const res = await fetch(`/logout`, {
            method: "GET", // hoặc GET nếu servlet bạn xử lý GET
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (res.ok) {
            const result = await res.json();
            if (result.success) {
                window.location.href = result.redirectUrl;
            } else {
                alert("Logout failed: " + result.message);
            }
        } else {
            alert("Logout request failed with status " + res.status);
        }
    } catch (err) {
        console.error("Error:", err);
        alert("System error occurred during logout.");
    }
});
