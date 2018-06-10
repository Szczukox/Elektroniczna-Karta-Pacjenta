import Controllers.ApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

public class Main extends Application {
    public static void main(String[] args) {
        FhirClient fhirClient = new FhirClient();
        Bundle result = fhirClient.getClient().search().forResource(Patient.class).returnBundle(org.hl7.fhir.dstu3.model.Bundle.class).execute();
        launch();
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLs/application.fxml"));
        Parent root = fxmlLoader.load();
        ApplicationController applicationController = fxmlLoader.getController();
        primaryStage.setTitle("Elektroniczna Karta Pacjenta");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }
}
