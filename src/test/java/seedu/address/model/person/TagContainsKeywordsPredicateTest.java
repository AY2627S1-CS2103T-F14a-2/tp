package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TagContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstKeywords = Collections.singletonList("rehab");
        List<String> secondKeywords = List.of("rehab", "strength");

        TagContainsKeywordsPredicate firstPredicate = new TagContainsKeywordsPredicate(firstKeywords);
        TagContainsKeywordsPredicate secondPredicate = new TagContainsKeywordsPredicate(secondKeywords);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordsPredicate firstPredicateCopy = new TagContainsKeywordsPredicate(firstKeywords);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_personHasMatchingTag_returnsTrue() {
        // One keyword
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        assertTrue(predicate.test(new PersonBuilder().withTags("rehab").build()));

        // Multiple keywords, only one matches (OR semantics)
        predicate = new TagContainsKeywordsPredicate(List.of("rehab", "strength"));
        assertTrue(predicate.test(new PersonBuilder().withTags("strength").build()));

        // Mixed-case keyword
        predicate = new TagContainsKeywordsPredicate(List.of("rEhAb"));
        assertTrue(predicate.test(new PersonBuilder().withTags("Rehab").build()));

        // Person has multiple tags, only one matches
        predicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        assertTrue(predicate.test(new PersonBuilder().withTags("strength", "rehab").build()));
    }

    @Test
    public void test_personHasNoMatchingTag_returnsFalse() {
        // Zero keywords
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withTags("rehab").build()));

        // Non-matching keyword
        predicate = new TagContainsKeywordsPredicate(List.of("strength"));
        assertFalse(predicate.test(new PersonBuilder().withTags("rehab").build()));

        // Partial tag name does not match
        predicate = new TagContainsKeywordsPredicate(List.of("reh"));
        assertFalse(predicate.test(new PersonBuilder().withTags("rehab").build()));

        // Person has no tags
        predicate = new TagContainsKeywordsPredicate(List.of("rehab"));
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));

        // Keyword matches name but not tag
        predicate = new TagContainsKeywordsPredicate(List.of("Rehab"));
        assertFalse(predicate.test(new PersonBuilder().withName("Rehab").withTags("strength").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("rehab", "strength");
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);

        String expected = TagContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
