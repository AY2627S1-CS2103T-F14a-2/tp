package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Shows the full details and history of a client identified by their displayed index.
 * Read-only: neither the data nor the displayed list changes, so nothing is saved.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the full details and history of the client identified by the index number "
            + "used in the displayed client list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    private static final Logger logger = LogsCenter.getLogger(ViewCommand.class);

    private final Index targetIndex;
    private final Clock clock;

    public ViewCommand(Index targetIndex) {
        this(targetIndex, Clock.systemDefaultZone());
    }

    /**
     * Creates a command that reads the current time from {@code clock}, so tests can fix the time
     * used to decide whether the next session has passed.
     */
    ViewCommand(Index targetIndex, Clock clock) {
        requireNonNull(targetIndex);
        requireNonNull(clock);
        this.targetIndex = targetIndex;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        assert targetIndex.getZeroBased() >= 0 && targetIndex.getZeroBased() < lastShownList.size();

        // Logs only the index, as client details do not belong in log files.
        logger.fine("Viewing client at displayed index " + targetIndex.getOneBased());
        Person personToView = lastShownList.get(targetIndex.getZeroBased());
        return new CommandResult(ClientHistoryFormatter.format(personToView, LocalDateTime.now(clock)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
