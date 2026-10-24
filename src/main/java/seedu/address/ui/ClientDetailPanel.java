package seedu.address.ui;

import java.time.LocalDateTime;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Person;

/**
 * Panel showing the details and full history of the client opened with {@code view}.
 * It redraws whenever the viewed client changes, e.g. after that client is edited or gets a new record.
 *
 * <p>AI assistance: Claude Code helped implement this class.
 */
public class ClientDetailPanel extends UiPart<Region> {

    private static final String FXML = "ClientDetailPanel.fxml";

    private final ObservableValue<Person> viewedPerson;

    @FXML
    private Label name;
    @FXML
    private Label history;

    /**
     * Creates a {@code ClientDetailPanel} that shows {@code viewedPerson} whenever a client is viewed.
     */
    public ClientDetailPanel(ObservableValue<Person> viewedPerson) {
        super(FXML);
        this.viewedPerson = viewedPerson;
        viewedPerson.addListener((observable, oldPerson, newPerson) -> refresh());
        refresh();
    }

    /**
     * Redraws the panel, so that time-dependent text, such as a next session that has just passed,
     * matches the client card. Does nothing while no client is viewed, as the panel is then hidden.
     */
    public void refresh() {
        Person person = viewedPerson.getValue();
        if (person == null) {
            return;
        }
        name.setText(person.getName().fullName);
        history.setText(ClientHistoryFormatter.format(person, LocalDateTime.now()));
    }
}
