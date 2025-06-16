const form = document.getElementById("registrationForm");
const errorMessage = document.getElementById("errorMessage");

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorMessage.style.display = "none";
    errorMessage.textContent = "";
    const contextPath = window.location.pathname.split('/')[1];
    const username = form.username.value.trim();
    const email = form.email.value.trim();
    const password = form.password.value;
    const re_password = form.re_password.value;

    if (password !== re_password) {
        errorMessage.textContent = "Password not match !";
        errorMessage.style.display = "block";
        return;
    }


    const data = {username, email, password, re_password};

    try {
        const res = await fetch(`/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(`Server responded with status ${res.status}: ${text}`);
        }

        const result = await res.json();

        if (result.success) {
            window.location.href = result.redirectUrl ;
        } else {
            errorMessage.textContent = result.message || "Login fail .";
            errorMessage.style.display = "block";
        }
    } catch (err) {
        console.error("Error request:", err);
        errorMessage.textContent = "error with sign up. Try again.";
        errorMessage.style.display = "block";
    }
});