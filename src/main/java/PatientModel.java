import javafx.beans.property.SimpleStringProperty;

public class PatientModel {
    private SimpleStringProperty id;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty birthDate;
    private SimpleStringProperty phoneNumber;
    private SimpleStringProperty address;
    private SimpleStringProperty language;

    public PatientModel(String id, String firstName, String lastName, String birtDate, String phoneNumber, String address, String language) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.birthDate = new SimpleStringProperty(birtDate);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.language = new SimpleStringProperty(language);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate.get();
    }

    public SimpleStringProperty birthDateProperty() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public String getLanguage() {
        return language.get();
    }

    public SimpleStringProperty languageProperty() {
        return language;
    }
}
