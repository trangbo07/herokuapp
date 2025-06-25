package dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PaymentAnalyticsDTO {
    private double totalRevenue;
    private int totalPayments;
    private int completedPayments;
    private int pendingPayments;
    private Map<String, Double> revenueByPaymentMethod;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<TopCustomerDTO> topCustomers;
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class MonthlyRevenueDTO {
        private int month;
        private double totalAmount;
        private int transactionCount;
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class TopCustomerDTO {
        private String customerName;
        private double totalPaid;
        private int paymentCount;
    }
} 