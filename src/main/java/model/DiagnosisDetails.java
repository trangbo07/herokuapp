package model;

import lombok.*;
import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DiagnosisDetails {
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String disease;
    private String conclusion;
    private String treatmentPlan;
}
