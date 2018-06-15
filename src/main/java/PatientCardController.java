import ca.uhn.fhir.rest.client.api.IGenericClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class PatientCardController implements Initializable {

    private String patientId;
    IGenericClient client;
    ArrayList<Observation> observations;
    ArrayList<MedicationRequest> medicationRequests;

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

    @FXML
    DatePicker fromDatePicker;
    @FXML
    DatePicker toDatePicker;

    @FXML
    TableView<ObservationModel> observationsTableView;
    @FXML
    TableView<MedicationRequestModel> medicationRequestsTableView;

    @FXML
    TableColumn<ObservationModel, String> dateTableColumnO;
    @FXML
    TableColumn<ObservationModel, String> statusTableColumnO;
    @FXML
    TableColumn<ObservationModel, String> categoryTableColumnO;
    @FXML
    TableColumn<ObservationModel, String> codeTableColumnO;
    @FXML
    TableColumn<ObservationModel, String> valueTableColumnO;
    @FXML
    TableColumn<ObservationModel, String> unitTableColumnO;

    @FXML
    TableColumn<MedicationRequestModel, String> dateTableColumnMR;
    @FXML
    TableColumn<MedicationRequestModel, String> statusTableColumnMR;
    @FXML
    TableColumn<MedicationRequestModel, String> intentTableColumnMR;
    @FXML
    TableColumn<MedicationRequestModel, String> medicationTableColumnMR;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FhirClient fhirClient = new FhirClient();
        client = fhirClient.getClient();
        observations = new ArrayList<>();
        medicationRequests = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        fromDatePicker.setValue(LocalDate.parse("01-01-2000", dateTimeFormatter));
        toDatePicker.setValue(LocalDate.parse(currentDate, dateTimeFormatter));
    }

    public void setPatient(PatientModel patientModel) {
        this.patientId = patientModel.getId();
        patientFullNameLabel.setText(patientModel.getFirstName() + " " + patientModel.getLastName());
        patientIdLabel.setText(patientModel.getId());
        patientBirthDateLabel.setText(patientModel.getBirthDate());
        patientPhoneNumberLabel.setText(patientModel.getPhoneNumber());
        patientAddressLabel.setText(patientModel.getAddress());
        patientLanguageLabel.setText(patientModel.getLanguage());
    }

    public void searchPatientObservation() {

        Bundle result = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(patientId)).returnBundle(Bundle.class).execute();

        while (true) {
            for (Bundle.BundleEntryComponent observation : result.getEntry()) {
                String observationId = observation.getResource().getId().split("/")[5];
                observations.add(client.read().resource(Observation.class).withId(observationId).execute());
            }
            if (result.getLink(Bundle.LINK_NEXT) != null) result = client.loadPage().next(result).execute();
            else break;
        }
    }

    public void searchPatientMedicationRequest() {
        Bundle result = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(patientId)).returnBundle(Bundle.class).execute();

        while (true) {
            for (Bundle.BundleEntryComponent medicationRequest : result.getEntry()) {
                String medicationRequestId = medicationRequest.getResource().getId().split("/")[5];
                medicationRequests.add(client.read().resource(MedicationRequest.class).withId(medicationRequestId).execute());
            }
            if (result.getLink(Bundle.LINK_NEXT) != null) result = client.loadPage().next(result).execute();
            else break;
        }
    }
}
