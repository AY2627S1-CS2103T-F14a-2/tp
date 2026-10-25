---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* Helen (He Jingqi) used OpenAI Codex to assist with implementing, testing and documenting
  the v1.4 measurement deletion feature for issue #84, including its model operation, command, parser,
  persistence tests and sequence diagram.

* Gabriel Wan used [OpenAI Codex](https://openai.com/codex/) to assist with implementing, testing,
  and documenting the v1.3 next-session feature, including parsing, client integration, JSON storage, sorting,
  and the client card row, and the v1.4 session cancellation/date-filter commands, tests, documentation,
  and release preparation.
* Gabriel Wan used OpenAI Codex to assist with the v1.5 UI polish: adaptive feedback sizing, card spacing and
  styling, separate history sections, GUI tests, documentation and screenshots.
* AI assistance: Luke Chin used Claude Code (Anthropic) to draft parts of the issue #64 changes (the `list` command messages, undoing changes when saving fails, the damaged data file warning, the client card phone and email rows, and the related tests and User Guide wording) on 2026-10-15. The changes were reviewed and tested by the author.
* AI assistance: Luke Chin used Claude Code (Anthropic) to draft parts of the issue #79 changes (the tests for data files with invalid values, the `view` sequence diagram and "View client history" section, and the related manual testing steps) on 2026-10-22. The changes were reviewed and tested by the author.
* Vincent Ong (v1-nce) used Claude Code (Anthropic) to help implement, test and document the client `add`/`delete` changes for v1.3 (removing address, optional email, name and phone limits, Spotter messages). All generated code was reviewed and tested before committing.
* Vincent Ong (v1-nce) used Claude Code (Anthropic) to help implement, test and document the v1.5 split-screen window
  (the Upcoming sessions panel and cards, showing a viewed client's history in its place, keeping both in sync in
  `ModelManager` and `LogicManager`, the related tests, the User Guide "The Spotter window" and `view` sections, and the
  "Upcoming sessions panel" and "View client history" sections of this guide). All generated code was reviewed and
  tested before committing.
* The GUI tests use [TestFX](https://github.com/TestFX/TestFX) to run JavaFX UI parts in tests.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, `UpcomingSessionPanel`, `ClientDetailPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

`ResultDisplay` measures its wrapped text with a JavaFX `Text` using the feedback area's font and available width.
Its preferred height ranges from 44 to 112 logical pixels, freeing space for the lists after short confirmations.
Longer messages remain complete in a wrapping, selectable, read-only `TextArea` with scrolling. Resizing the
window recalculates the height. The surrounding placeholder has no fixed height.

Card typography, selection borders and tag contrast are defined in `DarkTheme.css`. Both card layouts use the
same padding and line spacing. Names, email addresses and record summaries wrap within the card width.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores the clients with an upcoming session, soonest first, in a separate list built from the whole address book (so `find` and `filter` do not affect it). It is exposed as an unmodifiable `ObservableList<Person>` for the UI's Upcoming sessions panel.
* stores the client shown by `view` (or none) and exposes it as an `ObservableValue<Person>`, so that the UI's detail panel can follow that client.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Upcoming sessions panel

The right side of the window shows the clients with an upcoming session, soonest first, so that a trainer can
see who is next and prepare. It adds no new session logic. Instead, it reuses what the client list already has:

* `ModelManager` keeps a `SortedList` over a `FilteredList` of the whole address book. The filter keeps clients
  whose `NextSession` is not before "now", which is the same rule that the client card uses to show
  `- (none upcoming)`. The sort is the existing `PersonSessionComparator`, so the panel and the client list
  order sessions in the same way.
* Each `UpcomingSessionCard` shows the session time from `NextSession#toDisplayString`, the phone from
  `PhoneDisplayFormatter`, and the latest records from the static `PersonCard` formatters.
  The cards have no index numbers, so that they cannot be mistaken for the client list's indices.
* Booking, cancelling or recording a session replaces the client through `Model#setPerson`. The `FilteredList`
  re-tests the replaced client, so the panel updates without any command knowing about it.
  Only the passing of time changes nothing in the data, so after each successful command `LogicManager#execute`
  calls `Model#updateSessionLists(now)`. Against that one time, it re-sorts the client list (which makes
  `PersonListPanel` redraw the client cards) and drops passed sessions from this panel, so both sides agree.
  It runs only after the command, as re-sorting first could change which client a typed index refers to.

**Aspect: when to drop a passed session**

* **Alternative 1 (current choice):** On the next successful command.
  * Pros: Needs no timer, and the model logic is tested with a fixed "now".
  * Cons: A passed session stays visible until the trainer's next successful command.
* **Alternative 2:** On a timer.
  * Pros: Always exact.
  * Cons: Adds a background thread to the UI for little benefit, as the trainer types commands throughout the day.

`MainWindow` shows either this panel or the `ClientDetailPanel` in the same place, by binding each panel's
visibility to whether `Logic#getViewedPerson()` holds a client (see [View client history](#view-client-history)).

### View client history

`view INDEX` shows one client's details followed by their full measurement and gym-performance history in the
`ClientDetailPanel`, in place of the Upcoming sessions panel. It follows the usual `Logic` flow: `ViewCommandParser`
parses the index, and `ViewCommand` looks the client up in `Model#getFilteredPersonList()` and passes it to
`Model#setViewedPerson(Person)`. The `CommandResult` only carries a one-line confirmation for the result display.

<puml src="diagrams/ViewSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `view 1` Command" />

`view` is read-only. As `LogicManager` only saves when a command changes the address book, executing `view`
never writes the data file.

**Keeping the panel up to date.** `ModelManager` holds the viewed client in an `ObservableValue<Person>`, which
`Logic#getViewedPerson()` exposes to the UI. `ClientDetailPanel` listens to it and redraws whenever it changes.
As `Person` is immutable, a command that changes a client, e.g. `edit` or `measure`, replaces it through
`Model#setPerson(target, editedPerson)`. If `target` is the viewed client, `ModelManager` views `editedPerson`
instead, so the panel follows the client without any command knowing about the panel:

<puml src="diagrams/ViewedClientSyncSequenceDiagram.puml" alt="How the detail panel follows the viewed client after `measure`" />

In the same way, `Model#deletePerson` and `Model#setAddressBook` (used by `clear`) stop viewing a client that
no longer exists, and `ListCommand` stops viewing any client. The Upcoming sessions panel then shows again.
If saving fails, `LogicManager` restores the viewed client together with the address book and the filter, so
the panel matches the restored data.

Design notes:

* **Non-UI formatter:** `ClientHistoryFormatter` is a plain class in `logic.commands` with no JavaFX dependency,
  so the panel's text can be checked in unit tests without starting the GUI. Its detail and record formatting
  methods supply separate labels in `ClientDetailPanel`. Measurements and performance have styled headings with
  counts and a newest-first hint; the formatting and ordering rules are shared with the plain-text formatter.
* **"Now" at drawing time:** whether the next session has passed is decided when the panel is drawn, as on the
  client card. `MainWindow` also redraws the panel after every successful command, so a session that passes
  while the panel is open is shown the same way as on the card.
* **Scroll position:** when the viewed client changes to a different client (by `Person#isSamePerson`), the
  panel scrolls back to the top. A replacement of the same client, e.g. a new record, keeps the scroll position.
* **Ordering:** both histories are listed newest first. Measurements on the same date follow the type order
  weight, body fat, waist. Performance records on the same date are listed most recently recorded first, so the
  first line always matches the client card's "Latest performance".

**Aspect: where to show the history**

* **Alternative 1:** Show it as text in the result display (used up to v1.4).
  * Pros: Needs no UI changes, and the output is easy to test.
  * Cons: The result display is only a few lines tall, the history is replaced by the next command's result,
    and it goes stale when the client's records change.
* **Alternative 2 (current choice):** Show it on the right side of the window, in place of Upcoming sessions.
  * Pros: Has room for a long history, stays visible while records are logged during a session, and updates as
    the client changes.
  * Cons: Hides Upcoming sessions until `list`, and the viewed client must be kept in sync in the model.

**Aspect: where to keep the viewed client**

* **Alternative 1 (current choice):** In `ModelManager`, updated by `setPerson`, `deletePerson` and `setAddressBook`.
  * Pros: The model is the only place that knows which new `Person` replaces which old one, and the sync logic
    can be unit tested without JavaFX.
  * Cons: Adds UI-related state to the model.
* **Alternative 2:** In the UI, which would search the client list for the viewed client after every command.
  * Pros: Leaves the model unchanged.
  * Cons: An edited client cannot be matched reliably, e.g. after its name changes, and the logic would be in
    GUI code that is hard to test.

<!-- AI assistance: Claude Code helped draft this section and its sequence diagrams. -->

### Recording and deleting gym performances

`performance INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD` records one result for the client at the displayed index,
and `deleteperformance INDEX d/DATE e/EXERCISE` removes one. Both follow the usual `Logic` flow: the parser validates
the fields, and the command resolves the index against `Model#getFilteredPersonList()`.

The following partial diagram shows how the records are stored and which classes change them. Contact fields and the
other progress data are omitted.

<puml src="diagrams/PerformanceModelClassDiagram.puml" alt="Classes used to store and change a client's gym-performance records" />

Each `Person` owns one immutable `PerformanceHistory`, which keeps the records in the order they were recorded and
never holds two records for the same exercise on the same date. `PerformanceCommand` calls
`Person#withGymPerformance`, which replaces any record for the same exercise and date. `DeletePerformanceCommand`
finds the record through `PerformanceHistory#find` and calls `Person#withoutGymPerformance`; a missing record raises a
`CommandException` before any state changes. Either way, the command replaces the client through `Model#setPerson`,
and `LogicManager` saves the address book.

Both commands log rejected indices, missing records and successful changes at `INFO` level. The log records the
displayed index, exercise and date, but not the client's name or contact details.

**Design choice:** The client card's latest performance and record count are derived from `PerformanceHistory`
rather than stored separately, so they cannot become stale after a replacement or deletion. Keeping the recording
order means the record entered last is treated as the latest when several share a date.

<!-- AI assistance: Claude Code helped draft this section and its class diagram. -->

### Deleting body measurements

`deletemeasure INDEX d/DATE t/TYPE` removes one measurement from the client at the displayed index.
`DeleteMeasureCommandParser` validates the index, date and measurement type. The shared
`ParserUtil#parseExistingRecordDate` accepts future dates because human-edited storage can contain them;
`parseRecordDate` adds the existing future-date restriction for recording measurements and performances.

`DeleteMeasureCommand` looks up the client through `Model#getFilteredPersonList()` and finds the record
by date and type. A missing record raises a `CommandException` before any state changes.
`Person#withoutMeasurement` creates an immutable copy with that record removed, preserving all contact,
session and performance data. `Model#setPerson` replaces the original client without changing the filter.
The card derives its latest measurement and count from the updated list, including same-date type ties
and the empty state. History consumers read `Person#getMeasurements()` from the updated client; a
previously formatted history result is a snapshot and must be requested again after deletion.

<puml src="diagrams/DeleteMeasureSequenceDiagram.puml" alt="Measurement deletion sequence" />

`LogicManager` saves the changed address book using the existing storage format. If saving fails,
it restores the previous address book and filter and reports `Unable to save Spotter data.`
No storage schema change or separate cached record count is needed. Keeping counts derived from the
measurement list avoids stale summary data after deletion.

### Session cancellation and date filtering

`cancelsession INDEX` removes the current displayed client's single recorded session.
`CancelSessionCommandParser` accepts exactly one index and `CancelSessionCommand` resolves it against
`Model#getFilteredPersonList()`, so indices refer to the sorted/filtered UI rather than JSON order.
It rejects an invalid index or a client without a recorded session before changing the model.

The following partial model diagram shows the session field, the date predicate and the progress data that must survive
cancellation. Contact fields are omitted to keep the diagram focused.

<puml src="diagrams/SessionModelClassDiagram.puml" alt="Session date filtering and the client data preserved during cancellation" />

`Person#withNextSession(null)` returns a new immutable client with the session removed, carrying forward contact details,
tags, measurements and performance history. The command replaces the client and reapplies the current predicate to
refresh sorting. A session-date predicate no longer matches the cancelled client, so that card disappears from that view;
search/tag filters remain active. The observable list updates the UI and `CommandResult` requests scrolling to the top.

`LogicManager` handles saving. If storage throws an `IOException`, its existing transaction restores both the original
address book and the predicate captured before cancellation. This restores the booking and the date-filtered view.
No constructor, JSON schema or UI-specific cancellation path is needed.

`sessions d/DATE` uses `SessionsCommandParser` and `ParserUtil#parseCalendarDate` to validate one real calendar date.
Unlike booking a session, filtering permits past dates so retained expired bookings can be found. The shared calendar
validator preserves the existing booking errors; `parseSessionDate` adds the separate no-past-date rule for bookings.
`SessionsCommand` replaces the current predicate with `SessionOnDatePredicate` and reuses the normal session comparator.
It returns a count or a no-matches message. As no client data changes, `LogicManager` does not save the file.

**Design choices:** Reusing the current view avoids a second schedule panel or a separate session collection.
Retaining the predicate during cancellation lets a trainer cancel several bookings in the same date view.
The trade-off is that the date view contains only each client's currently recorded booking; it is not a session history.
The main list's upcoming-before-expired ordering also applies to date results, including today's elapsed sessions.

### Removing a client's email

`edit INDEX e/` with nothing after `e/` removes the client's optional email, the same way `t/` clears tags.
`EditCommandParser` calls `EditPersonDescriptor#removeEmail()` for an empty `e/` value and parses any other value as
before. `EditCommand` then builds a client without an email, carrying over the other details, the session, measurements
and performance history, and replaces the original through `Model#setPerson`.

<puml src="diagrams/EditRemoveEmailSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `edit 1 e/` Command" />

**Design choice:** `EditPersonDescriptor` stores a plain `Email` plus an `isEmailRemoved` flag. `setEmail` and
`removeEmail` reset each other, so an edit can never both replace and remove the email; `EditCommand` asserts this.
The alternative, an `Optional<Email>` field that is null when unchanged, made `getEmail()` return
`Optional<Optional<Email>>`, which reviewers found hard to read.

<!-- AI assistance: Claude Code helped draft this section and its sequence diagram. -->

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a freelance personal trainer who independently manages their own clients, including when attached to a gym
* manages a client base of tens of clients and handles their own administrative work
* needs to keep track of client contact details, upcoming session times, body measurements, and gym performance
* currently keeps client information across contacts, messages, notes, and calendars, making relevant information difficult to retrieve quickly
* uses a personal computer and is comfortable typing short commands
* prefers typing to navigating menus for frequent data-entry tasks
* manages client records individually without requiring shared access with other trainers


**Value proposition**: Freelance personal trainers manage client information across scattered contacts, notes, and calendars. Spotter brings contact details, session times, and progress records together around each client, helping trainers quickly retrieve information, prepare for sessions, and review progress while spending less time searching across separate tools.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a … | I can … | So that … |
|----------|--------|---------|-----------|
| `* *` | potential user exploring the app | see the app pre-loaded with sample data | I can understand how it looks in use before entering real data |
| `* *` | new user | purge all sample data | I can start entering my real clients on a clean slate |
| `* *` | new user | view a user guide or help information | I can learn what the app can do |
| `* * *` | user | add a client with their contact details | I can store everyone I train in one place |
| `* * *` | user | list all my clients | I can see everyone I train at a glance |
| `* * *` | user | delete a client | I can remove records of people I no longer train |
| `* *` | user | edit a client's details | I can keep their information up to date |
| `* *` | user | find a client by name | I can quickly retrieve their details |
| `* *` | user who cannot remember a client's full name | find a client using part of their name | I can locate them without remembering the exact name |
| `* * *` | user | record the date and time of a client's next session | I know when I am next meeting them |
| `* *` | user | see all sessions scheduled for today | I know who I am training today |
| `* *` | user | see a client's next upcoming session | I can prepare for it in advance |
| `* *` | user | update a client's next session date and time | my schedule stays accurate when plans change |
| `* *` | user | remove a recorded session | cancelled sessions do not clutter my schedule |
| `* *` | busy user | receive a warning when I schedule two sessions at the same time | I can avoid double-booking myself |
| `* * *` | user | record a client's body measurements | I can track changes in their measurements over time |
| `* * *` | user | record a client's gym performance | I can track changes in their performance over time |
| `* *` | user | add a note after a session | I can remember what we did when preparing for the next session |
| `* *` | user | view a client's progress history | I can assess how their measurements and performance have changed over time |
| `* *` | user | record a client's goal | I can plan their training with that goal in mind |
| `* *` | user | edit or delete a progress entry | I can correct mistakes in recorded data |
| `* *` | user | tag clients with labels such as rehab or weight-loss | I can group similar clients together |
| `* *` | user | filter clients by tag | I can focus on one group at a time |
| `* *` | user | sort clients by name or next session | I can find the client I need more quickly |
| `* *` | user | find clients I have not seen in a while | I can follow up with them about booking another session |
| `* *` | expert user | create shortcuts or aliases for frequent commands | I can save time on actions I repeat often |
| `* *` | long-time user | archive or hide inactive clients | I can focus on active clients without deleting older records |
| `* *` | user | restore an archived client | I can resume training someone who returns without re-entering their details |
| `* * *` | user | have my data saved automatically and loaded when the app starts | I can resume my work without manually saving or re-entering records |
| `* * *` | user | have my data stored in a human-editable local file | I can inspect its contents and copy it for backup or transfer between computers |
| `* *` | user | export my client data | I can keep a separate backup in case my laptop fails |
| `* *` | user | import client data from a file | I can move my data to a new computer |
| `* *` | expert user | edit the data file directly | I can make bulk changes quickly |                           |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `Spotter` and the **Actor** is the `trainer`, unless specified otherwise)

**Use case: UC01 - Add a client**

**MSS**

1.  Trainer requests to add a client with the client's details.
2.  Spotter adds the client and shows the added client.

    Use case ends.

**Extensions**

* 1a. A required detail is missing.

    * 1a1. Spotter shows an error message stating which detail is missing.

      Use case resumes at step 1.

* 1b. A given detail is invalid.

    * 1b1. Spotter shows an error message stating which detail is wrong and the expected format.

      Use case resumes at step 1.

* 1c. A client with the same name already exists. Names are compared ignoring case and leading/trailing spaces, with repeated spaces treated as one.

    * 1c1. Spotter informs the trainer that the client already exists.

      Use case ends.

**Use case: UC02 - Find a client**

**MSS**

1.  Trainer requests to find clients using keywords from their names, which may be full or partial names.
2.  Spotter shows the list of matching clients.

    Use case ends.

**Extensions**

* 1a. No keywords are given.

    * 1a1. Spotter shows an error message.

      Use case resumes at step 1.

* 2a. No client matches the keywords.

    * 2a1. Spotter shows an empty list and informs the trainer that no clients were found.

      Use case ends.

**Use case: UC03 - Edit a client's details**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to edit specific details of a client in the list.
4.  Spotter updates the client and shows the updated details.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      Use case resumes at step 3.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 3b. No details to edit are given.

    * 3b1. Spotter shows an error message stating that at least one detail must be given.

      Use case resumes at step 3.

* 3c. A given detail is invalid.

    * 3c1. Spotter shows an error message stating which detail is wrong and the expected format.

      Use case resumes at step 3.

* 3d. The new name is the same as that of another existing client, compared as in UC01 extension 1c.

    * 3d1. Spotter informs the trainer that the client already exists.

      Use case ends.

**Use case: UC04 - Delete a client**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to delete a specific client in the list.
4.  Spotter deletes the client, together with all records attached to the client, and shows the deleted client.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      Use case resumes at step 3.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.


**Use case: UC05 - Record a client's gym performance**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to record a gym performance for a specific client in the list, with the performance details.
4.  Spotter records the performance and shows the recorded performance.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      If matching clients are found, use case resumes at step 3; otherwise, the trainer retries the search or the use case ends.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 3b. A required detail is missing or given more than once.

    * 3b1. Spotter shows an error message with the expected input format.

      Use case resumes at step 3.

* 3c. The given date is invalid or in the future.

    * 3c1. Spotter shows an error message stating that the date is invalid or in the future, and the expected date format.

      Use case resumes at step 3.

* 3d. Another given detail is invalid.

    * 3d1. Spotter shows an error message stating which detail is wrong and the expected format.

      Use case resumes at step 3.

* 3e. A record for the same exercise on the same date already exists for the client.

    * 3e1. Spotter replaces the existing record with the new sets, repetitions and load, and shows the updated record.

      Use case ends.

**Use case: UC06 - Record a client's body measurements**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to record a body measurement for a specific client in the list, with the date, measurement type and value.
4.  Spotter records the measurement and shows the recorded measurement.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      If matching clients are found, use case resumes at step 3; otherwise, the trainer retries the search or the use case ends.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 3b. A required detail is missing or given more than once.

    * 3b1. Spotter shows an error message with the expected input format.

      Use case resumes at step 3.

* 3c. The given date is invalid or in the future.

    * 3c1. Spotter shows an error message stating that the date is invalid or in the future, and the expected date format.

      Use case resumes at step 3.

* 3d. The given measurement type is not supported.

    * 3d1. Spotter shows an error message listing the supported measurement types.

      Use case resumes at step 3.

* 3e. The given value is not a number, is outside the permitted range for the measurement type, or has more than two decimal places.

    * 3e1. Spotter shows an error message stating why the value is invalid.

      Use case resumes at step 3.

* 3f. A measurement of the same type on the same date already exists for the client.

    * 3f1. Spotter replaces the existing value with the new value, and shows the updated measurement.

      Use case ends.

**Use case: UC07 - View a client's progress history**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to view the progress history of a specific client in the list.
4.  Spotter shows the client's gym performance and body measurement records, sorted by date.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      If matching clients are found, use case resumes at step 3; otherwise, the trainer retries the search or the use case ends.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 4a. The client has no progress records yet.

    * 4a1. Spotter informs the trainer that the client has no progress records.

      Use case ends.

**Use case: UC08 - Schedule a client's next session**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to schedule the next session for a specific client in the list, with the session's date and time.
4.  Spotter records the next session and shows the client's next session.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      If matching clients are found, use case resumes at step 3; otherwise, the trainer retries the search or the use case ends.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 3b. The date or time is missing.

    * 3b1. Spotter shows an error message stating what is missing.

      Use case resumes at step 3.

* 3c. The given date or time is invalid.

    * 3c1. Spotter shows an error message stating what is invalid and the expected format.

      Use case resumes at step 3.

* 3d. The given date is before the current system date.

    * 3d1. Spotter shows an error message stating that the date cannot be in the past. Today is accepted,
      even when the supplied time has already passed.

      Use case resumes at step 3.

* 3e. The client already has a next session.

    * 3e1. Spotter replaces the existing next session with the new date and time.

      Use case resumes at step 4.

* 4a. Another client's next session is at the same date and time.

    * 4a1. Spotter keeps both sessions; the MVP does not warn about clashes.

      Use case ends.

**Use case: UC09 - Cancel a client's next session**

**MSS**

1.  Trainer requests to list all clients.
2.  Spotter shows the list of clients.
3.  Trainer requests to cancel the next session of a specific client in the list.
4.  Spotter removes the client's next session and shows the cancelled session.

    Use case ends.

**Extensions**

* 1a. Trainer wants to narrow down the list.

    * 1a1. Trainer finds the client (UC02).

      If matching clients are found, use case resumes at step 3; otherwise, the trainer retries the search or the use case ends.

* 2a. The list is empty.

  Use case ends.

* 3a. The specified client is not in the list.

    * 3a1. Spotter shows an error message.

      Use case resumes at step 3.

* 3b. The client has no next session.

    * 3b1. Spotter informs the trainer that the client has no next session to cancel.

      Use case ends.


### Non-Functional Requirements

1. **Keyboard operation:** After launching Spotter, the trainer must be able to complete all core client-management, next-session and progress-recording workflows using the keyboard without requiring mouse interaction.

2. **Capacity and responsiveness:** Spotter must support 50 clients, each with up to 1,000 body-measurement entries and 1,000 gym-performance entries. With this dataset, at least 95% of client-listing, client-search and record-update commands must display their results within 1 second on the documented reference computer.

3. **Startup performance:** With the dataset specified above, Spotter must load the saved records and become ready to accept commands within 5 seconds on the documented reference computer.

4. **Data integrity:** Commands rejected because of invalid input must leave existing client details, next-session information and progress records unchanged.

5. **Storage failure handling:** If Spotter cannot save a change, it must display an error indicating that the change has not been saved. If an existing data file is malformed, Spotter must not overwrite that file automatically.

6. **Offline operation:** After downloading the release JAR, all core workflows must operate without an internet connection. Spotter must not transmit client records to external services.

7. **Human-editable storage:** Spotter must store client records in a documented, plain-text format that can be inspected and edited using a standard text editor while Spotter is closed.

8. **Platform compatibility:** Spotter must launch and support all core workflows on mainstream operating systems with Java 25 installed, without requiring another Java version.

9. **Portability:** Spotter must be distributed as an executable JAR that runs on a computer with Java 25 installed, without requiring an installer for Spotter.



### Glossary

* **Trainer**: A freelance personal trainer who independently manages their own clients. The trainer is the user of Spotter.

* **Client**: A person whose contact details and training-related information are managed by the trainer in Spotter.

* **Client record**: The information stored for a client, including their contact details and any associated session information, progress records, goals, notes and tags.

* **Session**: A training appointment between the trainer and a client.

* **Next session**: The upcoming training appointment recorded for a client, identified by its date and time. Spotter records at most one next session per client.

* **Body measurement**: A numerical measurement of a client's physical characteristics, such as body weight or body-fat percentage.

* **Body-measurement record**: A dated entry containing one body measurement for a client, such as body weight, body-fat percentage or waist circumference. A client has at most one record of each measurement type on each date.

* **Gym-performance record**: A dated entry describing a client's performance in an exercise, such as the weight lifted or the number of repetitions completed.

* **Progress record / Progress entry**: A body-measurement record or gym-performance record associated with a client. These terms refer to the same concept.

* **Progress history**: A client's body-measurement and gym-performance records presented together in date order.

* **Goal**: A training outcome that a client wants to achieve, such as improving strength or reducing body weight.

* **Session note**: A textual description recorded by the trainer after a session to help them prepare for subsequent sessions.

* **Tag**: A label attached to a client record to support grouping and filtering, such as `rehab` or `weight-loss`.

* **Archived client**: A client whose record is retained but excluded from the normal active-client view and can subsequently be restored.

* **Command alias**: An alternative name or shortcut for a command.

* **Automatic data persistence**: Saving changes without requiring an explicit save command and loading saved data when Spotter starts.

* **Local data file**: A file on the trainer's computer containing Spotter's saved records.

* **Human-editable storage**: Storage in a documented plain-text format that can be inspected and edited using a standard text editor while Spotter is closed.

* **Sample data**: Fictional client records supplied to demonstrate Spotter's behaviour without using real client information.

* **Core workflows**: Adding, listing, finding, editing and deleting clients; managing next sessions; recording, viewing, editing and deleting progress entries; managing goals, notes and tags; filtering and sorting clients; archiving and restoring clients; and managing local data, including saving, loading, importing and exporting.

* **Response time**: The elapsed time between submitting a command and Spotter displaying its result.

* **Startup time**: The elapsed time between starting Spotter and it becoming ready to accept commands after loading saved records.

* **Reference computer**: The baseline computer for verifying NFRs 2 and 3: a CPU with four physical cores and a 2.0 GHz base clock, 8 GB RAM, SSD storage, Windows 11 (64-bit) and JDK 25. Performance tests use this configuration with no other user applications running. The exact CPU and SSD models, JDK distribution and version, and operating-system version must be recorded alongside the test results.

* **Offline operation**: Operation without an internet connection after downloading the release JAR, on a computer with Java 25 installed.

* **Mainstream OS**: For Spotter, Windows, Linux or macOS.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Recording body measurements

Use fictional data in a disposable test folder. Start with a client without measurements,
run `list` and substitute that client's displayed index for `1` below.

1. Enter `measure 1 d/2026-09-12 t/weight v/72.4`.<br>
   Expected: `Recorded measurement for NAME: weight = 72.4 kg on 2026-09-12.`
   The card shows `Latest measurement: 12 Sep - weight 72.4 kg (1 record)`.
1. Enter `measure 1 d/2026-09-12 t/weight v/71.8`.<br>
   Expected: `Updated measurement for NAME: weight = 71.8 kg on 2026-09-12.`
   The card value changes to 71.8 and the count stays at one.
1. Add `measure 1 d/2026-09-12 t/waist v/81`, then
   `measure 1 d/2026-09-11 t/bodyfat v/18.4`.<br>
   Expected: Both commands record new entries. The card still shows weight 71.8 kg, with three records.
1. Try `measure 1 d/2026-02-30 t/weight v/72`, `measure 1 d/2026-09-12 t/bodyfat v/0`,
   and a command using tomorrow's date.<br>
   Expected: Appropriate date/range errors; card and saved measurements remain unchanged.
1. Try a missing prefix, repeated prefix, unsupported type, non-numeric value, value with three decimal places,
   and an index outside the displayed list.<br>
   Expected: Each command is rejected without changing measurements.
1. Edit the client's phone number, close Spotter and restart it.<br>
   Expected: All three measurements persist and the card still shows weight 71.8 kg with three records.
1. Resize the window and check long card rows.<br>
   Expected: The measurement row remains readable and does not overlap neighbouring cards.

<!-- AI assistance: OpenAI Codex helped draft measurement acceptance tests. -->

### Deleting body measurements

Use fictional data in a disposable test folder. Run `list`, choose a client, and substitute that client's
displayed index for `1` below. Start with no measurements for that client and retain a session and a
performance record to check that unrelated data survives deletion.

1. Run `measure 1 d/2026-09-11 t/weight v/73`, `measure 1 d/2026-09-12 t/waist v/81`,
   and `measure 1 d/2026-09-12 t/weight v/72.4`.
   Expected: latest weight 72.4 kg with three records.
2. Run `deletemeasure 1 d/2026-09-12 t/weight`.
   Expected: a deletion message; the card shows waist 81 cm with two records.
3. Run `deletemeasure 1 d/2026-09-12 t/waist`.
   Expected: the card shows weight 73 kg on 11 Sep with one record.
4. Restart Spotter, run `list` and locate the same client again.
   Expected: only the 11 Sep measurement remains; contact details, session and performance are unchanged.
5. Filter to the client using `find`, then use its displayed index to delete the remaining measurement.
   Expected: the filter is preserved and the card shows `Latest measurement: -`.
6. Repeat that deletion, then try `deletemeasure 1 d/2026-02-30 t/weight`,
   `deletemeasure 0 d/2026-09-12 t/weight`, and `deletemeasure 1 d/2026-09-12 t/height`.
   Expected: missing-record, date, index and type errors respectively; no data changes.
7. Run `measure 1 d/2026-09-12 t/weight v/70`, then repeat it with `v/71`.
   Expected: recorded then updated messages; one measurement remains after restart.

The automated `DeleteMeasurePersistenceTest` additionally checks deletion of future-dated records loaded
from storage and rollback of records, the active filter and the saved file after an I/O or permission failure.
`PersonCardTest` checks latest-record fallback, same-date ties, singular/plural counts and the final empty state.

### Recording and deleting gym performances

Use fictional data in a disposable test folder. Start with a client without performance records,
run `list` and substitute that client's displayed index for `1` below. `NAME` is that client's name.

1. Recording and replacing records

   1. Enter `performance 1 d/2026-09-18 e/Bench Press s/3 r/8 w/60`.<br>
      Expected: `Recorded performance for NAME: Bench Press, 3 sets of 8 reps at 60 kg on 2026-09-18.`
      The card shows `Latest performance: 18 Sep - Bench Press, 3 sets of 8 reps at 60 kg (1 record)`.
   1. Enter `performance 1 d/2026-09-15 e/Squat s/3 r/5 w/100`.<br>
      Expected: A new record is added. The card still shows the 18 Sep bench press, now with `(2 records)`.
   1. Enter `performance 1 d/2026-09-18 e/bench press s/4 r/6 w/62.5`.<br>
      Expected: `Updated performance for NAME: bench press, 4 sets of 6 reps at 62.5 kg on 2026-09-18.`
      The card shows the new values and the count stays at two.

1. Deleting the latest record while keeping other data

   1. Enter `session 1 d/DATE t/1800`, using any future date, then `measure 1 d/2026-09-12 t/weight v/72.4`.<br>
      Expected: The card shows the next session and `Latest measurement: 12 Sep - weight 72.4 kg (1 record)`.
   1. Enter `deleteperformance 1 d/2026-09-18 e/Bench Press`.<br>
      Expected: `Deleted performance for NAME: bench press, 4 sets of 6 reps at 62.5 kg on 2026-09-18.`
      The card shows `Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (1 record)`.
      The next session and latest measurement rows are unchanged.

1. Rejected deletions

   1. Enter `deleteperformance 1 d/2026-09-18 e/Squat`, then `deleteperformance 1 d/2026-09-14 e/Squat`.<br>
      Expected: `No performance record for Squat on 2026-09-18 was found for NAME.`, then the same message for
      2026-09-14. The expected command format is not shown, and the card is unchanged.
   1. Enter `deleteperformance 1 d/15-09-2026 e/Squat`, then `deleteperformance 1 d/2026-02-30 e/Squat`.<br>
      Expected: `The date must use the format yyyy-MM-dd.`, then `2026-02-30 is not a real date.`,
      each followed by the expected command format. The card is unchanged.
   1. Try a missing `e/` prefix, a repeated `d/` prefix, and an index outside the displayed list.<br>
      Expected: Each command is rejected with an error and the expected command format, without changing records.

1. Persistence after restarting

   1. Enter `exit` and relaunch Spotter.<br>
      Expected: The card still shows the 15 Sep squat with `(1 record)`, together with the session and measurement.

1. Recovering from a failed save

   1. Enter `exit`. Make `data/spotter.json` read-only (on Windows, tick *Read-only* in the file's *Properties*;
      on macOS or Linux, run `chmod 444 data/spotter.json`), then relaunch Spotter.
   1. Enter `deleteperformance 1 d/2026-09-15 e/Squat`.<br>
      Expected: `Unable to save Spotter data.` The card still shows the 15 Sep squat with `(1 record)`.
   1. Enter `exit`, make the data file writable again, and relaunch Spotter.<br>
      Expected: The squat record is still present.

1. Deleting the final record

   1. Enter `deleteperformance 1 d/2026-09-15 e/squat`.<br>
      Expected: `Deleted performance for NAME: Squat, 3 sets of 5 reps at 100 kg on 2026-09-15.`
      The card shows `Latest performance: -`.
   1. Enter `exit` and relaunch Spotter.<br>
      Expected: The card still shows `Latest performance: -`, while the session and measurement remain.

<!-- AI assistance: Claude (Anthropic) helped draft these gym-performance manual tests. -->

### Cancelling and listing sessions

Use a disposable folder containing the JAR and fictional data. Let `DATE` be tomorrow in `YYYY-MM-DD` format;
replace `DATE` in every command below, and `TODAY` with today's date. Add unique test clients if the names already exist.

1. Enter `add n/SessionCancelAda p/91234567`, then `add n/SessionCancelZoe p/92345678`.
   Book them with `session n/SessionCancelAda d/DATE t/0900` and `session n/SessionCancelZoe d/DATE t/1700`.
1. Enter `find Nobody`, then `sessions d/DATE`.<br>
   Expected: Both test clients are visible, Ada before Zoe, with consecutive indices. The date command searches all
   clients, replacing the empty search. If other clients already have bookings that day, they also appear.
1. Enter `find SessionCancelAda`, then `measure 1 d/TODAY t/weight v/72.4`.
   Enter `find SessionCancelAda` again, then `performance 1 d/TODAY e/Squat s/3 r/5 w/100`.
   Re-enter `sessions d/DATE` and note Ada's current index `INDEX` before continuing.
1. Enter `cancelsession INDEX` for Ada.<br>
   Expected: `Cancelled session on DATE 09:00 for SessionCancelAda.` (with tomorrow's date).
   Ada disappears from the date view; Zoe remains.
   Enter `list` and locate Ada. The card shows `Next session: - (none booked)` while both progress rows remain populated.
   Close and restart Spotter. Ada remains unbooked and the contact/progress data are unchanged.
1. Enter `find SessionCancelAda`, then try `cancelsession 1`.<br>
   Expected: `No session is recorded for SessionCancelAda.` No client data or displayed results change.
1. Try `cancelsession 0`, `cancelsession 999999`, `cancelsession 1 2`, `sessions d/2026-02-30`,
   `sessions d/DATE d/DATE` and `sessions d/DATE x/foo`.<br>
   Expected: Invalid index, format or calendar errors, with no changes to clients, the current filter or saved data.
1. Enter `sessions d/0001-01-01` in this test dataset.<br>
   Expected: `No sessions recorded on 0001-01-01.` and an empty panel. `list` restores all clients.
1. For a save-failure test, first book Ada again and display `sessions d/DATE`.
   While Spotter is running, move `data/spotter.json` to a backup and create a directory named `data/spotter.json`.
   Cancel Ada's current displayed index.<br>
   Expected: `Unable to save Spotter data.` The booking and date-filtered list are restored with all progress intact.
   Close Spotter, remove the empty blocking directory and restore the backup file before further testing.

### Upcoming sessions and viewing a client's history

Start from the sample data, which has no bookings. Booking a session moves that client to the top of the client
list, so the indices below follow the list as it changes.

1. Launch Spotter.<br>
   Expected: The right side shows `Upcoming sessions` with `No upcoming sessions.` and how to book one.
1. Enter `session 1 d/2099-12-01 t/1800`, then `session 2 d/2099-11-30 t/0900`.<br>
   Expected: Two cards appear, with the 30 Nov session above the 1 Dec one. The client list shows the same
   two clients at indices 1 and 2.
1. Enter `view 1`.<br>
   Expected: The result box shows `Showing details and history of <name>.` The right side shows that client's
   details and history instead of Upcoming sessions.
1. Enter `measure 1 d/2026-10-20 t/weight v/70.5`.<br>
   Expected: Without running `view` again, the history lists `2026-10-20  weight 70.5 kg` under `Measurements`.
1. Enter `find zzz`.<br>
   Expected: The client list is empty, but the history is still shown.
1. Enter `list`.<br>
   Expected: Upcoming sessions is back, and the 30 Nov card shows the new measurement as its latest.
1. Enter `cancelsession 1`.<br>
   Expected: The 30 Nov card disappears; the 1 Dec card remains.
1. Enter `view 1`, then `delete 1`.<br>
   Expected: Upcoming sessions is back and shows `No upcoming sessions.`
1. Enter `view 0`.<br>
   Expected: `The client index provided is invalid.` The right side does not change.

### UI readability and feedback sizing

Use fictional clients with tags, long names and email addresses, and enough records to require scrolling.
Check the main window at 1920x1080 with 100% and 125% scaling, and 1280x720 with 150% scaling.

1. Enter `list`.<br>
   Expected: A compact confirmation leaves room for both lists. Names and session times stand out from
   supporting details. Tags and selected cards remain readable.
1. Enter `edit` without parameters.<br>
   Expected: The feedback area grows to show more of the usage message. Lines wrap; any remaining text is
   reachable by scrolling inside the feedback area. Resizing the window keeps the message readable.
1. Enter `list` again.<br>
   Expected: The feedback area returns to its compact height and starts at the beginning of the new message.
1. Enter `view 1`, then add or remove a measurement for that client.<br>
   Expected: Distinct Measurements and Performance headings show the correct counts, with records below them
   newest first. An empty section shows `None recorded yet.` and a single record uses `1 record`.
1. Scroll the history, then view another client.<br>
   Expected: The other client's history opens at the top. Updating the same client's records preserves the
   history's scroll position.

### Saving data

Use a copy of the release JAR in a disposable test folder with default configuration and fictional data.
The data file below is `data/spotter.json` inside that folder. Close Spotter before moving, replacing or
editing the file. Keep the backup until all tests are complete.

1. Saving changes across restarts

   1. Launch Spotter and enter `add n/Storage Test Client p/91234567`.<br>
      Expected: The client appears in the list and the data file is created or updated without a save command.
   1. Enter `exit`, relaunch Spotter and enter `find Storage Test Client`.<br>
      Expected: The client is present with phone number `91234567`.
   1. Enter `exit` and copy the data file to `data/spotter-backup.json`.
      Use this backup for the recovery test below.

1. Starting with a missing data file

   1. With Spotter closed, remove `data/spotter.json`, keeping the backup. Relaunch Spotter.<br>
      Expected: Spotter shows its sample clients rather than the previously saved test client.
   1. Enter `exit`.

1. Starting with a corrupted data file

   1. With Spotter closed, create `data/spotter.json` containing only `not valid JSON`. Relaunch Spotter.<br>
      Expected: No clients are shown. Spotter displays
      `Unable to load saved Spotter data. The existing data file has not been modified.`
   1. Enter `list`, then `exit`. Open the data file in a text editor.<br>
      Expected: The file still contains exactly `not valid JSON`; startup, listing and shutdown have not
      overwritten it.
   1. Relaunch Spotter and enter `add n/Recovery Test Client p/92345678`, then `exit`.<br>
      Expected: The successful data-changing command replaces the corrupted file with valid JSON.
   1. Relaunch Spotter.<br>
      Expected: The recovery test client is present with phone number `92345678`, and no load warning appears.
      Enter `exit`.

1. Starting with a data file containing invalid values

   1. With Spotter closed, replace `data/spotter.json` with a copy of `data/spotter-backup.json`. Open it in a
      text editor and change the phone number `91234567` to `12`, keeping the JSON valid. Relaunch Spotter.<br>
      Expected: No clients are shown, not even those with valid values. Spotter displays the same
      load warning as for a corrupted file.
   1. Enter `list`, then `exit`.<br>
      Expected: The data file still contains phone number `12`. Other invalid values, such as an email without
      `@`, a measurement outside its range, or two measurements of the same type on the same date, behave the same.
      Exception: a measurement or performance dated in the future is not invalid. Spotter loads it normally,
      because future dates are only rejected when a record is entered with a command.

1. Restoring a valid backup

   1. With Spotter closed, replace `data/spotter.json` with a copy of `data/spotter-backup.json`.
      Relaunch Spotter and enter `list`.<br>
      Expected: The original saved clients, including Storage Test Client with phone number `91234567`,
      are restored. Recovery Test Client is absent, and no load warning appears.

<box type="warning" seamless>

Preserve a backup before testing corrupted data. The damaged file is retained only until a successful
command changes client data; that command overwrites it with the current records.
</box>

<!-- AI assistance: OpenAI Codex helped draft storage persistence and recovery acceptance tests. -->
