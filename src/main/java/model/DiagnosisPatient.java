package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DiagnosisPatient {
    private String fullName;
    private String dob;
    private String gender;
    private String disease;
    private String conclusion;
    private String treatmentPlan;
}
