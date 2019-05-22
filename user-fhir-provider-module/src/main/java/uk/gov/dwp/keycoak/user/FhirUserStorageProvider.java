package uk.gov.dwp.keycoak.user;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Person;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FhirUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, UserQueryProvider, CredentialInputUpdater, CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final FhirContext ctx;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FhirUserStorageProvider.class);

    public FhirUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.ctx = FhirContext.forDstu3();

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("isValid");
        return false;
        /*
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.validateCredentials(user.getUsername(), cred.getValue());*/
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("updateCredentials");
        return false;
        /*
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.updateCredentials(user.getUsername(), cred.getValue());
        */

    }


    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void preRemove(RealmModel realm) {
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info("getUserById "+id);
        String externalId = StorageId.externalId(id);
        String fs = model.getConfig().getFirst("fhirUrl");
        if (fs == null) throw new ComponentValidationException("user property file does not exist");


        IGenericClient client = ctx.newRestfulGenericClient(fs);
        try {
            Person person = client.read()
                    .resource(Person.class)
                    .withId(id)
                    .execute();
            if (person != null) {
                return new UserAdapter(session, realm, model, person);
            }
        } catch (ResourceNotFoundException notFound) {
            return null;
        }
        return null;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("getUserByUsername "+username);
        return getUserById(username.replace("Person/",""), realm);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info("getUserByEmail " + email);
        String fs = model.getConfig().getFirst("fhirUrl");
        if (fs == null) throw new ComponentValidationException("user property file does not exist");


        IGenericClient client = ctx.newRestfulGenericClient(fs);

        Bundle results = client.search().forResource(Person.class)
                .where(new TokenClientParam(Person.SP_EMAIL).exactly().systemAndValues("email",email))
                .returnBundle(Bundle.class)
                .execute();
        for (Bundle.BundleEntryComponent entry : results.getEntry()) {
            if (entry.getResource() instanceof Person) {
                return new UserAdapter(session, realm, model, (Person) entry.getResource());
            }
        }
        return getUserByUsername(email, realm);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return 0; //repository.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.info("getUsers1");
        return new ArrayList<>();
        /*
        return repository.getAllUsers().stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());

         */
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        log.info("getUsers2");
        return new ArrayList<>();
        /*
        return getUsers(realm);

         */
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        log.info("searchForUser1");
        return new ArrayList<>();
        /*return repository.findUsers(search).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());

         */
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info("searchForUser2");
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        log.info("searchForUserByUserAttribute");
        return null;
    }
}
