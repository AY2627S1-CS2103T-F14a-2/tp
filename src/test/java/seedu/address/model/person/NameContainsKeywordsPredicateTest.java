package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void hashCode_consistentWithEquals() {
        NameContainsKeywordsPredicate first = new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate same = new NameContainsKeywordsPredicate(List.of("first"));

        assertEquals(first.hashCode(), same.hashCode());
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameContainsPartialKeyword_returnsTrue() {
        Person person = new PersonBuilder().withName("Hans Gruber").build();
        for (String keyword : List.of("han", "HAN", "an", "ANS", "grub", "BER")) {
            NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of(keyword));
            assertTrue(predicate.test(person), keyword);
        }
    }

    @Test
    public void test_onlyOnePartialKeywordMatches_returnsTrue() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("xyz", "HAN"));
        assertTrue(predicate.test(new PersonBuilder().withName("Hans Gruber").build()));
    }

    @Test
    public void test_turkishDefaultLocale_matchesIndependentlyOfLocale() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            NameContainsKeywordsPredicate lowercasePredicate = new NameContainsKeywordsPredicate(List.of("is"));
            assertTrue(lowercasePredicate.test(new PersonBuilder().withName("ISABELLE").build()));
            NameContainsKeywordsPredicate uppercasePredicate = new NameContainsKeywordsPredicate(List.of("IS"));
            assertTrue(uppercasePredicate.test(new PersonBuilder().withName("Isabelle").build()));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void test_equivalentAccentEncodings_returnsTrue() {
        String composed = "José";
        String decomposed = "José";
        Person storedComposed = new PersonBuilder().withName(composed + " Tan").build();
        Person storedFromDecomposed = new PersonBuilder().withName(decomposed + " Tan").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of(decomposed)).test(storedComposed));
        assertTrue(new NameContainsKeywordsPredicate(List.of(composed)).test(storedFromDecomposed));
        assertTrue(new NameContainsKeywordsPredicate(List.of("JOSÉ")).test(storedComposed));
    }

    @Test
    public void test_keywordWithoutAccent_returnsFalse() {
        Person person = new PersonBuilder().withName("José Tan").build();
        assertFalse(new NameContainsKeywordsPredicate(List.of("jose")).test(person));
    }

    @Test
    public void test_keywordMatchesOnlyOtherFields_returnsFalse() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("123", "email", "friend"));
        assertFalse(predicate.test(new PersonBuilder().withName("Hans Gruber").withPhone("12345")
                .withEmail("alice@email.com").withTags("friends").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
