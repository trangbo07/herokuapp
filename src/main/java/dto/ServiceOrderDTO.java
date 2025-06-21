package dto;

import java.util.List;

public class ServiceOrderDTO {
    private int service_order_id;
    private int doctor_id;
    private int medicineRecord_id;
    private String order_date;
    private String doctor_name;
    private String patient_name;
    private List<ServiceOrderItemDTO> items;
    private double total_amount;

    public ServiceOrderDTO() {
    }

    public ServiceOrderDTO(int service_order_id, int doctor_id, int medicineRecord_id, 
                          String order_date, String doctor_name, String patient_name) {
        this.service_order_id = service_order_id;
        this.doctor_id = doctor_id;
        this.medicineRecord_id = medicineRecord_id;
        this.order_date = order_date;
        this.doctor_name = doctor_name;
        this.patient_name = patient_name;
    }

    // Getters and Setters
    public int getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(int service_order_id) {
        this.service_order_id = service_order_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getMedicineRecord_id() {
        return medicineRecord_id;
    }

    public void setMedicineRecord_id(int medicineRecord_id) {
        this.medicineRecord_id = medicineRecord_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public List<ServiceOrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ServiceOrderItemDTO> items) {
        this.items = items;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }
} 