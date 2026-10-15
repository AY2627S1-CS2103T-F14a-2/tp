---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# Spotter User Guide

Spotter is a **desktop application for freelance personal trainers to manage their clients, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, Spotter can help you manage your clients faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `spotter.jar` file from [here](https://github.com/AY2627S1-CS2103T-F14a-2/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for Spotter.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar spotter.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample client data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all clients.

   * `add n/John Doe p/98765432 e/johnd@example.com` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd client shown in the current list.

   * `clear` : Deletes all clients.

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

* Extraneous parameters for `help`, `exit`, and `clear` are ignored.<br>
  For example, `help 123` is interpreted as `help`. `list` does not accept any extra text (see [`list`](#listing-all-clients-list)).

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

### Listing all clients: `list`

Shows a list of all clients in Spotter, clearing any `find` or `filter` results.

Format: `list`

* Type `list` with nothing after it. Extra spaces are fine, but any other text is rejected with:<br>
  `"list" does not take any parameters.`<br>
  `Example: list`
* On success, Spotter shows `Listed N clients.` (or `Listed 1 client.` when you have exactly one client).
* If you have no clients, Spotter shows `You have no clients yet. Use "add" to add your first client.`

<box type="tip" seamless>

**Tip:** Index numbers change when the displayed list changes (e.g., after `find`, `filter`, `add`, or `delete`). Re-run `list` before using an index-based command such as `edit` or `delete`, so that the index refers to the client you expect.
</box>

### Editing a client: `edit`

Edits an existing client in Spotter.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [t/TAG]... `

* Edits the client at the specified `INDEX`. The index refers to the index number shown in the displayed client list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* A client's email can be changed but not removed yet; `e/` must be followed by a valid email.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st client to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd client to be `Betsy Crower` and clears all existing tags.

### Locating clients by name: `find`

Finds clients whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Clients matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

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

### Clearing all entries: `clear`

Clears all clients from Spotter.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

Spotter saves your data automatically to `[JAR file location]/data/spotter.json` after each command that changes it. You do not need to save manually.

* If saving fails (e.g., the data file or folder is read-only), the change is undone and Spotter shows `Unable to save Spotter data.`
* If the data file is missing, Spotter starts with sample data.
* If the data file is damaged, Spotter starts with no clients and shows `Unable to load saved Spotter data. The existing data file has not been modified.` The damaged file is left untouched until your next successful command that changes data, which overwrites it.

### Editing the data file

Spotter data is saved automatically as a JSON file `[JAR file location]/data/spotter.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, Spotter starts with no clients at the next run and shows a warning. The invalid file remains on disk until your next successful command that changes data, which overwrites it. Therefore, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause Spotter to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous Spotter home folder.

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
**Help**   | `help`
