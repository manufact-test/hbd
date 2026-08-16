# BdaySquirrel — current progress

Last updated: 2026-08-16

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.2.0`
- Latest CI-verified package before the current visual pass: `4c625510cfc944a4b797cb0f361ef0512dfc1bc6`

## Implemented
- Main birthday list in the approved Retrowave / pixel BdaySquirrel visual style.
- Local Room database for birthdays.
- Add, edit and delete birthday cards.
- Optional photo, note and optional unknown birth year.
- Yearless birthday flow now has a dedicated day/month picker: when `Не указывать год рождения` is enabled, the picker contains no visible year.
- Sorting by nearest birthday and age calculation.
- Approved squirrel branding and launcher icon pipeline.
- Increased top spacing on the main screen using system status bar insets.
- Animated Retrowave background and a softly animated gradient hero panel.
- Newly added birthday cards enter with a short pop animation and pixel-burst effect.
- A birthday happening today gets a distinct animated gradient, pulsing border and stronger visual emphasis.
- Entrance effects distinguish genuinely new Room records from the initial database snapshot, so opening an existing list does not explode every card.
- Settings screen.
- Notification permission handling.
- Birthday reminders with configurable time and offsets:
  - same day;
  - 1 day before;
  - 3 days before;
  - 7 days before.
- Reminder rescheduling after birthday/settings changes, reboot, time change, timezone change and app update.
- Local ZIP backup/export and restore/import including birthday data, notes and available photos.
- Android automatic cloud backup disabled to preserve the local-first model.
- Unit tests for reminder date calculation, including leap day and year rollover.
- GitHub Actions runs tests and builds the debug APK.

## Current QA focus
1. Verify the new day/month-only picker on a real device, including February 29.
2. Verify the pixel burst and new-card pop do not affect card measurements or scrolling.
3. Verify today's birthday card stays visually prominent without making text/buttons harder to read.
4. Verify the animated background remains smooth on mid-range Android devices and does not distract from the list.
5. Re-check top spacing, settings, notification persistence and backup/export/import.

## Next work
1. Add an easy controlled test mode for notifications so reminders can be verified immediately instead of waiting days.
2. Fix any device-specific UI / animation / notification issues found during QA.
3. Continue polishing the MVP before moving to Pro / monetization features.

## Product direction
- Android birthday reminder app.
- Local-first and privacy-focused.
- No ads.
- Free core functionality with an optional lifetime Pro purchase later; no subscription.
- Main promise: enter birthdays once and reliably receive reminders without needing to keep the app open.
