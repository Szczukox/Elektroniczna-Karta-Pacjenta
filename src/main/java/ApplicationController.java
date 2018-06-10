import ca.uhn.fhir.rest.client.api.IGenericClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.mapping.Array;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    @FXML
    TableView<PatientModel> patientsTableView;

    @FXML
    TableColumn<PatientModel, String> patientIdTableColumn;
    @FXML
    TableColumn<PatientModel, String> patientFirstNameTableColumn;
    @FXML
    TableColumn<PatientModel, String> patientLastNameTableColumn;

    public void initialize(URL location, ResourceBundle resources) {
        FhirClient fhirClient = new FhirClient();
        IGenericClient client = fhirClient.getClient();
        Bundle result = client.search().forResource(Patient.class).returnBundle(org.hl7.fhir.dstu3.model.Bundle.class).elementsSubset("identifier", "name").execute();
        ArrayList<Patient> patients = new ArrayList<Patient>();
        while (result.getLink(Bundle.LINK_NEXT) != null) {
            for (Bundle.BundleEntryComponent patient : result.getEntry()) {
                patients.add((Patient) patient.getResource());
            }
            result = client.loadPage().next(result).execute();
        }

        ObservableList<PatientModel> patientModels = FXCollections.observableArrayList();
        for (Patient patient : patients) {
            String patientId = patient.getId().split("/")[5];
            String patientFirstName = patient.getNameFirstRep().getGivenAsSingleString();
            String patientLastName = patient.getNameFirstRep().getFamily();
            patientModels.add(new PatientModel(patientId, patientFirstName, patientLastName));
        }

        patientIdTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("id"));
        patientFirstNameTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("firstName"));
        patientLastNameTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("lastName"));

        patientsTableView.setItems(patientModels);
    }

}
