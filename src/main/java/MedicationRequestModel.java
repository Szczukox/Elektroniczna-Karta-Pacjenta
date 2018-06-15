import javafx.beans.property.SimpleStringProperty;

public class MedicationRequestModel {
    private SimpleStringProperty date;
    private SimpleStringProperty status;
    private SimpleStringProperty intent;
    private SimpleStringProperty medication;

    public MedicationRequestModel(String date, String status, String intent, String medication) {
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
        this.intent = new SimpleStringProperty(intent);
        this.medication = new SimpleStringProperty(medication);
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

    public String getIntent() {
        return intent.get();
    }

    public SimpleStringProperty intentProperty() {
        return intent;
    }

    public String getMedication() {
        return medication.get();
    }

    public SimpleStringProperty medicationProperty() {
        return medication;
    }
}
