package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;

/**
 * Records or replaces one client's next session, identified by displayed index or full name.
 */
public class SessionCommand extends Command {
    // @@author gabriel-wan
    public static final String COMMAND_WORD = "session";
    public static final String MESSAGE_USAGE = "session INDEX d/DATE t/TIME or session n/NAME d/DATE t/TIME";
    public static final String MESSAGE_INVALID_FORMAT = Messages.getInvalidFormatMessage(MESSAGE_USAGE);
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
    public static final String MESSAGE_NO_SUCH_NAME = "No client with that name exists in Spotter.";
    public static final String MESSAGE_EMPTY_NAME = "Name cannot be empty.";
    public static final String MESSAGE_NAME_SLASH = "Names containing '/' cannot be used for session name lookup."
            + " Use the client's displayed index with session or cancelsession instead.";
    public static final String MESSAGE_SUCCESS = "Recorded next session for %s on %s at %s.";
    public static final String MESSAGE_RESCHEDULED = "Rescheduled next session for %s from %s to %s.";
    public static final String MESSAGE_UNCHANGED = "Next session for %s is already recorded on %s.";
    public static final String MESSAGE_CLASH = "\nWarning: This time is also booked for %s. All bookings are kept.";
    // @@author
    public static final String MESSAGE_PAST_SESSION =
            "Note: this session is in the past, so it shows as \"- (none upcoming)\".";

    private final Index index;
    private final String name;
    private final NextSession nextSession;
    private final Clock clock;

    /**
     * Identifies the client using their position in the currently displayed list.
     */
    public SessionCommand(Index index, NextSession nextSession) {
        this(index, nextSession, Clock.systemDefaultZone());
    }

    /**
     * Identifies the client by displayed index, using {@code clock} for the current time so tests can fix "now".
     */
    SessionCommand(Index index, NextSession nextSession, Clock clock) {
        this.index = requireNonNull(index);
        this.name = null;
        this.nextSession = requireNonNull(nextSession);
        this.clock = requireNonNull(clock);
    }

    /**
     * Identifies the client by their full name, ignoring case and repeated spaces.
     */
    public SessionCommand(String name, NextSession nextSession) {
        this(name, nextSession, Clock.systemDefaultZone());
    }

    /**
     * Identifies the client by full name, using {@code clock} for the current time so tests can fix "now".
     */
    SessionCommand(String name, NextSession nextSession, Clock clock) {
        this.index = null;
        this.name = Name.normalizeSpaces(name);
        this.nextSession = requireNonNull(nextSession);
        this.clock = requireNonNull(clock);
    }

    // @@author gabriel-wan
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person target = findClient(model, index, name);
        String feedback = getFeedback(target) + getClashWarning(model, target);
        model.setPerson(target, target.withNextSession(nextSession));
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        // @@author
        // Past sessions are kept for record-keeping; warn rather than block, as the booking is not upcoming.
        // Same rule as the card: a session exactly at "now" has not passed yet.
        if (nextSession.value.isBefore(LocalDateTime.now(clock))) {
            feedback += "\n" + MESSAGE_PAST_SESSION;
        }
        return new CommandResult(feedback, false, false, true);
    }

    /**
     * Identifies a replacement explicitly so a trainer can check which booking was removed.
     */
    private String getFeedback(Person target) {
        if (target.getNextSession().isEmpty()) {
            return String.format(MESSAGE_SUCCESS, target.getName(), nextSession.value.toLocalDate(),
                    nextSession.value.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        NextSession previous = target.getNextSession().orElseThrow();
        if (previous.equals(nextSession)) {
            return String.format(MESSAGE_UNCHANGED, target.getName(), nextSession);
        }
        return String.format(MESSAGE_RESCHEDULED, target.getName(), previous, nextSession);
    }

    /**
     * Resolves a displayed index or normalized full name for both booking and cancellation.
     * Indices use the current displayed list; names search all clients regardless of its filter.
     */
    static Person findClient(Model model, Index index, String name) throws CommandException {
        assert (index == null) != (name == null) : "Exactly one client identifier must be supplied";
        if (index != null) {
            if (index.getZeroBased() >= model.getFilteredPersonList().size()) {
                throw new CommandException(MESSAGE_INVALID_INDEX);
            }
            return model.getFilteredPersonList().get(index.getZeroBased());
        }
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().matches(name))
                .findFirst().orElseThrow(() -> new CommandException(MESSAGE_NO_SUCH_NAME));
    }

    /**
     * Warns about exact-time clashes across all clients, including those outside the displayed filter.
     * Session duration is not recorded, so this does not attempt to infer overlapping time ranges.
     */
    private String getClashWarning(Model model, Person target) {
        String names = model.getAddressBook().getPersonList().stream()
                .filter(person -> !person.isSamePerson(target))
                .filter(person -> person.getNextSession().filter(nextSession::equals).isPresent())
                .map(person -> person.getName().fullName)
                .sorted(Name.alphabeticalComparator())
                .collect(Collectors.joining(", "));
        return names.isEmpty() ? "" : String.format(MESSAGE_CLASH, names);
    }

    // @@author gabriel-wan
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
    // @@author
}
