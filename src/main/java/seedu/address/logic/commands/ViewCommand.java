package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

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
 * Shows the full details and history of a client identified by their displayed index in the detail panel.
 * Read-only: neither the data nor the displayed list changes, so nothing is saved.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX";
    public static final String MESSAGE_INVALID_FORMAT = Messages.withUsage("Invalid command format!", MESSAGE_USAGE);

    public static final String MESSAGE_VIEW_SUCCESS = "Showing details and history of %1$s.";

    private static final Logger logger = LogsCenter.getLogger(ViewCommand.class);

    private final Index targetIndex;

    /**
     * Creates a command that shows the client at {@code targetIndex} of the displayed list.
     */
    public ViewCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
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
        model.setViewedPerson(personToView);
        return new CommandResult(String.format(MESSAGE_VIEW_SUCCESS, personToView.getName().fullName));
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
