package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Accepts one displayed index or full name, rejecting mixed identifiers and unsupported prefixes.
 */
public class CancelSessionCommandParser implements Parser<CancelSessionCommand> {
    @Override
    public CancelSessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = " " + args.replaceAll("\\s", " ").trim();
        ParserUtil.rejectUnsupportedPrefixes(normalized, CancelSessionCommand.MESSAGE_INVALID_FORMAT, PREFIX_NAME);
        if (!normalized.trim().startsWith(PREFIX_NAME.getPrefix())) {
            return new CancelSessionCommand(ParserUtil.parseClientIndex(args,
                    CancelSessionCommand.MESSAGE_INVALID_FORMAT));
        }
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalized, PREFIX_NAME);
        if (arguments.getAllValues(PREFIX_NAME).size() != 1 || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(CancelSessionCommand.MESSAGE_INVALID_FORMAT);
        }
        String name = ParserUtil.parseSessionClientName(arguments.getValue(PREFIX_NAME).orElseThrow());
        return new CancelSessionCommand(name);
    }
}
