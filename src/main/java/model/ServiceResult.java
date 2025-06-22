package model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceResult {
    private int result_id;
    private int service_order_item_id;
    private String result_description;
    private String created_at;
} 