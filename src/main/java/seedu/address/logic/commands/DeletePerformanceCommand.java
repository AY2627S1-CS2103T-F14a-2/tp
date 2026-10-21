package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;

/**
 * Deletes the gym-performance record for an exercise on a date, for the client at the given displayed index.
 * The client's latest performance and record count are recalculated from the remaining records.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this command and its tests.
 */
public class DeletePerformanceCommand extends Command {

    public static final String COMMAND_WORD = "deleteperformance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX d/DATE e/EXERCISE";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
    public static final String MESSAGE_RECORD_NOT_FOUND = "No performance record for %1$s on %2$s was found for %3$s.";
    public static final String MESSAGE_SUCCESS = "Deleted performance for %1$s: %2$s on %3$s.";

    private final Index index;
    private final LocalDate date;
    private final ExerciseName exerciseName;

    /**
     * Creates a command to delete the record for {@code exerciseName} on {@code date}
     * for the client at {@code index}.
     */
    public DeletePerformanceCommand(Index index, LocalDate date, ExerciseName exerciseName) {
        requireAllNonNull(index, date, exerciseName);
        this.index = index;
        this.date = date;
        this.exerciseName = exerciseName;
    }

    /**
     * Returns {@code message} followed by the expected command format.
     */
    public static String withUsage(String message) {
        return message + "\nUse: " + MESSAGE_USAGE;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedClients = model.getFilteredPersonList();

        if (index.getZeroBased() >= displayedClients.size()) {
            throw new CommandException(withUsage(MESSAGE_INVALID_INDEX));
        }

        Person client = displayedClients.get(index.getZeroBased());
        String formattedDate = date.format(GymPerformance.DATE_FORMATTER);
        GymPerformance record = client.getPerformanceHistory().find(date, exerciseName)
                .orElseThrow(() -> new CommandException(withUsage(String.format(MESSAGE_RECORD_NOT_FOUND,
                        exerciseName, formattedDate, client.getName()))));

        model.setPerson(client, client.withoutGymPerformance(record));

        return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), record.getSummary(),
                formattedDate));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeletePerformanceCommand otherCommand)) {
            return false;
        }

        return index.equals(otherCommand.index)
                && date.equals(otherCommand.date)
                && exerciseName.equals(otherCommand.exerciseName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("date", date)
                .add("exerciseName", exerciseName)
                .toString();
    }
}
