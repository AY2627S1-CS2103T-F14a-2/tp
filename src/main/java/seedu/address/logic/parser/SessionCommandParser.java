package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        String name = null;
        Index index = null;
        if (byName) {
            name = ParserUtil.parseSessionClientName(arguments.getValue(PREFIX_NAME).orElseThrow());
        } else {
            index = ParserUtil.parseClientIndex(arguments.getPreamble(), SessionCommand.MESSAGE_INVALID_FORMAT);
        }
        LocalDate date = ParserUtil.parseCalendarDate(arguments.getValue(PREFIX_DATE).orElseThrow());
        LocalTime time = ParserUtil.parseSessionTime(arguments.getValue(PREFIX_TIME).orElseThrow());
        NextSession session = new NextSession(date + " " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
        return byName ? new SessionCommand(name, session) : new SessionCommand(index, session);
    }
}
