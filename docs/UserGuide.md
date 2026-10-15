---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a client: `add`

Adds a client to Spotter.

Format: `add n/NAME p/PHONE [e/EMAIL] [t/TAG]...`

* `NAME` may contain only English letters (A–Z), digits and spaces, and must be 1 to 100 characters long. Repeated spaces between words are merged into one, and leading or trailing spaces are removed.
* `PHONE` must contain only digits and be 3 to 15 digits long.
* `EMAIL` is optional. A client without an email shows `-` in place of it.
* A client whose name matches an existing client's, ignoring case and extra spaces, is rejected as a duplicate. For example, `john tan` is a duplicate of `John Tan`.

<box type="tip" seamless>

**Tip:** A client can have any number of tags, including zero.
</box>

Examples:
* `add n/Sarah Lim p/91234567`
* `add n/John Tan p/88776655 e/john.tan@example.com t/rehab`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [t/TAG]... `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* A client's email can be changed but not removed yet; `e/` must be followed by a valid email.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a client: `delete`

Deletes the specified client, and all of their records, from Spotter.

Format: `delete INDEX`

* Deletes the client at the specified `INDEX`.
* The index refers to the index number shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd client in Spotter.
* `find Betsy` followed by `delete 1` deletes the 1st client in the results of the `find` command.

### Recording a gym performance: `performance`

Records a client's result for one exercise on a given date, so that you can track their training progress.

Format: `performance INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD`

* Records the performance for the client at the specified `INDEX`, as shown in the displayed client list.
* Each prefix must appear exactly once. The prefixes can be in any order.
* If the client already has a record for the same exercise on the same date, the new sets, repetitions and load
  replace the old record. Spotter keeps one result per exercise per client per day, so you can correct a mistake
  by recording it again.

Parameter | Accepted values
----------|----------------
`INDEX` | A positive integer from the displayed client list, e.g. `1`.
`DATE` | A real date in `yyyy-MM-dd` format, e.g. `2026-09-18`. It can be today or in the past, but not in the future.
`EXERCISE` | 1 to 50 characters: letters, numbers, spaces, hyphens (`-`) and apostrophes (`'`). It must contain at least one letter or number. Extra spaces are ignored, and letter case does not matter when checking for an existing record, so `bench press` and `Bench Press` are the same exercise.
`SETS` | A whole number from 1 to 100.
`REPS` | The repetitions per set, as a whole number from 1 to 1000.
`LOAD` | The load in kg, from 0 to 1000, with at most two decimal places. Use `0` for bodyweight exercises. Do not type the unit.

After a performance is recorded, the client's card shows their latest performance and the number of records,
e.g. `Latest performance: 18 Sep - Bench Press, 3 sets of 8 reps at 60 kg (4 records)`.
If several records share the latest date, the one recorded last is shown.

Examples:
* `performance 2 d/2026-09-18 e/Bench Press s/3 r/8 w/60` records `Recorded performance for <client name>: Bench Press, 3 sets of 8 reps at 60 kg on 2026-09-18.`
* `performance 2 d/2026-09-18 e/Pull-up s/3 r/10 w/0` records a bodyweight exercise.
* `performance 2 d/2026-09-18 e/bench press s/3 r/8 w/62.5` then replaces the first record and shows `Updated performance for <client name>: bench press, 3 sets of 8 reps at 62.5 kg on 2026-09-18.`

If the command is not accepted, Spotter shows the reason followed by the expected format, and no record is added or changed:

Problem | Message
--------|--------
A prefix is missing or repeated, or `INDEX` is missing or not a number | `Invalid command format. Use: performance INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD`
`INDEX` is 0, negative, or larger than the displayed list | `The client index provided is invalid.`
`DATE` is not a real date in `yyyy-MM-dd` format | `The date must use the format yyyy-MM-dd.`
`DATE` is in the future | `The performance date cannot be in the future.`
`EXERCISE` is invalid | `Exercise names must be 1 to 50 characters and may contain only letters, numbers, spaces, hyphens, and apostrophes.`
`SETS` is invalid | `The number of sets must be a whole number between 1 and 100.`
`REPS` is invalid | `The number of repetitions must be a whole number between 1 and 1000.`
`LOAD` is not a number | `Exercise load must be numeric.`
`LOAD` is outside 0 to 1000 | `Exercise load must be between 0 and 1000 kg.`
`LOAD` has more than two decimal places | `Exercise load may have at most two decimal places.`

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.
3. **Unrecognized prefixes are not reported**: text such as `a/x` that uses a prefix Spotter doesn't support is treated as part of the field before it. For example, `add n/John Tan p/91234567 a/x` reports an invalid phone number. The remedy is to remove the unsupported prefix.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE [e/EMAIL] [t/TAG]...` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com t/rehab`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Performance** | `performance INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD`<br> e.g., `performance 2 d/2026-09-18 e/Bench Press s/3 r/8 w/60`
**Help**   | `help`
