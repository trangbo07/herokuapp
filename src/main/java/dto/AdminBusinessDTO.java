package dto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AdminBusinessDTO {
    private int accountStaffId;
    private String username;
    private String password;
    private String email;
    private String role;
    private String img;
    private String status;

    private int adminId;
    private String fullName;
    private String department;
    private String phone;
}
