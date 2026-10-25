package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 *
 * <p>AI assistance: Gabriel used OpenAI Codex for adaptive feedback sizing and its tests.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final double MIN_HEIGHT = 44;
    private static final double MAX_HEIGHT = 112;
    // Conservative pixel allowances for the JavaFX TextArea skin with DarkTheme.css, not exact CSS insets:
    // 24 covers top/bottom padding and text-metric slack; 32 reserves horizontal padding plus a vertical
    // scrollbar even while hidden. Recheck wrapping and last-line visibility when changing either style.
    private static final double TEXT_INSETS = 24;
    private static final double SCROLLBAR_SPACE = 32;

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates feedback that grows with wrapped text up to a scrollable maximum height.
     */
    public ResultDisplay() {
        super(FXML);
        // Measure wrapped text without depending on the TextArea skin's internal nodes.
        Text measuredText = new Text();
        measuredText.textProperty().bind(resultDisplay.textProperty());
        measuredText.fontProperty().bind(resultDisplay.fontProperty());
        measuredText.wrappingWidthProperty().bind(
                Bindings.max(1, resultDisplay.widthProperty().subtract(SCROLLBAR_SPACE)));
        getRoot().prefHeightProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(MAX_HEIGHT, Math.max(MIN_HEIGHT, measuredText.getLayoutBounds().getHeight() + TEXT_INSETS)),
                measuredText.layoutBoundsProperty()));
        getRoot().setMinHeight(Region.USE_PREF_SIZE);
        getRoot().setMaxHeight(Region.USE_PREF_SIZE);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        resultDisplay.positionCaret(0);
        resultDisplay.setScrollTop(0);
    }

}
