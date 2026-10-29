package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book once the user confirms with {@code clear confirm}.
 * A plain {@code clear} changes nothing and explains how to confirm, so that one mistyped command cannot
 * delete every client.
 *
 * <p>AI assistance: Vincent used Claude Code to add the confirmation step and its tests.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String CONFIRM_KEYWORD = "confirm";
    public static final String MESSAGE_SUCCESS = "All clients have been cleared.";
    public static final String MESSAGE_CONFIRMATION_REQUIRED = "\"" + COMMAND_WORD
            + "\" permanently deletes all clients and their records. No clients were deleted.\n"
            + "To proceed, enter: " + COMMAND_WORD + " " + CONFIRM_KEYWORD;
    public static final String MESSAGE_EXTRA_PARAMETERS = "\"" + COMMAND_WORD + "\" only accepts \""
            + CONFIRM_KEYWORD + "\".\nExample: " + COMMAND_WORD + " " + CONFIRM_KEYWORD;

    private final boolean isConfirmed;

    /**
     * Creates a {@code ClearCommand} that deletes all clients only if {@code isConfirmed} is true.
     */
    public ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (!isConfirmed) {
            return new CommandResult(MESSAGE_CONFIRMATION_REQUIRED);
        }
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClearCommand otherClearCommand)) {
            return false;
        }

        return isConfirmed == otherClearCommand.isConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("isConfirmed", isConfirmed)
                .toString();
    }
}
