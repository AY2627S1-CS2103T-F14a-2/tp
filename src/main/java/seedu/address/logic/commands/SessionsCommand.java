package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Locale;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.SessionOnDatePredicate;

/**
 * Shows all clients whose single recorded session is on the requested date, without modifying stored data.
 */
public class SessionsCommand extends Command {
    public static final String COMMAND_WORD = "sessions";
    public static final String MESSAGE_USAGE = "sessions d/DATE";
    public static final String MESSAGE_INVALID_FORMAT = Messages.getInvalidFormatMessage(MESSAGE_USAGE);
    public static final String MESSAGE_SUCCESS = "Listed %d clients with sessions on %s.";
    public static final String MESSAGE_SUCCESS_ONE_CLIENT = "Listed 1 client with a session on %s.";
    public static final String MESSAGE_NO_SESSIONS = "No sessions recorded on %s.";

    private final LocalDate date;

    public SessionsCommand(LocalDate date) {
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(new SessionOnDatePredicate(date));
        int count = model.getFilteredPersonList().size();
        String message;
        if (count == 0) {
            message = String.format(MESSAGE_NO_SESSIONS, date);
        } else if (count == 1) {
            message = String.format(MESSAGE_SUCCESS_ONE_CLIENT, date);
        } else {
            message = String.format(Locale.ROOT, MESSAGE_SUCCESS, count, date);
        }
        return new CommandResult(message, false, false, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SessionsCommand command && date.equals(command.date);
    }
}
