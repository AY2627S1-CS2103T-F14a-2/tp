package seedu.address.logic.commands;

import java.util.List;

import seedu.address.model.person.Person;

/**
 * Builds the note shown when a command changes which client a previously displayed index refers to.
 */
final class ClientOrderNote {
    static final String MESSAGE =
            "\nThe client list order has changed; check the index numbers before your next command.";

    private ClientOrderNote() {}

    /**
     * Returns {@link #MESSAGE} if any client in {@code shownBefore} is no longer at the same displayed index in
     * {@code shownAfter}, or an empty string otherwise. {@code target} is matched as {@code updated} afterwards.
     * Clients appended after the previously displayed ones (e.g. by clearing a filter) do not count as moves,
     * because every earlier index still refers to the same client.
     */
    static String forChange(List<Person> shownBefore, Person target, Person updated, List<Person> shownAfter) {
        for (int i = 0; i < shownBefore.size(); i++) {
            Person client = shownBefore.get(i).equals(target) ? updated : shownBefore.get(i);
            if (shownAfter.indexOf(client) != i) {
                return MESSAGE;
            }
        }
        return "";
    }
}
