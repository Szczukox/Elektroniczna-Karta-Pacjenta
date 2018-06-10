import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirClient {

    private IGenericClient client;

    public FhirClient() {
        FhirContext fhirContext = FhirContext.forDstu3();
        String serverBase = "http://localhost:8080/baseDstu3";
        client = fhirContext.newRestfulGenericClient(serverBase);
    }
}
