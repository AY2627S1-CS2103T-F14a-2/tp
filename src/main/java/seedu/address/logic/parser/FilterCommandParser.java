package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_EMPTY_TAG = "Tags to filter by cannot be empty.\n"
            + FilterCommand.MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_TAG = Tag.MESSAGE_CONSTRAINTS + "\n"
            + FilterCommand.MESSAGE_USAGE;

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        List<String> tagKeywords = argMultimap.getAllValues(PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty() || tagKeywords.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        if (tagKeywords.stream().anyMatch(String::isBlank)) {
            throw new ParseException(MESSAGE_EMPTY_TAG);
        }

        // A tag that add/edit would reject can never match, so report it instead of showing an empty list
        List<String> trimmedTags = tagKeywords.stream().map(String::trim).toList();
        if (!trimmedTags.stream().allMatch(Tag::isValidTagName)) {
            throw new ParseException(MESSAGE_INVALID_TAG);
        }

        return new FilterCommand(new TagContainsKeywordsPredicate(trimmedTags));
    }
}
