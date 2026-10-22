package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Accepts exactly one displayed index and rejects extra text or prefixes.
 */
public class CancelSessionCommandParser implements Parser<CancelSessionCommand> {
    @Override
    public CancelSessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return new CancelSessionCommand(ParserUtil.parseClientIndex(args, CancelSessionCommand.MESSAGE_INVALID_FORMAT));
    }
}
