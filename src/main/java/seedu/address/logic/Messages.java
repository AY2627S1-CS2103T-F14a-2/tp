package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type \"help\" to see all commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The client index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns {@code message} followed by the expected command format {@code usage} on a new line,
     * e.g. "The client index provided is invalid.\nUse: performance INDEX d/DATE ...".
     */
    public static String withUsage(String message, String usage) {
        return message + "\nUse: " + usage;
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user, omitting the email if there is none.
     * The phone is grouped the same way as on the client card.
     */
    public static String format(Person person) {
        String email = person.getEmail().map(e -> "; Email: " + e).orElse("");
        return person.getName() + "; Phone: " + PhoneDisplayFormatter.format(person.getPhone().value) + email;
    }

}
