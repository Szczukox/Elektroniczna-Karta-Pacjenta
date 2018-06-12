import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientCardController implements Initializable {

    @FXML
    Label patientFullNameLabel;
    @FXML
    Label patientIdLabel;
    @FXML
    Label patientBirthDateLabel;
    @FXML
    Label patientPhoneNumberLabel;
    @FXML
    Label patientAddressLabel;
    @FXML
    Label patientLanguageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setPatient(PatientModel patientModel) {
        patientFullNameLabel.setText(patientModel.getFirstName() + " " + patientModel.getLastName());
        patientIdLabel.setText(patientModel.getId());
        patientBirthDateLabel.setText(patientModel.getBirthDate());
        patientPhoneNumberLabel.setText(patientModel.getPhoneNumber());
        patientAddressLabel.setText(patientModel.getAddress());
        patientLanguageLabel.setText(patientModel.getLanguage());
    }
}
