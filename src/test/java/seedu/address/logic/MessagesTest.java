package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void withUsage_appendsUsageOnNewLine() {
        assertEquals("The client index provided is invalid.\nUse: view INDEX",
                Messages.withUsage("The client index provided is invalid.", "view INDEX"));
    }
}
