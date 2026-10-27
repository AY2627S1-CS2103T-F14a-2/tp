package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.getInArabicFormatLocale;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterCommand}.
 */
public class FilterCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        TagContainsKeywordsPredicate firstPredicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        TagContainsKeywordsPredicate secondPredicate = new TagContainsKeywordsPredicate(List.of("strength"));

        FilterCommand firstCommand = new FilterCommand(firstPredicate);
        FilterCommand secondCommand = new FilterCommand(secondPredicate);

        // same object -> returns true
        assertTrue(firstCommand.equals(firstCommand));

        // same values -> returns true
        assertTrue(firstCommand.equals(new FilterCommand(firstPredicate)));

        // different types -> returns false
        assertFalse(firstCommand.equals(1));

        // null -> returns false
        assertFalse(firstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(firstCommand.equals(secondCommand));
    }

    @Test
    public void execute_noMatchingTag_noClientFound() {
        assertFilterSuccess(List.of("rehab"), List.of());
    }

    @Test
    public void execute_singleTagDifferentCase_multipleClientsFound() {
        assertFilterSuccess(List.of("FRIENDS"), List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void execute_multipleTags_clientsWithAnyTagFound() {
        assertFilterSuccess(List.of("owesMoney", "rehab"), List.of(BENSON));
    }

    @Test
    public void execute_threeTags_clientsWithAnyTagFound() {
        assertFilterSuccess(List.of("friends", "owesMoney", "rehab"), List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void getResultMessage_exactWording() {
        assertEquals("No clients tagged rehab. Use \"list\" to show all clients.",
                FilterCommand.getResultMessage(0, List.of("rehab")));
        assertEquals("Listed 1 client tagged owesMoney or rehab.",
                FilterCommand.getResultMessage(1, List.of("owesMoney", "rehab")));
        assertEquals("Listed 3 clients tagged friends, owesMoney or rehab.",
                FilterCommand.getResultMessage(3, List.of("friends", "owesMoney", "rehab")));
    }

    @Test
    public void joinTags() {
        assertEquals("rehab", FilterCommand.joinTags(List.of("rehab")));
        assertEquals("rehab or knee", FilterCommand.joinTags(List.of("rehab", "knee")));
        assertEquals("rehab, knee or strength", FilterCommand.joinTags(List.of("rehab", "knee", "strength")));
    }

    @Test
    public void execute_arabicFormatLocale_usesLatinDigits() {
        FilterCommand command = new FilterCommand(new TagContainsKeywordsPredicate(List.of("friends")));
        assertEquals("Listed 3 clients tagged friends.",
                getInArabicFormatLocale(() -> command.execute(model).getFeedbackToUser()));
    }

    @Test
    public void toStringMethod() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }

    /**
     * Filters the typical clients by {@code tags} and confirms that exactly {@code expectedClients} are shown,
     * with the message naming the tags and the list scrolled to the top.
     */
    private void assertFilterSuccess(List<String> tags, List<Person> expectedClients) {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(tags);
        expectedModel.updateFilteredPersonList(predicate);
        CommandResult expectedResult = new CommandResult(
                FilterCommand.getResultMessage(expectedClients.size(), tags), false, false, true);
        assertCommandSuccess(new FilterCommand(predicate), model, expectedResult, expectedModel);
        assertEquals(expectedClients, model.getFilteredPersonList());
    }
}
