package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Accepts exactly one displayed index and rejects extra text or prefixes.
 */
public class CancelSessionCommandParser implements Parser<CancelSessionCommand> {
    @Override
    public CancelSessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String index = args.trim();
        if (!index.matches("[+-]?[0-9]+")) {
            throw new ParseException(CancelSessionCommand.MESSAGE_INVALID_FORMAT);
        }
        try {
            return new CancelSessionCommand(ParserUtil.parseIndex(index));
        } catch (ParseException exception) {
            throw new ParseException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }
}
