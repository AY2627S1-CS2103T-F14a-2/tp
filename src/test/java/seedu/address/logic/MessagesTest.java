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
}
