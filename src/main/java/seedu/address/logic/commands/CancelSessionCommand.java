package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;

/**
 * Removes a client's recorded session by displayed index or full name, preserving their data and filter.
 */
public class CancelSessionCommand extends Command {
    public static final String COMMAND_WORD = "cancelsession";
    public static final String MESSAGE_USAGE = "cancelsession INDEX or cancelsession n/NAME";
    public static final String MESSAGE_INVALID_FORMAT = Messages.getInvalidFormatMessage(MESSAGE_USAGE);
    public static final String MESSAGE_NO_SESSION = "No session is recorded for %s.";
    public static final String MESSAGE_SUCCESS = "Cancelled session on %s for %s.";

    private final Index index;
    private final String name;

    /**
     * Identifies the client at the supplied position in the displayed list.
     */
    public CancelSessionCommand(Index index) {
        this.index = requireNonNull(index);
        this.name = null;
    }

    /**
     * Identifies a client across the whole address book, using the same name matching as booking.
     */
    public CancelSessionCommand(String name) {
        this.index = null;
        this.name = Name.normalizeSpaces(requireNonNull(name));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person target = SessionCommand.findClient(model, index, name);
        NextSession cancelledSession = target.getNextSession().orElseThrow(() ->
                new CommandException(String.format(MESSAGE_NO_SESSION, target.getName())));
        List<Person> shownBefore = List.copyOf(model.getFilteredPersonList());
        Person cancelled = target.withNextSession(null);
        model.setPerson(target, cancelled);
        // Keep date/search filters: a cancelled client should disappear from a session-date view.
        model.updateFilteredPersonList(model.getFilteredPersonListPredicate());
        // Cancellation can remove a filtered row or move a client behind other bookings in the full list.
        String message = String.format(MESSAGE_SUCCESS, cancelledSession, target.getName())
                + ClientOrderNote.forChange(shownBefore, target, cancelled, model.getFilteredPersonList());
        return new CommandResult(message, false, false, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CancelSessionCommand command
                && Objects.equals(index, command.index) && Objects.equals(name, command.name);
    }
}
