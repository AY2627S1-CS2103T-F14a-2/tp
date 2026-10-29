package seedu.address.logic;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 * AI assistance: Helen used OpenAI Codex for issue #202's recovery guidance, related tests and UG/DG updates.
 * Vincent used Claude Code to share one invalid command format message across commands.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type \"help\" for a link to the user guide.";
    public static final String MESSAGE_INVALID_FORMAT_HEADER = "Invalid command format!";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = MESSAGE_INVALID_FORMAT_HEADER + "\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The client index provided is invalid."
            + " Use a positive index from the displayed client list. Use \"list\" to show all clients,"
            + " then check their indexes again.";
    public static final String MESSAGE_DUPLICATE_CLIENT = "Another client already has this name."
            + " Names are compared ignoring letter case and extra spaces."
            + " Use \"find NAME\" to check the existing client.";
    public static final String MESSAGE_NO_CLIENTS_FOUND = "No clients found.";
    public static final String MESSAGE_ONE_CLIENT_LISTED = "Listed 1 client.";
    public static final String MESSAGE_CLIENTS_LISTED = "Listed %1$d clients.";
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
     * Returns the invalid command format message followed by the expected command format {@code usage},
     * e.g. "Invalid command format!\nUse: sessions d/DATE".
     */
    public static String getInvalidFormatMessage(String usage) {
        return withUsage(MESSAGE_INVALID_FORMAT_HEADER, usage);
    }

    /**
     * Returns the feedback message for a search that shows {@code clientCount} clients, using the same
     * "Listed N clients." wording as {@code list} with a correct singular form.
     */
    public static String getClientsListedMessage(int clientCount) {
        assert clientCount >= 0;
        if (clientCount == 0) {
            return MESSAGE_NO_CLIENTS_FOUND;
        }
        if (clientCount == 1) {
            return MESSAGE_ONE_CLIENT_LISTED;
        }
        // Locale.ROOT keeps the count in ASCII digits whatever the system locale is
        return String.format(Locale.ROOT, MESSAGE_CLIENTS_LISTED, clientCount);
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
