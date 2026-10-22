package seedu.address.model.progress;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents all gym-performance records of one client, in the order they were recorded.
 * Guarantees: immutable; contains no two records for the same exercise on the same date
 * (see {@link GymPerformance#isSameEntry(GymPerformance)}).
 *
 * <p>The recording order is kept so that, among records on the same date, the one recorded last
 * is treated as the latest.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this class and its tests.
 */
public final class PerformanceHistory {

    public static final String MESSAGE_DUPLICATE_RECORDS =
            "A client cannot have two performance records for the same exercise on the same date.";

    // Unmodifiable, as it is created with List.of or List.copyOf.
    private final List<GymPerformance> records;

    /**
     * Constructs an empty {@code PerformanceHistory}.
     */
    public PerformanceHistory() {
        records = List.of();
    }

    /**
     * Constructs a {@code PerformanceHistory} with the given records, oldest recorded first.
     *
     * @param records Records with no duplicates, as declared in {@link #hasNoDuplicates(List)}.
     */
    public PerformanceHistory(List<GymPerformance> records) {
        requireAllNonNull(records);
        checkArgument(hasNoDuplicates(records), MESSAGE_DUPLICATE_RECORDS);
        this.records = List.copyOf(records);
    }

    /**
     * Returns true if no two of the given records are for the same exercise on the same date.
     */
    public static boolean hasNoDuplicates(List<GymPerformance> records) {
        requireAllNonNull(records);
        // Uses a set instead of comparing every pair, as a client may have up to 1000 records.
        // The key relies on ExerciseName's case-insensitive equality, matching GymPerformance#isSameEntry.
        Set<List<Object>> seenEntries = new HashSet<>();
        for (GymPerformance record : records) {
            if (!seenEntries.add(List.of(record.getDate(), record.getExerciseName()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if this history has a record for the same exercise on the same date as {@code record}.
     */
    public boolean hasSameEntry(GymPerformance record) {
        requireNonNull(record);
        return records.stream().anyMatch(record::isSameEntry);
    }

    /**
     * Returns the record for {@code exerciseName} on {@code date}, if any.
     * Exercise names are matched ignoring letter case and extra spaces, as in {@link ExerciseName}.
     */
    public Optional<GymPerformance> find(LocalDate date, ExerciseName exerciseName) {
        requireAllNonNull(date, exerciseName);
        return records.stream()
                .filter(record -> record.getDate().equals(date) && record.getExerciseName().equals(exerciseName))
                .findFirst();
    }

    /**
     * Returns a copy of this history without the record for the same exercise on the same date as {@code record}.
     * The other records keep their recording order, so the latest record and the count are recalculated
     * from what remains.
     */
    public PerformanceHistory withoutRecord(GymPerformance record) {
        requireNonNull(record);
        List<GymPerformance> updatedRecords = new ArrayList<>(records);
        updatedRecords.removeIf(record::isSameEntry);
        return new PerformanceHistory(updatedRecords);
    }

    /**
     * Returns a copy of this history with {@code record} added as the most recently recorded entry.
     * Any existing record for the same exercise on the same date is removed, so a replacement
     * also counts as a new recording.
     */
    public PerformanceHistory withRecord(GymPerformance record) {
        requireNonNull(record);
        List<GymPerformance> updatedRecords = new ArrayList<>(records);
        updatedRecords.removeIf(record::isSameEntry);
        updatedRecords.add(record);
        return new PerformanceHistory(updatedRecords);
    }

    /**
     * Returns the record with the latest date. If several records share that date,
     * the one recorded last is returned.
     */
    public Optional<GymPerformance> getLatest() {
        return getNewestFirst().stream().findFirst();
    }

    /**
     * Returns the records as an unmodifiable list sorted from the newest date to the oldest.
     * Records on the same date are ordered from the most recently recorded, so the first record is always
     * the one returned by {@link #getLatest()}. This keeps a client's card and full history consistent.
     */
    public List<GymPerformance> getNewestFirst() {
        List<GymPerformance> newestFirst = new ArrayList<>(records);
        // Reversing puts later recordings first; the stable sort then keeps that order within each date.
        Collections.reverse(newestFirst);
        newestFirst.sort(Comparator.comparing(GymPerformance::getDate).reversed());
        return List.copyOf(newestFirst);
    }

    public int size() {
        return records.size();
    }

    /**
     * Returns the records as an unmodifiable list, oldest recorded first.
     */
    public List<GymPerformance> asUnmodifiableList() {
        return records;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PerformanceHistory otherHistory)) {
            return false;
        }

        return records.equals(otherHistory.records);
    }

    @Override
    public int hashCode() {
        return records.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("records", records)
                .toString();
    }

}
