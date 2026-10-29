package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Lists all clients who have at least one of the given tags.
 * Tag matching is case-insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " t/TAG [t/MORE_TAGS]...";
    public static final String MESSAGE_INVALID_FORMAT = Messages.getInvalidFormatMessage(MESSAGE_USAGE)
            + "\nExample: " + COMMAND_WORD + " t/rehab t/strength";
    public static final String MESSAGE_NO_MATCHES = "No clients tagged %1$s. Use \"list\" to show all clients.";
    public static final String MESSAGE_ONE_MATCH = "Listed 1 client tagged %1$s.";
    public static final String MESSAGE_MATCHES = "Listed %1$d clients tagged %2$s.";

    private final TagContainsKeywordsPredicate predicate;

    /**
     * Creates a FilterCommand that filters the client list using the given predicate.
     */
    public FilterCommand(TagContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        // Scrolls to the top like list and sessions, so the first match is visible
        return new CommandResult(getResultMessage(model.getFilteredPersonList().size(), predicate.getKeywords()),
                false, false, true);
    }

    /**
     * Returns the feedback for {@code clientCount} clients matching {@code tags}, naming the tags.
     */
    static String getResultMessage(int clientCount, List<String> tags) {
        assert clientCount >= 0;
        String tagText = joinTags(tags);
        if (clientCount == 0) {
            return String.format(MESSAGE_NO_MATCHES, tagText);
        }
        if (clientCount == 1) {
            return String.format(MESSAGE_ONE_MATCH, tagText);
        }
        // Locale.ROOT keeps the count in ASCII digits whatever the system locale is
        return String.format(Locale.ROOT, MESSAGE_MATCHES, clientCount, tagText);
    }

    /**
     * Joins tags for a message: "rehab", "rehab or knee", "rehab, knee or strength".
     */
    static String joinTags(List<String> tags) {
        assert !tags.isEmpty();
        if (tags.size() == 1) {
            return tags.get(0);
        }
        return String.join(", ", tags.subList(0, tags.size() - 1)) + " or " + tags.get(tags.size() - 1);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
