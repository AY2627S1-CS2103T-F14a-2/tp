package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteMeasureCommand;
import seedu.address.logic.commands.DeletePerformanceCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MeasureCommand;
import seedu.address.logic.commands.PerformanceCommand;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.commands.SessionsCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        // Command words ignore case so that fast typists aren't slowed by an accidental Caps Lock or Shift
        final String commandWord = matcher.group("commandWord").toLowerCase(Locale.ROOT);
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD -> new AddCommandParser().parse(arguments);
            case EditCommand.COMMAND_WORD -> new EditCommandParser().parse(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case DeletePerformanceCommand.COMMAND_WORD -> new DeletePerformanceCommandParser().parse(arguments);
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            case MeasureCommand.COMMAND_WORD -> new MeasureCommandParser().parse(arguments);
            case DeleteMeasureCommand.COMMAND_WORD -> new DeleteMeasureCommandParser().parse(arguments);
            case ClearCommand.COMMAND_WORD -> parseClearCommand(arguments);
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case FilterCommand.COMMAND_WORD -> new FilterCommandParser().parse(arguments);
            case SessionCommand.COMMAND_WORD -> new SessionCommandParser().parse(arguments);
            case CancelSessionCommand.COMMAND_WORD -> new CancelSessionCommandParser().parse(arguments);
            case SessionsCommand.COMMAND_WORD -> new SessionsCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> parseListCommand(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            case PerformanceCommand.COMMAND_WORD -> new PerformanceCommandParser().parse(arguments);
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

    /**
     * Returns a {@code ListCommand}, rejecting any non-blank text after the command word.
     */
    private ListCommand parseListCommand(String arguments) throws ParseException {
        if (!arguments.isBlank()) {
            throw new ParseException(ListCommand.MESSAGE_EXTRA_PARAMETERS);
        }
        return new ListCommand();
    }

    /**
     * Returns a confirmed {@code ClearCommand} for {@code confirm} (any case) and an unconfirmed one for blank
     * arguments, rejecting accidental indices or other arguments before a destructive clear can execute.
     */
    private ClearCommand parseClearCommand(String arguments) throws ParseException {
        if (arguments.isBlank()) {
            return new ClearCommand(false);
        }
        if (arguments.strip().equalsIgnoreCase(ClearCommand.CONFIRM_KEYWORD)) {
            return new ClearCommand(true);
        }
        throw new ParseException(ClearCommand.MESSAGE_EXTRA_PARAMETERS);
    }

}
