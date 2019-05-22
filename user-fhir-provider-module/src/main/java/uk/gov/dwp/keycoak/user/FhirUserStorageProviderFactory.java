package uk.gov.dwp.keycoak.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FhirUserStorageProviderFactory implements UserStorageProviderFactory<FhirUserStorageProvider> {

    @Override
    public FhirUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here you can setup the user storage provider, initiate some connections, etc.

        //FhirRepository repository = new FhirRepository();

        return new FhirUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return "fhir-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("fhirUrl", "FHIR Server (STU3)", "Base Address of FHIR Server", ProviderConfigProperty.STRING_TYPE, "some value", null)
                .build();
    }
}
