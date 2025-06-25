package model;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Payment {
    private int payment_id;
    private double amount;
    private String payment_type; // 'Service' hoặc 'Prescription'
    private int invoice_id;
    private LocalDateTime payment_date;
    private String status; // 'Pending', 'Paid', 'Cancelled'
}
