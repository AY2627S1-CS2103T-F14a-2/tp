package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel listing the clients with an upcoming session, soonest first, so that the trainer can prepare for them.
 *
 * <p>AI assistance: Claude Code helped implement this class.
 */
public class UpcomingSessionPanel extends UiPart<Region> {

    private static final String FXML = "UpcomingSessionPanel.fxml";
    private static final String NO_UPCOMING_SESSIONS =
            "No upcoming sessions.\nBook one with: session INDEX d/DATE t/TIME";

    @FXML
    private ListView<Person> upcomingSessionListView;

    /**
     * Creates an {@code UpcomingSessionPanel} that shows the clients in {@code upcomingSessionList}.
     */
    public UpcomingSessionPanel(ObservableList<Person> upcomingSessionList) {
        super(FXML);
        upcomingSessionListView.setItems(upcomingSessionList);
        upcomingSessionListView.setCellFactory(listView -> new UpcomingSessionCell());
        Label placeholder = new Label(NO_UPCOMING_SESSIONS);
        placeholder.setWrapText(true);
        upcomingSessionListView.setPlaceholder(placeholder);
    }

    /**
     * Custom {@code ListCell} that displays a {@code Person}'s session using an {@code UpcomingSessionCard}.
     */
    private static class UpcomingSessionCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);
            setText(null);
            setGraphic(empty || person == null ? null : new UpcomingSessionCard(person).getRoot());
        }
    }
}
