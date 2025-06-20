package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordSummaryDTO {
    private int recordId;
    private String doctorName;
    private String clinicName; // = department
    private String reason;
    private String note;
}
