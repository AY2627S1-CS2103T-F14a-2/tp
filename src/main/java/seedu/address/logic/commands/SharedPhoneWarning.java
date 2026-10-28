package seedu.address.logic.commands;

import java.util.stream.Collectors;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * Builds non-blocking warnings for other clients with the same complete phone digit sequence.
 */
final class SharedPhoneWarning {

    private SharedPhoneWarning() {}

    /**
     * Returns one warning line per other matching client, in address book order.
     * The saved client is excluded by identity, including after an edit that changes their name.
     */
    static String forClient(ReadOnlyAddressBook addressBook, Person savedClient) {
        String digits = savedClient.getPhone().getDigits();
        return addressBook.getPersonList().stream()
                .filter(other -> !other.isSamePerson(savedClient))
                .filter(other -> other.getPhone().getDigits().equals(digits))
                .map(other -> "\nWarning: " + other.getName() + " also has this phone number.")
                .collect(Collectors.joining());
    }
}
