package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Locale;

import seedu.address.model.Model;

/**
 * Lists all clients in Spotter to the user, and shows the upcoming sessions again instead of a viewed client.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed %1$d clients.";
    public static final String MESSAGE_SUCCESS_ONE_CLIENT = "Listed 1 client.";
    public static final String MESSAGE_NO_CLIENTS =
            "You have no clients yet. Use \"add\" to add your first client.";
    public static final String MESSAGE_EXTRA_PARAMETERS =
            "\"" + COMMAND_WORD + "\" does not take any parameters.\nExample: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.setViewedPerson(null);
        return new CommandResult(getResultMessage(model.getFilteredPersonList().size()), false, false, true);
    }

    /**
     * Returns the feedback message for listing {@code clientCount} clients.
     */
    public static String getResultMessage(int clientCount) {
        assert clientCount >= 0;
        if (clientCount == 0) {
            return MESSAGE_NO_CLIENTS;
        }
        if (clientCount == 1) {
            return MESSAGE_SUCCESS_ONE_CLIENT;
        }
        return String.format(Locale.ROOT, MESSAGE_SUCCESS, clientCount);
    }
}
