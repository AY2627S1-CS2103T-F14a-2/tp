package seedu.address.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.model.person.Person;

/**
 * A card in the upcoming sessions panel: when a client's next session is, and their latest records
 * to prepare with. It has no index, so that it is not confused with the client list's indices used by commands.
 *
 * <p>AI assistance: Claude Code helped implement this class.
 */
public class UpcomingSessionCard extends UiPart<Region> {

    private static final String FXML = "UpcomingSessionCard.fxml";

    @FXML
    private Label sessionTime;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label latestMeasurement;
    @FXML
    private Label latestPerformance;

    /**
     * Creates an {@code UpcomingSessionCard} for {@code person}, who must have a next session.
     */
    public UpcomingSessionCard(Person person) {
        super(FXML);
        assert person.getNextSession().isPresent() : "Only clients with a booked session are listed";
        sessionTime.setText(person.getNextSession().get().toDisplayString(LocalDateTime.now()));
        name.setText(person.getName().fullName);
        phone.setText(PersonCard.PHONE_LABEL + PhoneDisplayFormatter.format(person.getPhone().value));
        LocalDate today = LocalDate.now();
        latestMeasurement.setText(PersonCard.formatLatestMeasurement(person.getMeasurements(), today));
        latestPerformance.setText(PersonCard.formatLatestPerformance(person.getPerformanceHistory(), today));
    }
}
