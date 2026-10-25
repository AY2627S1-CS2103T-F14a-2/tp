package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/**
 * Accepts one displayed index or full name, rejecting mixed identifiers and unsupported prefixes.
 */
public class CancelSessionCommandParser implements Parser<CancelSessionCommand> {
    @Override
    public CancelSessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = " " + args.replaceAll("\\s", " ").trim();
        if (!normalized.trim().startsWith(PREFIX_NAME.getPrefix())) {
            return new CancelSessionCommand(ParserUtil.parseClientIndex(args,
                    CancelSessionCommand.MESSAGE_INVALID_FORMAT));
        }
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalized, PREFIX_NAME);
        if (arguments.getAllValues(PREFIX_NAME).size() != 1 || !arguments.getPreamble().isEmpty()) {
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
