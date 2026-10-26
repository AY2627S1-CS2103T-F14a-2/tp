package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_withEmail_includesEmail() {
        assertEquals("Sarah Lim; Phone: 91234567; Email: sarah@example.com",
                Messages.format(new PersonBuilder().withName("Sarah Lim").withPhone("91234567")
                        .withEmail("sarah@example.com").withTags("rehab").build()));
    }

    @Test
    public void format_noEmail_omitsEmail() {
        assertEquals("Sarah Lim; Phone: 91234567",
                Messages.format(new PersonBuilder().withName("Sarah Lim").withPhone("91234567")
                        .withoutEmail().build()));
    }

    @Test
    public void getClientsListedMessage_usesSingularAndPluralForms() {
        assertEquals("No clients found.", Messages.getClientsListedMessage(0));
        assertEquals("Listed 1 client.", Messages.getClientsListedMessage(1));
        assertEquals("Listed 2 clients.", Messages.getClientsListedMessage(2));
        assertEquals("Listed 1000 clients.", Messages.getClientsListedMessage(1000)); // no digit grouping
    }

    @Test
    public void withUsage_appendsUsageOnNewLine() {
        assertEquals("The client index provided is invalid.\nUse: view INDEX",
                Messages.withUsage("The client index provided is invalid.", "view INDEX"));
    }
}
