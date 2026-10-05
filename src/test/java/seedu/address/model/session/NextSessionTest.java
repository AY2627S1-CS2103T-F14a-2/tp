package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NextSessionTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NextSession(null));
    }

    @Test
    public void constructor_invalidNextSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new NextSession("2026-02-30 18:00"));
    }

    @Test
    public void isValidNextSession() {
        assertThrows(NullPointerException.class, () -> NextSession.isValidNextSession(null));

        // invalid dates and times
        assertFalse(NextSession.isValidNextSession(""));
        assertFalse(NextSession.isValidNextSession("2026-02-30 18:00"));
        assertFalse(NextSession.isValidNextSession("2026-10-08 24:00"));
        assertFalse(NextSession.isValidNextSession("08-10-2026 18:00"));
        assertFalse(NextSession.isValidNextSession("2026-10-08"));
        assertFalse(NextSession.isValidNextSession(" 2026-10-08 18:00"));

        // valid dates and times
        assertTrue(NextSession.isValidNextSession("2026-10-08 18:00"));
        assertTrue(NextSession.isValidNextSession("2028-02-29 09:05"));
        assertTrue(NextSession.isValidNextSession("2026-01-01 00:00"));
        assertTrue(NextSession.isValidNextSession("2026-12-31 23:59"));
    }

    @Test
    public void toString_returnsRequiredFormat() {
        NextSession nextSession = new NextSession("2026-10-08 09:05");
        assertEquals("2026-10-08 09:05", nextSession.toString());
    }

    @Test
    public void equals() {
        NextSession nextSession = new NextSession("2026-10-08 18:00");

        assertTrue(nextSession.equals(new NextSession("2026-10-08 18:00")));
        assertTrue(nextSession.equals(nextSession));
        assertFalse(nextSession.equals(null));
        assertFalse(nextSession.equals("2026-10-08 18:00"));
        assertFalse(nextSession.equals(new NextSession("2026-10-09 18:00")));
    }
}
