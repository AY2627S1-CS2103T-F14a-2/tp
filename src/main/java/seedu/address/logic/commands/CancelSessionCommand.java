package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;

/**
 * Removes a client's recorded session, identified by displayed index or full name, while preserving their
 * other data and the current filter.
 */
public class CancelSessionCommand extends Command {
    public static final String COMMAND_WORD = "cancelsession";
    public static final String MESSAGE_USAGE = "cancelsession INDEX or cancelsession n/NAME";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format! Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_NO_SESSION = "No session is recorded for %s.";
    public static final String MESSAGE_SUCCESS = "Cancelled session on %s for %s.";
    public static final String MESSAGE_CLIENTS_SHIFTED =
            " %s is no longer shown here; clients below have moved up one place.";

    private final Index index;
    private final String name;

    /**
     * Identifies the client using their position in the currently displayed list.
     */
    public CancelSessionCommand(Index index) {
        this.index = requireNonNull(index);
        this.name = null;
    }

    /**
     * Identifies the client by their full name, ignoring case and repeated spaces, among all clients.
     */
    public CancelSessionCommand(String name) {
        this.index = null;
        this.name = Name.normalizeSpaces(requireNonNull(name));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person target = findTarget(model);
        NextSession cancelledSession = target.getNextSession().orElseThrow(() ->
                new CommandException(String.format(MESSAGE_NO_SESSION, target.getName())));
        boolean wasShown = model.getFilteredPersonList().contains(target);
        Person cancelled = target.withNextSession(null);
        model.setPerson(target, cancelled);
        // Keep date/search filters: a cancelled client should disappear from a session-date view.
        model.updateFilteredPersonList(model.getFilteredPersonListPredicate());
        String message = String.format(MESSAGE_SUCCESS, cancelledSession, target.getName());
        // Index numbers below the cancelled client have shifted, so a repeated index would hit someone else.
        if (wasShown && !model.getFilteredPersonList().contains(cancelled)) {
            message += String.format(MESSAGE_CLIENTS_SHIFTED, target.getName());
        }
        return new CommandResult(message, false, false, true);
    }

    private Person findTarget(Model model) throws CommandException {
        if (index != null) {
            if (index.getZeroBased() >= model.getFilteredPersonList().size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            return model.getFilteredPersonList().get(index.getZeroBased());
        }
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().matches(name))
                .findFirst().orElseThrow(() -> new CommandException(SessionCommand.MESSAGE_NO_SUCH_NAME));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CancelSessionCommand command
                && Objects.equals(index, command.index)
                && Objects.equals(name, command.name);
    }
}
