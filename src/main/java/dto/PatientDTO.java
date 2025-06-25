package dto;

public class PatientDTO {
    private String fullName;
    private String phone;
    private String username;
    private String email;
    private String img;


    public PatientDTO() {}

    public PatientDTO(String fullName, String phone,
                            String username, String email, String img) {
        this.fullName = fullName;
        this.phone = phone;
        this.username = username;
        this.email = email;
        this.img = img;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
