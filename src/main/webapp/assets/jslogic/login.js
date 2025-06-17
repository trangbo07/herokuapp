const form = document.getElementById("loginForm");
const errorMessage = document.getElementById("errorMessage");

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorMessage.textContent = "";

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const rememberme = document.getElementById("rememberme").checked;
    const contextPath = window.location.pathname.split('/')[1];
    const data = {username, password, rememberme};

    try {
        const res = await fetch("/api/login", {
            method: "POST", headers: {
                "Content-Type": "application/json"
            }, body: JSON.stringify(data)
        });

        if (!res.ok) {
            throw new Error(`Server error: ${res.status}`);
        }

        const result = await res.json();

        if (result.success) {
            window.location.href = result.redirectUrl;
        } else {
            errorMessage.textContent = result.message || "Login failed.";
        }

    } catch (err) {
        errorMessage.textContent = "System error occurred.";
    }
});