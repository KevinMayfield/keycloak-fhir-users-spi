package uk.gov.dwp.keycoak.user;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Person;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private final Person user;
    private final String keycloakId;


    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Person user) {
        super(session, realm, model);
        this.user = user;
        this.keycloakId = StorageId.keycloakId(model, user.getId());
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        if (user.hasName()) {
            return user.getNameFirstRep().getNameAsSingleString();
        }
        return null;
    }

    @Override
    public void setUsername(String username) {

        // user.setUsername(username);
    }

    @Override
    public String getEmail() {
        if (user.hasTelecom()) {
            for (ContactPoint contactPoint : user.getTelecom()) {
                if (contactPoint.hasSystem() && contactPoint.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                    return contactPoint.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void setEmail(String email) {
        // user.setEmail(email);
    }

    @Override
    public String getFirstName() {
        if (user.hasName()) {
            return user.getNameFirstRep().getGivenAsSingleString();
        }
        return null;
    }

    @Override
    public void setFirstName(String firstName) {
        // user.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        if (user.hasName()) {
            return user.getNameFirstRep().getFamily();
        }
        return null;
    }

    @Override
    public void setLastName(String lastName) {
        //user.setLastName(lastName);
    }
}
