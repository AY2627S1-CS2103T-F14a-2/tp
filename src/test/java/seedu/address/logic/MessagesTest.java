package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_withEmail_includesEmail() {
        assertEquals("Sarah Lim; Phone: 9123 4567; Email: sarah@example.com",
                Messages.format(new PersonBuilder().withName("Sarah Lim").withPhone("91234567")
                        .withEmail("sarah@example.com").withTags("rehab").build()));
    }

    @Test
    public void format_noEmail_omitsEmail() {
        assertEquals("Sarah Lim; Phone: 9123 4567",
                Messages.format(new PersonBuilder().withName("Sarah Lim").withPhone("91234567")
                        .withoutEmail().build()));
    }

    @Test
    public void format_phoneNotEightDigits_showsPhoneUnchanged() {
        assertEquals("Sarah Lim; Phone: 911",
                Messages.format(new PersonBuilder().withName("Sarah Lim").withPhone("911").withoutEmail().build()));
    }

    @Test
    public void getInvalidFormatMessage_usage_putsUsageOnNewLine() {
        assertEquals("Invalid command format!\nUse: sessions d/DATE",
                Messages.getInvalidFormatMessage("sessions d/DATE"));
        assertEquals(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "x").split("\n")[0],
                Messages.getInvalidFormatMessage("x").split("\n")[0]);
    }

    @Test
    public void getClientsListedMessage_usesSingularAndPluralForms() {
        assertEquals("No clients found.", Messages.getClientsListedMessage(0));
        assertEquals("Listed 1 client.", Messages.getClientsListedMessage(1));
        assertEquals("Listed 2 clients.", Messages.getClientsListedMessage(2));
        assertEquals("Listed 1000 clients.", Messages.getClientsListedMessage(1000)); // no digit grouping
    }

    @Test
    public void getClientsListedMessage_negativeCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Messages.getClientsListedMessage(-1));
    }

    @Test
    public void withUsage_appendsUsageOnNewLine() {
        assertEquals("The client index provided is invalid.\nUse: view INDEX",
                Messages.withUsage("The client index provided is invalid.", "view INDEX"));
    }
}
