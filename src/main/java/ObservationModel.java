import javafx.beans.property.SimpleStringProperty;

public class ObservationModel {
    private SimpleStringProperty date;
    private SimpleStringProperty status;
    private SimpleStringProperty category;
    private SimpleStringProperty code;
    private SimpleStringProperty value;
    private SimpleStringProperty unit;

    public ObservationModel(String date, String status, String category, String code, String value, String unit) {
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
        this.category = new SimpleStringProperty(category);
        this.code = new SimpleStringProperty(code);
        this.value = new SimpleStringProperty(value);
        this.unit = new SimpleStringProperty(unit);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getCategory() {
        return category.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public String getCode() {
        return code.get();
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }
}
