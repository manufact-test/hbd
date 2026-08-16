# BdaySquirrel — current progress

Last updated: 2026-08-16

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.2.0`
- Latest CI-verified visual package: `3eea49090f2482f63ad55328693ab4fa9a29220c`

## Implemented
- Main birthday list in the approved Retrowave / pixel BdaySquirrel visual style.
- Local Room database for birthdays.
- Add, edit and delete birthday cards.
- Optional photo, note and optional unknown birth year.
- Yearless birthday flow has a dedicated day/month picker: when `Не указывать год рождения` is enabled, the picker contains no visible year.
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

## Roadmap — next work

### 1. Notification test mode — NEXT
Add a controlled test tool in Settings so a tester can trigger a real BdaySquirrel notification immediately.

Requirements:
- one-tap `Send test notification` action;
- use the same notification channel/style as real birthday reminders;
- clearly show whether notification permission is granted;
- do not modify birthday data or scheduled real reminders;
- useful for real-device QA without changing the system date or waiting for a reminder window.

### 2. Contacts birthday import
Reduce the biggest onboarding friction: manually entering many birthdays.

Requirements:
- optional Contacts permission; manual entry remains fully available without it;
- read contacts that contain birthday information;
- preview and multi-select before import;
- preserve birthdays that do not have a known year;
- detect likely duplicates against existing BdaySquirrel cards;
- never upload contacts or birthday data anywhere.

### 3. Bulk-import polish
- clear import result summary;
- duplicate/conflict handling;
- easy edit after import;
- useful empty-state prompt for first-time users.

### 4. Reliability / device QA pass
- notification delivery on common Android vendors and battery-management modes;
- animation performance on mid-range devices;
- backup/export/import round-trip testing;
- regression tests for date edge cases and notification scheduling.

### 5. Release preparation
- production signing/release pipeline;
- privacy policy/store listing assets;
- crash-free release checklist;
- final onboarding and permission copy;
- release-only entitlement clock foundation for the future paid-access model.

## Monetization model — UPDATED
BdaySquirrel does **not** have a Free tier vs Pro tier and does **not** hide individual features behind feature-level paywalls.

The intended model is:
1. A new production user receives the **complete application with all functionality unlocked for the first 365 days**.
2. The 365-day period starts from the production entitlement/trial start, not from internal debug/test builds.
3. During that year there are no ads, no feature restrictions and no artificial limits on birthdays/reminders/import/backup.
4. After the first year, continued normal use requires payment for the application.
5. The preferred payment is a **one-time non-consumable purchase**, not a recurring subscription.
6. Google Play Billing must support purchase restoration after reinstall/device change when the same Play account is used.
7. Expiration must never delete local birthday data. An expired user should still be able to reach purchase/restore flows and a safe backup/export path.
8. The app should warn clearly before the free year ends rather than surprise the user at the exact expiry moment.

### Monetization architecture rule
Do not build future features as `free` vs `pro` variants. Features are either part of BdaySquirrel or not.

Before the public release, introduce a small centralized entitlement layer with states such as:
- `FULL_YEAR_ACTIVE`
- `PAYMENT_REQUIRED`
- `PURCHASED`

All screens should read one entitlement source instead of implementing billing checks individually. Actual Google Play Billing integration can be completed closer to the production release, after the core birthday experience is stable.

## Product direction
- Android birthday reminder app.
- Local-first and privacy-focused.
- No ads.
- Full functionality for the first year; paid continuation afterward.
- No feature-split Free/Pro model.
- No recurring subscription planned; preferred unlock is a one-time purchase after the first year.
- Main promise: enter birthdays once and reliably receive reminders without needing to keep the app open.
