package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.progress.GymPerformance;

/**
 * Records a dated gym-performance result for the client at the given displayed index.
 * A record for the same exercise on the same date replaces the existing one.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this command and its tests.
 */
public class PerformanceCommand extends Command {

    public static final String COMMAND_WORD = "performance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
    public static final String MESSAGE_FUTURE_DATE = "The performance date cannot be in the future.";
    public static final String MESSAGE_RECORDED = "Recorded performance for %1$s: %2$s on %3$s.";
    public static final String MESSAGE_UPDATED = "Updated performance for %1$s: %2$s on %3$s.";

    private final Index index;
    private final GymPerformance performance;

    /**
     * Creates a command to record {@code performance} for the client at {@code index}.
     */
    public PerformanceCommand(Index index, GymPerformance performance) {
        requireAllNonNull(index, performance);
        this.index = index;
        this.performance = performance;
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
        boolean isReplacing = client.hasGymPerformance(performance);
        model.setPerson(client, client.withGymPerformance(performance));

        String message = isReplacing ? MESSAGE_UPDATED : MESSAGE_RECORDED;
        return new CommandResult(String.format(message, client.getName(), performance.getSummary(),
                performance.getDate().format(GymPerformance.DATE_FORMATTER)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PerformanceCommand otherCommand)) {
            return false;
        }

        return index.equals(otherCommand.index)
                && performance.equals(otherCommand.performance);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("performance", performance)
                .toString();
    }
}
