package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.Messages;
import seedu.address.model.Model;

/**
 * Lists all clients in Spotter to the user, and shows the upcoming sessions again instead of a viewed client.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

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
        // Same wording as find for one or more clients; only the empty case invites the trainer to add one
        return clientCount == 0 ? MESSAGE_NO_CLIENTS : Messages.getClientsListedMessage(clientCount);
    }
}
