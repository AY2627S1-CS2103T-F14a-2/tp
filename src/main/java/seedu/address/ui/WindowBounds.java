package seedu.address.ui;

import java.awt.Point;
import java.util.List;

import javafx.geometry.Rectangle2D;
import seedu.address.commons.core.GuiSettings;

/**
 * Resolves saved window bounds without accessing a display. Screen rectangles are visual bounds,
 * excluding taskbars and other reserved desktop areas.
 */
final class WindowBounds {

    private WindowBounds() {
    }

    /**
     * Fits a window on the screen with the largest saved overlap, preserving valid coordinates.
     * Missing coordinates are centered on the primary screen. Invalid dimensions or completely
     * off-screen bounds restore the default size centered on the primary screen.
     * Screen bounds must be non-empty, finite rectangles supplied by JavaFX.
     */
    static Rectangle2D resolve(GuiSettings settings, List<Rectangle2D> screens, Rectangle2D primary,
            double minWidth, double minHeight) {
        double width = settings.getWindowWidth();
        double height = settings.getWindowHeight();
        Point position = settings.getWindowCoordinates();
        Rectangle2D target = primary;
        if (!isPositiveFinite(width) || !isPositiveFinite(height)) {
            return centeredDefaults(primary, minWidth, minHeight);
        }
        if (position != null) {
            double largestOverlap = 0;
            for (Rectangle2D screen : screens) {
                double overlapWidth = Math.max(0, Math.min(position.getX() + width, screen.getMaxX())
                        - Math.max(position.getX(), screen.getMinX()));
                double overlapHeight = Math.max(0, Math.min(position.getY() + height, screen.getMaxY())
                        - Math.max(position.getY(), screen.getMinY()));
                double overlap = overlapWidth * overlapHeight;
                if (overlap > largestOverlap) {
                    largestOverlap = overlap;
                    target = screen;
                }
            }
            if (largestOverlap == 0) {
                return centeredDefaults(primary, minWidth, minHeight);
            }
        }
        width = Math.min(Math.max(width, minWidth), target.getWidth());
        height = Math.min(Math.max(height, minHeight), target.getHeight());
        double x = position == null ? target.getMinX() + (target.getWidth() - width) / 2
                : clamp(position.getX(), target.getMinX(), target.getMaxX() - width);
        double y = position == null ? target.getMinY() + (target.getHeight() - height) / 2
                : clamp(position.getY(), target.getMinY(), target.getMaxY() - height);
        return new Rectangle2D(x, y, width, height);
    }

    private static Rectangle2D centeredDefaults(Rectangle2D primary, double minWidth, double minHeight) {
        return resolve(new GuiSettings(), List.of(primary), primary, minWidth, minHeight);
    }

    private static boolean isPositiveFinite(double value) {
        return Double.isFinite(value) && value > 0;
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}
