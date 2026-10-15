package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalGymPerformances.PULL_UP;
import static seedu.address.testutil.TypicalGymPerformances.SQUAT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.progress.PerformanceHistory;

/**
 * Tests the text shown on a client card. These tests do not start JavaFX.
 */
public class PersonCardTest {

    @Test
    public void formatLatestPerformance_noRecords_showsDash() {
        assertEquals("Latest performance: -", PersonCard.formatLatestPerformance(new PerformanceHistory()));
    }

    @Test
    public void formatLatestPerformance_oneRecord_usesSingularCount() {
        assertEquals("Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT))));
    }

    @Test
    public void formatLatestPerformance_severalRecords_showsLatestAndCount() {
        // the latest date is shown even though an older record was entered last
        assertEquals("Latest performance: 18 Sep - Bench Press, 3 sets of 8 reps at 60 kg (2 records)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(BENCH_PRESS, SQUAT))));

        // records on the same date: the one entered last is shown
        assertEquals("Latest performance: 18 Sep - Pull-up, 1 set of 1 rep at 0 kg (3 records)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT, BENCH_PRESS, PULL_UP))));
    }
}
