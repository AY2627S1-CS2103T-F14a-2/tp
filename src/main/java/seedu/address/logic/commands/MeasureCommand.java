package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Person;

/**
 * Records or replaces a body measurement for the client at a displayed index.
 * AI assistance: OpenAI Codex helped implement this command, parser and tests.
 * Helen used OpenAI Codex to rename replacement booleans here and in MeasurePersistenceTest for issue #179.
 * Helen used OpenAI Codex for old/new replacement feedback and regression tests for issue #191.
 * This assistance also covered the related UG example and DG implementation, use case and manual tests.
 */
public class MeasureCommand extends Command {
    public static final String COMMAND_WORD = "measure";
    public static final String MESSAGE_USAGE = "measure INDEX d/DATE t/TYPE v/VALUE";
    public static final String MESSAGE_INVALID_FORMAT = Messages.getInvalidFormatMessage(MESSAGE_USAGE);
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
    public static final String MESSAGE_RECORDED = "Recorded measurement for %s: %s.";
    public static final String MESSAGE_UPDATED = "Updated measurement for %s: %s on %s: %s → %s %s.";

    private final Index index;
    private final Measurement measurement;

    /**
     * Creates a command targeting an index in the currently displayed client list.
     */
    public MeasureCommand(Index index, Measurement measurement) {
        requireAllNonNull(index, measurement);
        this.index = index;
        this.measurement = measurement;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> displayedClients = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayedClients.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX + "\nUse: " + MESSAGE_USAGE);
        }
        Person client = displayedClients.get(index.getZeroBased());
        Optional<Measurement> previous = client.findMeasurement(measurement);
        model.setPerson(client, client.withMeasurement(measurement));
        String feedback = previous.map(record -> String.format(MESSAGE_UPDATED, client.getName(),
                measurement.getType(), measurement.getDate(), record.getValue().toPlainString(),
                measurement.getValue().toPlainString(), measurement.getType().getUnit()))
                .orElseGet(() -> String.format(MESSAGE_RECORDED, client.getName(), measurement));
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MeasureCommand command
                && index.equals(command.index) && measurement.equals(command.measurement);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("measurement", measurement).toString();
    }
}
