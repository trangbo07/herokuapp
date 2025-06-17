package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Receptionist {
    private int receptionist_id, account_staff_id;
    private String full_name, phone;
}
