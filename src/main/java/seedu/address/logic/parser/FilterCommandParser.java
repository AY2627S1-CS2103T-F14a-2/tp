package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_EMPTY_TAG =
            Messages.withUsage("Tags to filter by cannot be empty.", FilterCommand.MESSAGE_USAGE);
    public static final String MESSAGE_INVALID_TAG =
            Messages.withUsage(Tag.MESSAGE_CONSTRAINTS, FilterCommand.MESSAGE_USAGE);

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        List<String> tagKeywords = argMultimap.getAllValues(PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty() || tagKeywords.isEmpty()) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_FORMAT);
        }

        if (tagKeywords.stream().anyMatch(String::isBlank)) {
            throw new ParseException(MESSAGE_EMPTY_TAG);
        }

        // A tag that add/edit would reject can never match, so report it instead of showing an empty list
        List<String> trimmedTags = tagKeywords.stream().map(String::trim).toList();
        if (!trimmedTags.stream().allMatch(Tag::isValidTagName)) {
            throw new ParseException(MESSAGE_INVALID_TAG);
        }

        // Repeated tags would only repeat themselves in the result message, so keep the first spelling of each
        List<String> uniqueTags = new ArrayList<>();
        for (String tag : trimmedTags) {
            if (uniqueTags.stream().noneMatch(tag::equalsIgnoreCase)) {
                uniqueTags.add(tag);
            }
        }
        return new FilterCommand(new TagContainsKeywordsPredicate(uniqueTags));
    }
}
