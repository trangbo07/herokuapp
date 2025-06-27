package dto;

public class AppointmentpatientDTO {
    private int patientId;
    private int appointmentId;
    private String doctorName;
    private String dateTime;      // ← đổi thành String
    private String shift;
    private String eduLevel;
    private String status;
    private String note;
    
    // Thêm thông tin patient
    private String patientName;
    private String dob;
    private String gender;
    private String phone;

    public AppointmentpatientDTO() {}

    public AppointmentpatientDTO(int appointmentId, String doctorName, String shift, String dateTime, String eduLevel, String status, String note) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.shift = shift;
        this.dateTime = dateTime;
        this.eduLevel = eduLevel;
        this.status = status;
        this.note = note;
    }

    public AppointmentpatientDTO(int patientId, int appointmentId, String doctorName,
                                 String dateTime, String shift, String eduLevel,
                                 String status, String note) {
        this.patientId     = patientId;
        this.appointmentId = appointmentId;
        this.doctorName    = doctorName;
        this.dateTime      = dateTime;
        this.shift         = shift;
        this.eduLevel      = eduLevel;
        this.status        = status;
        this.note          = note;
    }

    // getters & setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getEduLevel() { return eduLevel; }
    public void setEduLevel(String eduLevel) { this.eduLevel = eduLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    // Thêm getters & setters cho thông tin patient
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
