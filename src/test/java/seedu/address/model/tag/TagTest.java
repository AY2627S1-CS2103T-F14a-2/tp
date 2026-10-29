package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TagTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "7", "Rehab", "lower-back", "a-b-c", "Level-2"})
    public void isValidTagName_supportedLabels_returnsTrue(String label) {
        assertTrue(Tag.isValidTagName(label));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "-rehab", "rehab-", "lower--back", "lower back", "rehab!", "é", "中"})
    public void isValidTagName_invalidLabels_returnsFalse(String label) {
        assertFalse(Tag.isValidTagName(label));
        assertThrows(IllegalArgumentException.class, () -> new Tag(label));
    }

    @Test
    public void isValidTagName_lengthBoundary() {
        assertTrue(Tag.isValidTagName("a".repeat(50)));
        assertFalse(Tag.isValidTagName("a".repeat(51)));
        assertThrows(IllegalArgumentException.class, () -> new Tag("a".repeat(51)));
    }

    @Test
    public void equals_caseVariants_haveEqualHashesAndKeepSpelling() {
        Tag first = new Tag("Lower-Back");
        Tag second = new Tag("lower-back");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals("Lower-Back", first.tagName);
        assertFalse(first.equals(new Tag("lowerback")));
        assertFalse(first.equals(null));
        assertFalse(first.equals("Lower-Back"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

}
