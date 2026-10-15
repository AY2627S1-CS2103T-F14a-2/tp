package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class NextSessionTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NextSession(null));
    }

    @Test
    public void constructor_invalidNextSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new NextSession("2026-02-30 18:00"));
        assertThrows(IllegalArgumentException.class, () -> new NextSession("-2026-10-08 18:00"));
        assertThrows(IllegalArgumentException.class, () -> new NextSession("+12026-10-08 18:00"));
        assertThrows(IllegalArgumentException.class, () -> new NextSession("0000-01-01 00:00"));
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
        assertFalse(NextSession.isValidNextSession("-2026-10-08 18:00"));
        assertFalse(NextSession.isValidNextSession("+12026-10-08 18:00"));
        assertFalse(NextSession.isValidNextSession("0000-01-01 00:00"));

        // valid dates and times
        assertTrue(NextSession.isValidNextSession("2026-10-08 18:00"));
        assertTrue(NextSession.isValidNextSession("2028-02-29 09:05"));
        assertTrue(NextSession.isValidNextSession("2026-01-01 00:00"));
        assertTrue(NextSession.isValidNextSession("2026-12-31 23:59"));
        assertTrue(NextSession.isValidNextSession("0001-01-01 00:00"));
        assertTrue(NextSession.isValidNextSession("9999-12-31 23:59"));
    }

    @Test
    public void toString_returnsRequiredFormat() {
        NextSession nextSession = new NextSession("2026-10-08 09:05");
        assertEquals("2026-10-08 09:05", nextSession.toString());
    }

    @Test
    public void toDisplayString_englishDateAndTwelveHourTime() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        assertEquals("Wed 18 Sep, 7:00pm", new NextSession("2030-09-18 19:00").toDisplayString(now));
        assertEquals("Wed 18 Sep, 12:00am", new NextSession("2030-09-18 00:00").toDisplayString(now));
        assertEquals("Wed 18 Sep, 12:05pm", new NextSession("2030-09-18 12:05").toDisplayString(now));
        assertEquals("Wed 18 Sep, 9:05am", new NextSession("2030-09-18 09:05").toDisplayString(now));
    }

    @Test
    public void toDisplayString_atSessionTimeAndJustAfter_distinguishesExpiry() {
        NextSession session = new NextSession("2030-09-18 19:00");
        assertEquals("Wed 18 Sep, 7:00pm", session.toDisplayString(session.value));
        assertEquals("- (none upcoming)", session.toDisplayString(session.value.plusSeconds(1)));
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
