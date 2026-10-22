package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;

/**
 * Removes the displayed client's recorded session while preserving their other data and the current filter.
 */
public class CancelSessionCommand extends Command {
    public static final String COMMAND_WORD = "cancelsession";
    public static final String MESSAGE_USAGE = "cancelsession INDEX";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format! Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_NO_SESSION = "No session is recorded for %s.";
    public static final String MESSAGE_SUCCESS = "Cancelled session on %s for %s.";

    private final Index index;

    public CancelSessionCommand(Index index) {
        this.index = requireNonNull(index);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (index.getZeroBased() >= model.getFilteredPersonList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person target = model.getFilteredPersonList().get(index.getZeroBased());
        NextSession cancelledSession = target.getNextSession().orElseThrow(() ->
                new CommandException(String.format(MESSAGE_NO_SESSION, target.getName())));
        model.setPerson(target, target.withNextSession(null));
        // Keep date/search filters: a cancelled client should disappear from a session-date view.
        model.updateFilteredPersonList(model.getFilteredPersonListPredicate());
        return new CommandResult(String.format(MESSAGE_SUCCESS, cancelledSession, target.getName()),
                false, false, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CancelSessionCommand command && index.equals(command.index);
    }
}
