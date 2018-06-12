import ca.uhn.fhir.rest.client.api.IGenericClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
    @FXML
    TableColumn<PatientModel, String> patientPhoneNumberTableColumn;

    @FXML
    TextField filterLastNameTextField;

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
            patient = client.read().resource(Patient.class).withId(patientId).execute();
            //System.out.println(patient.getPhotoFirstRep().getUrl());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String patientBirthDate = simpleDateFormat.format(patient.getBirthDate());
            String patientPhoneNumber = patient.getTelecomFirstRep().getValue();
            String patientAddress = patient.getAddressFirstRep().getLine().get(0).getValue() + ", " +
                    patient.getAddressFirstRep().getCity() + ", " +
                    patient.getAddressFirstRep().getState() + " " + patient.getAddressFirstRep().getPostalCode() + ", " +
                    patient.getAddressFirstRep().getCountry();
            String patientLanguage = patient.getCommunicationFirstRep().getLanguage().getCodingFirstRep().getDisplay();
            patientModels.add(new PatientModel(patientId, patientFirstName, patientLastName, patientBirthDate, patientPhoneNumber, patientAddress, patientLanguage));
        }

        patientIdTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("id"));
        patientFirstNameTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("firstName"));
        patientLastNameTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("lastName"));
        patientPhoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("phoneNumber"));

        final FilteredList<PatientModel> filteredPatients = new FilteredList<PatientModel>(patientModels);

        filterLastNameTextField.textProperty().addListener((observable, oldValues, newValues) -> filteredPatients.setPredicate(patient -> {
            if (newValues == null || newValues.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValues.toLowerCase();

            return patient.getLastName().toLowerCase().startsWith(lowerCaseFilter);
        }));

        patientsTableView.setItems(filteredPatients);

        patientsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        if (!(((Node) event.getTarget()).getTypeSelector().equals("TableColumnHeader")) && !(((Node) event.getTarget()).getTypeSelector().equals("TableHeaderRow"))) {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("FXMLs/patientCard.fxml"));
                            try {
                                fxmlLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            PatientCardController patientCardController = fxmlLoader.getController();
                            PatientModel selectedPatient = patientsTableView.getSelectionModel().getSelectedItem();
                            patientCardController.setPatient(selectedPatient);
                            Parent parent = fxmlLoader.getRoot();
                            Stage stage = new Stage();
                            stage.setTitle("Karta Pacjenta - " + selectedPatient.getFirstName() + " " + selectedPatient.getLastName());
                            stage.setScene(new Scene(parent, 1200, 800));
                            stage.show();
                        }
                    }
                }
            }
        });
    }
}
