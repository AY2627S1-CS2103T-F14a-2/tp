package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXERCISE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOAD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REPS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SETS;

import java.time.LocalDate;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
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

    // An index that looks like an integer is reported as an invalid index rather than an invalid format.
    private static final String INTEGER_REGEX = "[+-]?\\d+";

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
        boolean hasEachPrefixOnce = Stream.of(PREFIXES)
                .allMatch(prefix -> argMultimap.getAllValues(prefix).size() == 1);
        if (!hasEachPrefixOnce || !preamble.matches(INTEGER_REGEX)) {
            throw new ParseException(PerformanceCommand.MESSAGE_INVALID_FORMAT);
        }

        try {
            Index index = parseIndex(preamble);
            LocalDate date = ParserUtil.parseRecordDate(argMultimap.getValue(PREFIX_DATE).get(),
                    PerformanceCommand.MESSAGE_FUTURE_DATE);
            ExerciseName exerciseName = parseExerciseName(argMultimap.getValue(PREFIX_EXERCISE).get());
            int sets = parseSets(argMultimap.getValue(PREFIX_SETS).get());
            int reps = parseReps(argMultimap.getValue(PREFIX_REPS).get());
            Load load = parseLoad(argMultimap.getValue(PREFIX_LOAD).get());
            return new PerformanceCommand(index, new GymPerformance(date, exerciseName, sets, reps, load));
        } catch (ParseException pe) {
            throw new ParseException(PerformanceCommand.withUsage(pe.getMessage()), pe);
        }
    }

    private static Index parseIndex(String index) throws ParseException {
        try {
            return ParserUtil.parseIndex(index);
        } catch (ParseException pe) {
            // Zero, negative and overflowing indexes
            throw new ParseException(PerformanceCommand.MESSAGE_INVALID_INDEX, pe);
        }
    }

    private static ExerciseName parseExerciseName(String exerciseName) throws ParseException {
        if (!ExerciseName.isValidExerciseName(exerciseName)) {
            throw new ParseException(ExerciseName.MESSAGE_CONSTRAINTS);
        }
        return new ExerciseName(exerciseName);
    }

    private static int parseSets(String sets) throws ParseException {
        String trimmedSets = sets.trim();
        if (!GymPerformance.isValidSets(trimmedSets)) {
            throw new ParseException(GymPerformance.MESSAGE_SETS_CONSTRAINTS);
        }
        return Integer.parseInt(trimmedSets);
    }

    private static int parseReps(String reps) throws ParseException {
        String trimmedReps = reps.trim();
        if (!GymPerformance.isValidReps(trimmedReps)) {
            throw new ParseException(GymPerformance.MESSAGE_REPS_CONSTRAINTS);
        }
        return Integer.parseInt(trimmedReps);
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
