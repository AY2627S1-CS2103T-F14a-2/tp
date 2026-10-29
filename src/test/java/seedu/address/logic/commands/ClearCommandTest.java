package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_confirmedEmptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_confirmedNonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(true), model, "All clients have been cleared.", expectedModel);
    }

    @Test
    public void execute_unconfirmedNonEmptyAddressBook_keepsClientsAndExplainsConfirmation() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandSuccess(new ClearCommand(false), model, "\"clear\" permanently deletes all clients and their "
                + "records. No clients were deleted.\nTo proceed, enter: clear confirm", expectedModel);
    }

    @Test
    public void equals() {
        ClearCommand confirmedCommand = new ClearCommand(true);

        assertTrue(confirmedCommand.equals(confirmedCommand));
        assertTrue(confirmedCommand.equals(new ClearCommand(true)));
        assertFalse(confirmedCommand.equals(new ClearCommand(false)));
        assertFalse(confirmedCommand.equals(null));
        assertFalse(confirmedCommand.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        String expected = ClearCommand.class.getCanonicalName() + "{isConfirmed=false}";
        assertEquals(expected, new ClearCommand(false).toString());
    }

}
