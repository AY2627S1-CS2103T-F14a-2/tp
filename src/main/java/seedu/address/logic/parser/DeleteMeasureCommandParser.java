package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEASUREMENT_TYPE;

import java.time.LocalDate;
import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteMeasureCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MeasurementType;

/**
 * Parses the displayed client index, date and type identifying an existing measurement.
 */
public class DeleteMeasureCommandParser implements Parser<DeleteMeasureCommand> {
    @Override
    public DeleteMeasureCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // Otherwise an unknown prefix, e.g. v/ copied from a measure command, is read as part of the previous value
        ParserUtil.rejectUnsupportedPrefixes(args, DeleteMeasureCommand.MESSAGE_INVALID_FORMAT,
                PREFIX_DATE, PREFIX_MEASUREMENT_TYPE);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_MEASUREMENT_TYPE);
        if (arguments.getAllValues(PREFIX_DATE).size() != 1
                || arguments.getAllValues(PREFIX_MEASUREMENT_TYPE).size() != 1
                || !arguments.getPreamble().matches(ParserUtil.INTEGER_REGEX)) {
            throw new ParseException(DeleteMeasureCommand.MESSAGE_INVALID_FORMAT);
        }
        try {
            Index index;
            try {
                index = ParserUtil.parseIndex(arguments.getPreamble());
            } catch (ParseException e) {
                throw new ParseException(DeleteMeasureCommand.MESSAGE_INVALID_INDEX, e);
            }
            LocalDate date = ParserUtil.parseCalendarDate(arguments.getValue(PREFIX_DATE).orElseThrow());
            MeasurementType type;
            try {
                type = MeasurementType.valueOf(arguments.getValue(PREFIX_MEASUREMENT_TYPE).orElseThrow()
                        .trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new ParseException(MeasurementType.MESSAGE_CONSTRAINTS, e);
            }
            return new DeleteMeasureCommand(index, date, type);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\nUse: " + DeleteMeasureCommand.MESSAGE_USAGE, e);
        }
    }
}
