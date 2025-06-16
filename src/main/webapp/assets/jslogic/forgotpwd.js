document.getElementById('sendOtpBtn').addEventListener('click', function (e) {
    e.preventDefault();
    submitForgotPassword();
});

document.querySelector('#otpForm button[type="submit"]').addEventListener('click', function (e) {
    e.preventDefault();
    submitOTP();
});

async function submitForgotPassword() {
    const email = document.getElementById('email').value;
    const sendOtpBtn = document.getElementById('sendOtpBtn');
    const otpLoading = document.getElementById('otpLoading');
    const errorMessageEmail = document.getElementById('errorMessageEmail');
    errorMessageEmail.innerText = "";
    console.log(email);

    sendOtpBtn.disabled = true;
    otpLoading.style.display = 'block';

    try {
        const response = await fetch("/reset", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                action: 'sendOTP',
                email: email
            })
        });

        const result = await response.json();
        if (result.success) {
            document.getElementById('emailForm').style.display = 'none';
            document.getElementById('otpForm').style.display = 'block';
        } else {
            errorMessageEmail.innerText = result.message;
        }
    } catch (error) {
        errorMessageEmail.innerText = 'An error occurred. Please try again.';
    } finally {
        sendOtpBtn.disabled = false;
        otpLoading.style.display = 'none';
    }
}

async function submitOTP() {
    const otp = document.getElementById('otp').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorMessageOTP = document.getElementById('errorMessageOTP');
    errorMessageOTP.innerText = "";

    if (newPassword !== confirmPassword) {
        errorMessageOTP.innerText = 'Passwords do not match';
        return;
    }

    try {
        const response = await fetch("/reset", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                action: 'resetPassword',
                otp: otp,
                newPassword: newPassword
            })
        });

        const result = await response.json();
        if (result.success) {
            window.location.href = 'login.html';
        } else {
            errorMessageOTP.innerText = result.message;
        }
    } catch (error) {
        errorMessageOTP.innerText = 'An error occurred. Please try again.';
    }
}
