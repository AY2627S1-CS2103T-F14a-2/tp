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

### Recording body measurements: `measure`

Records a client's weight, body fat percentage or waist measurement.

Format: `measure INDEX d/DATE t/TYPE v/VALUE`

* `INDEX` is the positive index in the currently displayed client list.
* `DATE` is a real date in `yyyy-MM-dd` format, today or earlier.
* `TYPE` is case-insensitive: `weight` (kg), `bodyfat` (%) or `waist` (cm).
* Weight accepts 1–500 kg; body fat accepts values greater than 0 and up to 100%; waist accepts 1–300 cm.
* Values accept up to two decimal places. Enter only the number, without units, commas or scientific notation.
  Surrounding whitespace is ignored.
* Each prefix must appear exactly once. Prefixes may appear in any order.
* The same date and type replaces the previous value without increasing the record count.
* Records are saved automatically and restored when the application restarts.
* The client card shows the measurement with the latest date and the total record count, for example:
  `Latest measurement: 12 Sep - weight 72.4 kg (4 records)`.
* When multiple types share the latest date, the card prefers weight, then body fat, then waist.
* Clients without measurements show `Latest measurement: -`.
* The count includes all dates and types. Adding an older measurement increases the count but does not change
  the displayed latest measurement. A single record is shown as `(1 record)`.

Examples:

Run `list` first and use the displayed index of your intended client. These examples use client 1.

* `measure 1 d/2026-09-12 t/weight v/72.4` records a weight measurement.
* `measure 1 d/2026-09-12 t/weight v/71.8` updates that record.
* `measure 1 d/2026-09-12 t/waist v/81` adds a separate waist record.
* `measure 1 d/2026-09-12 t/bodyfat v/18.4` adds a separate body fat record.

Success messages begin with `Recorded measurement for` or `Updated measurement for`,
followed by the client's name, measurement and date. Invalid input is rejected without changing records.

For a client named Alex Tan, the first two examples produce:

```text
Recorded measurement for Alex Tan: weight = 72.4 kg on 2026-09-12.
Updated measurement for Alex Tan: weight = 71.8 kg on 2026-09-12.
```

Common errors:

| Input problem | Message |
| --- | --- |
| Missing or repeated prefix | `Invalid command format. Use: measure INDEX d/DATE t/TYPE v/VALUE` |
| Invalid client index | `The client index provided is invalid.` |
| Invalid date or date format | `The date must use the format yyyy-MM-dd.` |
| Future date | `The measurement date cannot be in the future.` |
| Unsupported type | `Measurement type must be weight, bodyfat, or waist.` |
| Non-numeric value | `Measurement value must be numeric.` |
| Too many decimal places | `Measurement values may have at most two decimal places.` |

Out-of-range values produce a message stating the permitted range for that type.
Parsing errors and invalid client indexes also show the expected command format.

If saving fails, Spotter displays `Unable to save Spotter data` and restores the previous client records
and displayed list. The failed measurement is not retained in memory.

<!-- AI assistance: OpenAI Codex helped draft the measurement command documentation. -->

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

### Viewing a client's history: `view`

Shows one client's details together with all of their measurements and gym performances, so that you can review
their progress before a session.

Format: `view INDEX`

* Shows the client at the specified `INDEX`, as shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, ...
* The details appear in the result box below the command box. The client list and cards do not change, and no data is changed.
* Records are listed with the newest date first.
  * Measurements on the same date are listed in the order weight, body fat, waist.
  * Performances on the same date are listed with the one recorded last first, so the first performance line
    always matches the card's `Latest performance`.
* `Next session` shows the same text as the client's card.
* Tags are listed alphabetically. A client with no email or no tags shows `-`.

Examples:
* `list` followed by `view 2` shows the history of the 2nd client in Spotter.
* `find Alex` followed by `view 1` shows the history of the 1st client in the results of the `find` command.

For a client named Alex Tan with three measurements and two performances, `view 1` shows:

```text
Alex Tan
Phone: 9123 4567
Email: alex@example.com
Next session: Fri 23 Oct, 6:00pm
Tags: knee, rehab

Measurements (3 records, newest first):
2026-10-12  weight 72.4 kg
2026-10-12  waist 81 cm
2026-09-30  weight 73 kg

Performance (2 records, newest first):
2026-10-15  Squat, 3 sets of 5 reps at 100 kg
2026-10-01  Bench Press, 3 sets of 8 reps at 60 kg
```

