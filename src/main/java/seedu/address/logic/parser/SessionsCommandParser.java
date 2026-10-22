package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.SessionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses one date prefix; viewing retained sessions permits past dates as well as today and future dates.
 */
public class SessionsCommandParser implements Parser<SessionsCommand> {
    // Matching the entire argument rejects repeated/unknown prefixes and stray text.
    private static final Pattern ARGUMENT_FORMAT = Pattern.compile(Pattern.quote(PREFIX_DATE.getPrefix())
            + "\\s*(\\S*)");

    @Override
    public SessionsCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = ARGUMENT_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(SessionsCommand.MESSAGE_INVALID_FORMAT);
        }
        return new SessionsCommand(ParserUtil.parseCalendarDate(matcher.group(1)));
    }
}
