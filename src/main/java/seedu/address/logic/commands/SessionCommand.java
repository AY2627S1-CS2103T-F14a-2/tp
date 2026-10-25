package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;

/**
 * Records or replaces one client's next session, identified by displayed index or full name.
 */
public class SessionCommand extends Command {
    public static final String COMMAND_WORD = "session";
    public static final String MESSAGE_USAGE = "session INDEX d/DATE t/TIME or session n/NAME d/DATE t/TIME";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format! Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX = "The client index provided is invalid.";
    public static final String MESSAGE_NO_SUCH_NAME = "No client with that name exists in Spotter.";
    public static final String MESSAGE_EMPTY_NAME = "Name cannot be empty.";
    public static final String MESSAGE_NAME_SLASH = "Name must not contain '/'.";
    public static final String MESSAGE_SUCCESS = "Recorded next session for %s on %s at %s";
    public static final String MESSAGE_PAST_SESSION =
            "Note: this session is in the past, so it shows as \"- (none upcoming)\".";

    private final Index index;
    private final String name;
    private final NextSession nextSession;

    /**
     * Identifies the client using their position in the currently displayed list.
     */
    public SessionCommand(Index index, NextSession nextSession) {
        this.index = requireNonNull(index);
        this.name = null;
        this.nextSession = requireNonNull(nextSession);
    }

    /**
     * Identifies the client by their full name, ignoring case and repeated spaces.
     */
    public SessionCommand(String name, NextSession nextSession) {
        this.index = null;
        this.name = Name.normalizeSpaces(name);
        this.nextSession = requireNonNull(nextSession);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person target;
        if (index != null) {
            if (index.getZeroBased() >= model.getFilteredPersonList().size()) {
                throw new CommandException(MESSAGE_INVALID_INDEX);
            }
            target = model.getFilteredPersonList().get(index.getZeroBased());
        } else {
            target = model.getAddressBook().getPersonList().stream()
                    .filter(person -> person.getName().matches(name))
                    .findFirst().orElseThrow(() -> new CommandException(MESSAGE_NO_SUCH_NAME));
        }
        model.setPerson(target, target.withNextSession(nextSession));
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String message = String.format(MESSAGE_SUCCESS, target.getName(), nextSession.value.toLocalDate(),
                nextSession.value.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        // Past sessions are kept for record-keeping; warn rather than block, as the booking is not upcoming.
        boolean isPast = nextSession.value.isBefore(LocalDateTime.now());
        return new CommandResult(isPast ? message + "\n" + MESSAGE_PAST_SESSION : message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof SessionCommand command
                && Objects.equals(index, command.index)
                && Objects.equals(name, command.name)
                && nextSession.equals(command.nextSession);
    }
}
