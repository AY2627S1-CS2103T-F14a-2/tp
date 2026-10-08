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

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

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

* 3d. The given date and time are in the past.

    * 3d1. Spotter shows an error message stating that the next session must be in the future.

      Use case resumes at step 3.

* 3e. The client already has a next session.

    * 3e1. Spotter informs the trainer that the existing next session will be replaced.

      Use case resumes at step 4.

* 4a. Another client's next session is at the same date and time.

    * 4a1. Spotter keeps the recorded session and warns the trainer about the clashing session.

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

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
