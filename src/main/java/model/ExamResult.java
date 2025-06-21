package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExamResult {
    private int exam_result_id;
    private int medicineRecord_id;
    private int doctor_id;
    private String preliminary_diagnosis;
    private String symptoms;
}
