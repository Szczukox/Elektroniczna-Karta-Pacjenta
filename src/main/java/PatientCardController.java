import ca.uhn.fhir.rest.client.api.IGenericClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class PatientCardController implements Initializable {

    private String patientId;
    private IGenericClient client;
    private ObservableList<ObservationModel> observations;
    private ObservableList<MedicationRequestModel> medicationRequests;
    private DateFormat dateFormat;
    private DateTimeFormatter dateTimeFormatter;

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
        observations = FXCollections.observableArrayList();
        medicationRequests = FXCollections.observableArrayList();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        fromDatePicker.setValue(LocalDate.parse("01-01-1900", dateTimeFormatter));
        toDatePicker.setValue(LocalDate.parse(currentDate, dateTimeFormatter));

        dateTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("date"));
        statusTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("status"));
        categoryTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("category"));
        codeTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("code"));
        valueTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("value"));
        unitTableColumnO.setCellValueFactory(new PropertyValueFactory<ObservationModel, String>("unit"));

        dateTableColumnMR.setCellValueFactory(new PropertyValueFactory<MedicationRequestModel, String>("date"));
        statusTableColumnMR.setCellValueFactory(new PropertyValueFactory<MedicationRequestModel, String>("status"));
        intentTableColumnMR.setCellValueFactory(new PropertyValueFactory<MedicationRequestModel, String>("intent"));
        medicationTableColumnMR.setCellValueFactory(new PropertyValueFactory<MedicationRequestModel, String>("medication"));

        dateTableColumnO.setSortType(TableColumn.SortType.ASCENDING);
        dateTableColumnMR.setSortType(TableColumn.SortType.ASCENDING);

        observationsTableView.getSortOrder().add(dateTableColumnO);
        medicationRequestsTableView.getSortOrder().add(dateTableColumnMR);
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
                Observation newObservation = client.read().resource(Observation.class).withId(observationId).execute();
                String date = dateFormat.format(newObservation.getIssued());
                String status = newObservation.getStatus().getDisplay();
                String category = "";
                if (newObservation.getCategoryFirstRep().getCodingFirstRep().getCode() != null)
                    category = newObservation.getCategoryFirstRep().getCodingFirstRep().getCode();
                String code = newObservation.getCode().getCodingFirstRep().getDisplay();
                String value = "";
                String unit = "";
                try {
                    value = newObservation.getValueQuantity().getValue().toString();
                    unit = newObservation.getValueQuantity().getUnit();
                } catch (FHIRException | NullPointerException e) {
                }
                ObservationModel observationModel = new ObservationModel(date, status, category, code, value, unit);
                observations.add(observationModel);
            }
            if (result.getLink(Bundle.LINK_NEXT) != null) result = client.loadPage().next(result).execute();
            else break;
        }

        final FilteredList<ObservationModel> filteredObservations = new FilteredList<ObservationModel>(observations);

        fromDatePicker.valueProperty().addListener((observable, oldValues, newValues) -> filteredObservations.setPredicate(observation -> {
            if (newValues == null) {
                return true;
            }
            try {
                return dateFormat.parse(newValues.format(dateTimeFormatter)).before(dateFormat.parse(observation.getDate())) ||
                        dateFormat.parse(newValues.format(dateTimeFormatter)).equals(dateFormat.parse(observation.getDate()));
            } catch (ParseException e) {
                return false;
            }
        }));

        toDatePicker.valueProperty().addListener(((observable, oldValues, newValues) -> filteredObservations.setPredicate(observation -> {
            if (newValues == null) {
                return true;
            }
            try {
                return dateFormat.parse(newValues.format(dateTimeFormatter)).after(dateFormat.parse(observation.getDate())) ||
                        dateFormat.parse(newValues.format(dateTimeFormatter)).equals(dateFormat.parse(observation.getDate()));
            } catch (ParseException e) {
                return false;
            }
        })));

        observationsTableView.setItems(filteredObservations);

        System.out.println(observations.size());
    }

    public void searchPatientMedicationRequest() {
        Bundle result = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(patientId)).returnBundle(Bundle.class).execute();

        while (true) {
            for (Bundle.BundleEntryComponent medicationRequest : result.getEntry()) {
                String medicationRequestId = medicationRequest.getResource().getId().split("/")[5];
                MedicationRequest newMedicationRequest = client.read().resource(MedicationRequest.class).withId(medicationRequestId).execute();
                String date = dateFormat.format(newMedicationRequest.getAuthoredOn());
                String status = newMedicationRequest.getStatus().getDisplay();
                String intent = newMedicationRequest.getIntent().getDisplay();
                String medication = "";
                try {
                    medication = newMedicationRequest.getMedicationCodeableConcept().getCodingFirstRep().getDisplay();
                } catch (FHIRException | NullPointerException e) {
                }
                MedicationRequestModel medicationRequestModel = new MedicationRequestModel(date, status, intent, medication);
                medicationRequests.add(medicationRequestModel);
            }
            if (result.getLink(Bundle.LINK_NEXT) != null) result = client.loadPage().next(result).execute();
            else break;
        }

        final FilteredList<MedicationRequestModel> filteredMedicalRequests = new FilteredList<MedicationRequestModel>(medicationRequests);

        medicationRequestsTableView.setItems(filteredMedicalRequests);

        fromDatePicker.valueProperty().addListener((observable, oldValues, newValues) -> filteredMedicalRequests.setPredicate(medicationRequestModel -> {
            if (newValues == null) {
                return true;
            }
            try {
                return dateFormat.parse(newValues.format(dateTimeFormatter)).before(dateFormat.parse(medicationRequestModel.getDate())) ||
                        dateFormat.parse(newValues.format(dateTimeFormatter)).equals(dateFormat.parse(medicationRequestModel.getDate()));
            } catch (ParseException e) {
                return false;
            }
        }));

        toDatePicker.valueProperty().addListener(((observable, oldValues, newValues) -> filteredMedicalRequests.setPredicate(medicationRequest -> {
            if (newValues == null) {
                return true;
            }
            try {
                return dateFormat.parse(newValues.format(dateTimeFormatter)).after(dateFormat.parse(medicationRequest.getDate())) ||
                        dateFormat.parse(newValues.format(dateTimeFormatter)).equals(dateFormat.parse(medicationRequest.getDate()));
            } catch (ParseException e) {
                return false;
            }
        })));

        System.out.println(medicationRequests.size());
    }
}
