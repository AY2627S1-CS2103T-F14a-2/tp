package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.commands.SessionCommand;

/**
 * Checks exact index syntax and the distinction between invalid format and an invalid client index.
 */
public class CancelSessionCommandParserTest {
    private final CancelSessionCommandParser parser = new CancelSessionCommandParser();

    @Test
    public void parse_positiveIndex_success() {
        assertParseSuccess(parser, " 1\t ", new CancelSessionCommand(Index.fromOneBased(1)));
        assertParseSuccess(parser, "2147483647", new CancelSessionCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingOrExtraArguments_failure() {
        for (String input : List.of("", " ", "Sarah", "1 2", "1.0", "+1", "1 n/Sarah", "1 d/2026-10-22",
                "n/Sarah n/Alex", "d/2026-10-22 n/Sarah", "n/Sarah d/2026-10-22",
                "n/Sarah t/1700", "n/Sarah x/foo")) {
            assertParseFailure(parser, input, CancelSessionCommand.MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_fullName_normalizesWhitespace() {
        assertParseSuccess(parser, " n/  sArAh\t  LiM ", new CancelSessionCommand("sArAh LiM"));
    }

    @Test
    public void parse_emptyOrSlashName_rejected() {
        assertParseFailure(parser, " n/   ", SessionCommand.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, " n/Sarah/Lim", SessionCommand.MESSAGE_NAME_SLASH);
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String input : List.of("0", "-1", "2147483648", "999999999999999999999")) {
            assertParseFailure(parser, input, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }
}
