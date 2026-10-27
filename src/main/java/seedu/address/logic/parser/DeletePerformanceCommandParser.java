package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXERCISE;

import java.time.LocalDate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeletePerformanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.progress.ExerciseName;

/**
 * Parses input arguments and creates a new {@code DeletePerformanceCommand} object.
 *
 * <p>The index and exercise name follow the same rules as {@code performance}. The date may be in the future,
 * so that a future-dated record loaded from an edited data file can still be deleted.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this parser and its tests.
 */
public class DeletePerformanceCommandParser implements Parser<DeletePerformanceCommand> {

    private static final Prefix[] PREFIXES = {PREFIX_DATE, PREFIX_EXERCISE};

    /**
     * Parses the given {@code String} of arguments in the context of the {@code DeletePerformanceCommand}
     * and returns a {@code DeletePerformanceCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format or has an invalid value.
     */
    @Override
    public DeletePerformanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIXES);

        String preamble = argMultimap.getPreamble();
        if (!argMultimap.hasEachPrefixOnce(PREFIXES) || !preamble.matches(ParserUtil.INTEGER_REGEX)) {
            throw new ParseException(DeletePerformanceCommand.MESSAGE_INVALID_FORMAT);
        }

        try {
            Index index = ParserUtil.parseClientIndex(preamble);
            LocalDate date = ParserUtil.parseExistingRecordDate(argMultimap.getValue(PREFIX_DATE).get());
            ExerciseName exerciseName = ParserUtil.parseExerciseName(argMultimap.getValue(PREFIX_EXERCISE).get());
            return new DeletePerformanceCommand(index, date, exerciseName);
        } catch (ParseException pe) {
            throw new ParseException(Messages.withUsage(pe.getMessage(), DeletePerformanceCommand.MESSAGE_USAGE), pe);
        }
    }
}
