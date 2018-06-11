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
    Label patientPhoneNumberLabel;
    @FXML
    Label patientAddressLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setPatient(PatientModel patientModel) {
        patientFullNameLabel.setText(patientModel.getFirstName() + " " + patientModel.getLastName());
        patientIdLabel.setText(patientModel.getId());
        patientPhoneNumberLabel.setText(patientModel.getPhoneNumber());
        patientAddressLabel.setText(patientModel.getAddress());
    }
}
