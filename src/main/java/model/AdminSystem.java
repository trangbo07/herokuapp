package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AdminSystem {
    private int admin_id, account_staff_id;
    private String full_name, department, phone;
}