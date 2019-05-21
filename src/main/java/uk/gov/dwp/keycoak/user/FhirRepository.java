package uk.gov.dwp.keycoak.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
class FhirRepository {

    private List<FhirUser> users;

    FhirRepository() {
        users = Arrays.asList(
                new FhirUser("1", "Fred", "Flintstone"),
                new FhirUser("3", "Wilma", "Flintstone"),
                new FhirUser("5", "Pebbles", "Flintstone"),
                new FhirUser("2", "Barney", "Rubble"),
                new FhirUser("4", "Betty", "Rubble"),
                new FhirUser("6", "Bam Bam", "Rubble")
        );
    }

    List<FhirUser> getAllUsers() {
        return users;
    }

    int getUsersCount() {
        return users.size();
    }

    FhirUser findUserById(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    FhirUser findUserByUsernameOrEmail(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    List<FhirUser> findUsers(String query) {
        return users.stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    boolean validateCredentials(String username, String password) {
        return findUserByUsernameOrEmail(username).getPassword().equals(password);
    }

    boolean updateCredentials(String username, String password) {
        findUserByUsernameOrEmail(username).setPassword(password);
        return true;
    }

}
