package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} contains any keyword as a case-insensitive substring.
 * Matching is independent of the system locale.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final List<String> lowercaseKeywords;

    /**
     * Constructs a predicate that matches names containing any of the given keywords.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
        this.lowercaseKeywords = keywords.stream()
                .map(keyword -> keyword.toLowerCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean test(Person person) {
        String lowercaseName = person.getName().fullName.toLowerCase(Locale.ROOT);
        return lowercaseKeywords.stream().anyMatch(lowercaseName::contains);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
