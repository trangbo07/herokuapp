package dto;

public class ServiceOrderItemDTO {
    private int service_order_item_id;
    private int service_order_id;
    private int service_id;
    private int doctor_id;
    private String service_name;
    private String service_description;
    private double service_price;
    private String doctor_name;

    public ServiceOrderItemDTO() {
    }

    public ServiceOrderItemDTO(int service_order_item_id, int service_order_id, int service_id, 
                              int doctor_id, String service_name, String service_description, 
                              double service_price, String doctor_name) {
        this.service_order_item_id = service_order_item_id;
        this.service_order_id = service_order_id;
        this.service_id = service_id;
        this.doctor_id = doctor_id;
        this.service_name = service_name;
        this.service_description = service_description;
        this.service_price = service_price;
        this.doctor_name = doctor_name;
    }

    // Getters and Setters
    public int getService_order_item_id() {
        return service_order_item_id;
    }

    public void setService_order_item_id(int service_order_item_id) {
        this.service_order_item_id = service_order_item_id;
    }

    public int getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(int service_order_id) {
        this.service_order_id = service_order_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public double getService_price() {
        return service_price;
    }

    public void setService_price(double service_price) {
        this.service_price = service_price;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }
} 