A client with no records of a kind shows `Measurements: none recorded yet.` or `Performance: none recorded yet.`
instead of that list. A single record is shown as `(1 record, newest first)`.

Problem | Message
--------|--------
`INDEX` is missing or not a whole number, or there is extra text after it | `Invalid command format!` followed by the expected format
`INDEX` is 0, negative, or larger than the displayed list | `The client index provided is invalid.`

<box type="tip" seamless>

**Tip:** `view` shows the client's records at the moment you run it. Re-run `view` after changing that client's records to see the latest history.
</box>

<!-- AI assistance: Claude Code helped draft the view command documentation. -->

### Clearing all entries: `clear`

Clears all clients from Spotter.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Recording a client's next session: `session`

Records one next session for an existing client. A new booking replaces that client's previous booking.

Format: `session INDEX d/DATE t/TIME` or `session n/NAME d/DATE t/TIME`

* `INDEX` is the positive number shown on the **current displayed list**, including a filtered list.
* `NAME` must match a client's full name. Matching ignores case, leading/trailing whitespace, and repeated
  internal spaces. Names cannot be empty or contain `/`.
* `DATE` must be a real date in `YYYY-MM-DD` format, such as `2026-12-15`, and cannot be before the current
  system date. Today is accepted even if the specified time has passed.
* `TIME` must use 24-hour `HHMM` or `HH:MM`, such as `1900` or `19:00`. Midnight is `0000`; `2400` is invalid.
* Supply exactly one identifier and one of each required prefix. Unknown or repeated prefixes are rejected.

Examples (use today's date or a later date):

* `session 1 d/2026-12-15 t/1900`
* `session n/Sarah Lim d/2026-12-15 t/19:00`
* `session n/sarah lim d/2026-12-15 t/1900`

Success: `Recorded next session for Sarah Lim on 2026-12-15 at 19:00`.
The stored client's name is used in the message; both time formats are stored and reported as `HH:mm`.
The booking is saved automatically and survives restarting Spotter. Editing contact details preserves it,
and deleting the client removes it together with the client.

After a successful booking, all clients are shown. Upcoming sessions appear first, soonest first, with
case-insensitive alphabetical names breaking ties. Expired recorded sessions follow upcoming ones, followed
by clients with no recorded session; unbooked clients are alphabetical. This order also applies to `list`,
filtered lists, and startup. Sorting the displayed list does not reorder the JSON file.
**Index numbers can change after a booking or a client change. Check the displayed list before any index-based command.**

The card shows `Next session: Tue 15 Dec, 7:00pm` for an upcoming booking, `Next session: - (none booked)`
when no session has been recorded, and `Next session: - (none upcoming)` when the recorded booking has passed.
Expired bookings are retained in the data file until replaced.

Invalid commands leave client data and list order unchanged. The relevant messages are:

Error | Message
------|--------
Missing/repeated/unknown prefix or both identifiers | `Invalid command format! Use: session INDEX d/DATE t/TIME or session n/NAME d/DATE t/TIME`
Invalid index | `The client index provided is invalid.`
Unknown name | `No client with that name exists in Spotter.`
Empty name | `Name cannot be empty.`
Name containing a slash | `Name must not contain '/'.`
Malformed date or component out of range | `Date must use YYYY-MM-DD format (year 0001-9999, month 01-12, day 01-31).`
Impossible calendar date (e.g. 30 February) | `Date must be a valid calendar date.`
Date before today | `Date cannot be in the past.`
Invalid time | `Time must be a valid time in HHMM or HH:MM format.`

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
**Session** | `session INDEX d/DATE t/TIME` or `session n/NAME d/DATE t/TIME`<br> e.g., `session 1 d/2026-12-15 t/1900`
**Measure** | `measure INDEX d/DATE t/TYPE v/VALUE`<br> e.g., `measure 1 d/2026-09-12 t/weight v/72.4`
**Performance** | `performance INDEX d/DATE e/EXERCISE s/SETS r/REPS w/LOAD`<br> e.g., `performance 2 d/2026-09-18 e/Bench Press s/3 r/8 w/60`
**View** | `view INDEX`<br> e.g., `view 1`
**Help**   | `help`
