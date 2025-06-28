package dto;

public class DoctorDTOFA {
    private int accountStaffId;
    private String username;
    private String role;
    private String email;
    private String img;
    private String status;
    private int doctorId;
    private String fullName;
    private String department;
    private String phone;
    private String eduLevel;

    public DoctorDTOFA() {
    }

    public DoctorDTOFA(int accountStaffId, String username, String role, String email, String img,
                     String status, int doctorId, String fullName, String department, String phone, String eduLevel) {
        this.accountStaffId = accountStaffId;
        this.username = username;
        this.role = role;
        this.email = email;
        this.img = img;
        this.status = status;
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.department = department;
        this.phone = phone;
        this.eduLevel = eduLevel;
    }

    public int getAccountStaffId() {
        return accountStaffId;
    }

    public void setAccountStaffId(int accountStaffId) {
        this.accountStaffId = accountStaffId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }
}