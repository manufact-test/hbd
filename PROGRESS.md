# BdaySquirrel — current progress

Last updated: 2026-08-16

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.2.0`
- Latest working commit from the current package: `4c625510cfc944a4b797cb0f361ef0512dfc1bc6`

## Implemented
- Main birthday list in the approved Retrowave / pixel BdaySquirrel visual style.
- Local Room database for birthdays.
- Add, edit and delete birthday cards.
- Optional photo, note and optional unknown birth year.
- Sorting by nearest birthday and age calculation.
- Approved squirrel branding and launcher icon pipeline.
- Increased top spacing on the main screen using system status bar insets.
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
- GitHub Actions runs tests and builds the debug APK; latest checked package passed CI successfully.

## Next work
1. Real-device QA of the current 0.2.0 package:
   - top spacing;
   - settings button/layout;
   - notification settings persistence;
   - backup/export/import.
2. Add an easy controlled test mode for notifications so reminders can be verified immediately instead of waiting days.
3. Fix any device-specific UI / notification issues found during QA.
4. Continue polishing the MVP before moving to Pro / monetization features.

## Product direction
- Android birthday reminder app.
- Local-first and privacy-focused.
- No ads.
- Free core functionality with an optional lifetime Pro purchase later; no subscription.
- Main promise: enter birthdays once and reliably receive reminders without needing to keep the app open.
