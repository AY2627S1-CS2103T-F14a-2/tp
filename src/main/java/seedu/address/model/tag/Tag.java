package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 * Tag identity ignores case; the supplied spelling is retained for display and storage.
 * AI assistance: Helen used OpenAI Codex for issue #222's tag rules and regression tests.
 */
public class Tag {

    public static final int MAX_LENGTH = 50;
    public static final String MESSAGE_EMPTY = "Tag names cannot be empty. Omit t/ to add a client without tags.";
    public static final String MESSAGE_CONSTRAINTS = "Tag names must be 1 to " + MAX_LENGTH
            + " characters: English letters and digits, with single hyphens between them (e.g. lower-back).";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9]+(-[A-Za-z0-9]+)*";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName;
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equalsIgnoreCase(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.toLowerCase(Locale.ROOT).hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
