package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.LocalTime;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.NextSession;

/**
 * Parses the two session command forms and rejects missing, duplicate, and unknown prefixes.
 */
public class SessionCommandParser implements Parser<SessionCommand> {
    @Override
    public SessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // AB3's tokenizer recognizes spaces; normalize tabs as well, without changing name case.
        String normalized = " " + args.replaceAll("\\s", " ").trim();
        ParserUtil.rejectUnsupportedPrefixes(normalized, SessionCommand.MESSAGE_INVALID_FORMAT,
                PREFIX_NAME, PREFIX_DATE, PREFIX_TIME);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalized, PREFIX_NAME, PREFIX_DATE, PREFIX_TIME);
        if (arguments.getAllValues(PREFIX_DATE).size() != 1 || arguments.getAllValues(PREFIX_TIME).size() != 1
                || arguments.getAllValues(PREFIX_NAME).size() > 1) {
            throw new ParseException(SessionCommand.MESSAGE_INVALID_FORMAT);
        }
        boolean byName = arguments.getValue(PREFIX_NAME).isPresent();
        if ((byName && !arguments.getPreamble().isEmpty()) || (!byName && arguments.getPreamble().isEmpty())) {
            throw new ParseException(SessionCommand.MESSAGE_INVALID_FORMAT);
        }
        if (byName) {
            String name = ParserUtil.parseSessionClientName(arguments.getValue(PREFIX_NAME).orElseThrow());
            return new SessionCommand(name, parseNextSession(arguments));
        }
        Index index = ParserUtil.parseClientIndex(arguments.getPreamble(), SessionCommand.MESSAGE_INVALID_FORMAT);
        return new SessionCommand(index, parseNextSession(arguments));
    }

    /** Builds the stored, minute-precision session after validating its calendar date and time. */
    private NextSession parseNextSession(ArgumentMultimap arguments) throws ParseException {
        LocalDate date = ParserUtil.parseCalendarDate(arguments.getValue(PREFIX_DATE).orElseThrow());
        LocalTime time = ParserUtil.parseSessionTime(arguments.getValue(PREFIX_TIME).orElseThrow());
        return new NextSession(date + " " + time);
    }
}
