package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;

/**
 * Deletes one measurement identified by displayed client index, date and type.
 */
public class DeleteMeasureCommand extends Command {
    public static final String COMMAND_WORD = "deletemeasure";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX d/DATE t/TYPE";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
    public static final String MESSAGE_RECORD_NOT_FOUND = "No measurement record for %1$s on %2$s was found for %3$s."
            + "\nUse \"view %4$d\" to check this client's measurement history.";
    public static final String MESSAGE_SUCCESS = "Deleted measurement for %s: %s.";

    private final Index index;
    private final LocalDate date;
    private final MeasurementType type;

    /**
     * Creates a command targeting a measurement of the client at the currently displayed index.
     */
    public DeleteMeasureCommand(Index index, LocalDate date, MeasurementType type) {
        requireAllNonNull(index, date, type);
        this.index = index;
        this.date = date;
        this.type = type;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> displayedClients = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayedClients.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX + "\nUse: " + MESSAGE_USAGE);
        }
        Person client = displayedClients.get(index.getZeroBased());
        Measurement record = client.getMeasurements().stream()
                .filter(measurement -> measurement.getDate().equals(date) && measurement.getType() == type)
                .findFirst().orElseThrow(() -> new CommandException(
                        String.format(Locale.ROOT, MESSAGE_RECORD_NOT_FOUND,
                                type, date, client.getName(), index.getOneBased())));
        model.setPerson(client, client.withoutMeasurement(date, type));
        return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), record));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DeleteMeasureCommand command
                && index.equals(command.index) && date.equals(command.date) && type == command.type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("date", date).add("type", type).toString();
    }
}
