package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXERCISE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOAD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REPS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SETS;

import java.time.LocalDate;
import java.util.function.Predicate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.PerformanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * Parses input arguments and creates a new {@code PerformanceCommand} object.
 *
 * <p>A missing, repeated or non-numeric part of the command gives the invalid-format message.
 * Any other invalid value gives its specific message, followed by the expected command format.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this parser and its tests.
 */
public class PerformanceCommandParser implements Parser<PerformanceCommand> {

    private static final Prefix[] PREFIXES = {PREFIX_DATE, PREFIX_EXERCISE, PREFIX_SETS, PREFIX_REPS, PREFIX_LOAD};

    /**
     * Parses the given {@code String} of arguments in the context of the {@code PerformanceCommand}
     * and returns a {@code PerformanceCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format or has an invalid value.
     */
    @Override
    public PerformanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIXES);

        String preamble = argMultimap.getPreamble();
        if (!argMultimap.hasEachPrefixOnce(PREFIXES) || !preamble.matches(ParserUtil.INTEGER_REGEX)) {
            throw new ParseException(PerformanceCommand.MESSAGE_INVALID_FORMAT);
        }

        try {
            Index index = ParserUtil.parseClientIndex(preamble);
            LocalDate date = ParserUtil.parseRecordDate(argMultimap.getValue(PREFIX_DATE).get(),
                    PerformanceCommand.MESSAGE_FUTURE_DATE);
            ExerciseName exerciseName = ParserUtil.parseExerciseName(argMultimap.getValue(PREFIX_EXERCISE).get());
            int sets = parseCount(argMultimap.getValue(PREFIX_SETS).get(), GymPerformance::isValidSets,
                    GymPerformance.MESSAGE_SETS_CONSTRAINTS);
            int reps = parseCount(argMultimap.getValue(PREFIX_REPS).get(), GymPerformance::isValidReps,
                    GymPerformance.MESSAGE_REPS_CONSTRAINTS);
            Load load = parseLoad(argMultimap.getValue(PREFIX_LOAD).get());
            return new PerformanceCommand(index, new GymPerformance(date, exerciseName, sets, reps, load));
        } catch (ParseException pe) {
            throw new ParseException(Messages.withUsage(pe.getMessage(), PerformanceCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses a whole-number count, such as the number of sets, after checking it with {@code isValid}.
     */
    private static int parseCount(String count, Predicate<String> isValid, String constraintsMessage)
            throws ParseException {
        String trimmedCount = count.trim();
        if (!isValid.test(trimmedCount)) {
            throw new ParseException(constraintsMessage);
        }
        return Integer.parseInt(trimmedCount);
    }

    private static Load parseLoad(String load) throws ParseException {
        try {
            // Load reports whether the value is not numeric, out of range or too precise
            return new Load(load.trim());
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
