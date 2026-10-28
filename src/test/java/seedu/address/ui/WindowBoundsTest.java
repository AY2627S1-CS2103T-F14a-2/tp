package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;
import seedu.address.commons.core.GuiSettings;

public class WindowBoundsTest {
    private static final Rectangle2D PRIMARY = new Rectangle2D(0, 30, 1920, 1010);
    private static final Rectangle2D LEFT = new Rectangle2D(-1280, -200, 1280, 720);
    private static final List<Rectangle2D> SCREENS = List.of(PRIMARY, LEFT);

    @Test
    public void resolve_validPositions_preservesIncludingNegativeCoordinates() {
        assertBounds(new Rectangle2D(100, 100, 800, 600), new GuiSettings(800, 600, 100, 100));
        assertBounds(new Rectangle2D(-1200, -150, 800, 600), new GuiSettings(800, 600, -1200, -150));
    }

    @Test
    public void resolve_partialOverlap_clampsEveryEdge() {
        assertBounds(new Rectangle2D(1120, 440, 800, 600), new GuiSettings(800, 600, 1800, 1000));
        assertBounds(new Rectangle2D(100, 30, 800, 600), new GuiSettings(800, 600, 100, -100));
        assertBounds(new Rectangle2D(-1280, -200, 800, 600), new GuiSettings(800, 600, -1400, -300));
    }

    @Test
    public void resolve_spansScreens_usesLargestOverlap() {
        assertBounds(new Rectangle2D(0, 100, 800, 600), new GuiSettings(800, 600, -200, 100));
        assertBounds(new Rectangle2D(-800, -80, 800, 600), new GuiSettings(800, 600, -600, 100));
    }

    @Test
    public void resolve_oversizedWindow_fitsVisualArea() {
        assertBounds(PRIMARY, new GuiSettings(3000, 2000, 100, 100));
        assertBounds(PRIMARY, new GuiSettings(Double.MAX_VALUE, Double.MAX_VALUE, 100, 100));
    }

    @Test
    public void resolve_offscreenOrTouchingEdge_resetsDefaults() {
        Rectangle2D expected = new Rectangle2D(590, 235, 740, 600);
        assertBounds(expected, new GuiSettings(900, 700, 4000, 4000));
        assertBounds(expected, new GuiSettings(900, 700, 1920, 100));
        assertBounds(expected, new GuiSettings(900, 700, Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertEquals(expected, WindowBounds.resolve(new GuiSettings(800, 600, -1200, -150),
                List.of(PRIMARY), PRIMARY, 450, 400));
    }

    @Test
    public void resolve_invalidDimensions_resetsDefaults() {
        for (double invalid : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY}) {
            assertBounds(new Rectangle2D(590, 235, 740, 600), new GuiSettings(invalid, 600, 100, 100));
            assertBounds(new Rectangle2D(590, 235, 740, 600), new GuiSettings(800, invalid, 100, 100));
        }
    }

    @Test
    public void resolve_missingCoordinates_centersOnPrimary() {
        assertBounds(new Rectangle2D(590, 235, 740, 600), new GuiSettings());
    }

    @Test
    public void resolve_belowMinimum_enlargesAndClampsPosition() {
        assertBounds(new Rectangle2D(1470, 640, 450, 400), new GuiSettings(1, 1, 1919, 1039));
    }

    @Test
    public void resolve_screenSmallerThanMinimum_fitsInsteadOfEnforcingMinimum() {
        Rectangle2D small = new Rectangle2D(20, 40, 320, 240);
        assertEquals(small, WindowBounds.resolve(new GuiSettings(800, 600, 30, 50),
                List.of(small), small, 450, 400));
        assertEquals(small, WindowBounds.resolve(new GuiSettings(800, 600, 4000, 4000),
                List.of(small), small, 450, 400));
    }

    private void assertBounds(Rectangle2D expected, GuiSettings settings) {
        assertEquals(expected, WindowBounds.resolve(settings, SCREENS, PRIMARY, 450, 400));
    }
}
