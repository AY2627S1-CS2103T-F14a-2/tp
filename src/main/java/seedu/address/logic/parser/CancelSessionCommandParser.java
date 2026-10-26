package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.regex.Matcher;

import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/**
 * Accepts exactly one of a displayed index or {@code n/NAME}, and rejects extra text or other prefixes.
 * The name form follows the same rules and messages as {@code session n/NAME}.
 */
public class CancelSessionCommandParser implements Parser<CancelSessionCommand> {
    @Override
    public CancelSessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // Same normalisation as session, so tabs separate prefixes too.
        String normalized = " " + args.replaceAll("\\s", " ").trim();
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalized, PREFIX_NAME);
        if (arguments.getValue(PREFIX_NAME).isEmpty()) {
            return new CancelSessionCommand(
                    ParserUtil.parseClientIndex(args, CancelSessionCommand.MESSAGE_INVALID_FORMAT));
        }

        Matcher matcher = SessionCommandParser.PREFIX_PATTERN.matcher(normalized);
        while (matcher.find()) {
            if (!matcher.group(1).equals(PREFIX_NAME.getPrefix())) {
                throw new ParseException(CancelSessionCommand.MESSAGE_INVALID_FORMAT);
            }
        }
        if (arguments.getAllValues(PREFIX_NAME).size() > 1 || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(CancelSessionCommand.MESSAGE_INVALID_FORMAT);
        }
        String name = Name.normalizeSpaces(arguments.getValue(PREFIX_NAME).orElseThrow());
        if (name.isEmpty()) {
            throw new ParseException(SessionCommand.MESSAGE_EMPTY_NAME);
        }
        if (name.contains("/")) {
            throw new ParseException(SessionCommand.MESSAGE_NAME_SLASH);
        }
        return new CancelSessionCommand(name);
    }
}
