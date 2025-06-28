package dto;

import java.util.List;

public class DistinctResponse {
    private String field;
    private List<String> values;

    public DistinctResponse(String field, List<String> values) {
        this.field = field;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public List<String> getValues() {
        return values;
    }
}
