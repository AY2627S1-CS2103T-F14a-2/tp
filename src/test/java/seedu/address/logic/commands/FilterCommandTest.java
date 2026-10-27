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

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
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
    public void execute_noMatchingTag_noPersonFound() {
        String expectedMessage = "No clients tagged rehab. Use \"list\" to show all clients.";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleTagDifferentCase_multiplePersonsFound() {
        String expectedMessage = "Listed 3 clients tagged FRIENDS.";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("FRIENDS"));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(List.of(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleTags_personsWithAnyTagFound() {
        String expectedMessage = "Listed 1 client tagged owesMoney or rehab.";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("owesMoney", "rehab"));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_threeTags_namesAllTags() {
        String expectedMessage = "Listed 3 clients tagged friends, owesMoney or rehab.";
        TagContainsKeywordsPredicate predicate =
                new TagContainsKeywordsPredicate(List.of("friends", "owesMoney", "rehab"));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(List.of(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
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
}
