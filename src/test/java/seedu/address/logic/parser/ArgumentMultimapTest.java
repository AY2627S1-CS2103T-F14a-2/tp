package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXERCISE;

import org.junit.jupiter.api.Test;

public class ArgumentMultimapTest {

    @Test
    public void hasEachPrefixOnce() {
        // each prefix once -> returns true
        assertTrue(tokenize(" d/2026-09-18 e/Squat").hasEachPrefixOnce(PREFIX_DATE, PREFIX_EXERCISE));

        // no prefixes to check -> returns true
        assertTrue(tokenize("").hasEachPrefixOnce());

        // a prefix is missing -> returns false
        assertFalse(tokenize(" d/2026-09-18").hasEachPrefixOnce(PREFIX_DATE, PREFIX_EXERCISE));

        // a prefix is repeated -> returns false
        assertFalse(tokenize(" d/2026-09-18 e/Squat e/Lunge").hasEachPrefixOnce(PREFIX_DATE, PREFIX_EXERCISE));

        // a prefix is given with an empty value -> still counts as present
        assertTrue(tokenize(" d/ e/Squat").hasEachPrefixOnce(PREFIX_DATE, PREFIX_EXERCISE));
    }

    private static ArgumentMultimap tokenize(String args) {
        return ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_EXERCISE);
    }
}
