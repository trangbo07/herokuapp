package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Nurse {
    private int nurse_id, account_staff_id;
    private String full_name, department, phone, eduLevel;
}
