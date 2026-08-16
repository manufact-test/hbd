# BdaySquirrel — current progress

Last updated: 2026-08-16

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.3.0`
- Latest CI-verified package: `6e07c08f7ea9299a3714ecdd6a8bd886a8f953c1` — Android CI run #143 passed unit tests and debug APK assembly.

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
- Birthday reminders with configurable time and offsets:
  - same day;
  - 1 day before;
  - 3 days before;
  - 7 days before.
- Reminder rescheduling after birthday/settings changes, reboot, time change, timezone change and app update.
- Real-device reminder delivery has been successfully verified by the project owner.
- Notification permission is now requested contextually after birthday data exists instead of automatically on every fresh install.
- Local ZIP backup/export and restore/import including birthday data, notes and available photos.
- Android automatic cloud backup disabled to preserve the local-first model.
- GitHub Actions runs tests and builds the debug APK.

## 0.3.0 onboarding / import package
- Short branded first-run onboarding with animated squirrel.
- Start choices:
  - import birthdays from Android contacts;
  - add the first birthday manually;
  - restore an existing BdaySquirrel ZIP backup.
- Onboarding is skippable and is not shown again after completion.
- Existing installations with birthday data skip first-run onboarding automatically.
- `READ_CONTACTS` is requested only after the user explicitly chooses contact import.
- Contacts birthday reader supports birthdays with a known year and Android's yearless `--MM-dd` format, including February 29.
- Contact import preview before database changes.
- Multi-select plus `Select all` / `Clear all` controls.
- Likely duplicates are detected by normalized name + day + month, marked in the preview and left unselected by default.
- Imported contacts without a birth year stay yearless in BdaySquirrel.
- Contact data is read locally and is never uploaded.
- Empty / denied / retry states are handled in the import flow, including a shortcut to Android app settings.
- Successful import returns directly to the populated BdaySquirrel experience instead of a separate completion page.
- Empty main screen now offers both contact import and manual birthday creation.
- Contact import can also be reopened later from Settings.
- Added unit coverage for contact birthday date parsing.

## Current QA focus
1. First-run onboarding on a clean install.
2. Upgrade behavior for an existing installation with stored birthdays.
3. Contacts permission: allow, deny, retry and system-settings path.
4. Contact import with known-year and yearless birthdays, including February 29.
5. Duplicate marking and default selection behavior.
6. Import of many contacts and transition back to the main birthday list.
7. Manual-start and backup-restore onboarding paths.
8. Contextual notification permission card after the first birthday exists.
9. Existing animation, backup and reminder regressions.

## Roadmap — next work

### 1. Real-device QA of 0.3.0 — NEXT
Use the new package on actual Android devices and fix any provider/vendor-specific contact-format or permission behavior.

### 2. Bulk-import polish
- optional search/filter when the contact birthday list is long;
- stronger duplicate/conflict controls if tester data reveals ambiguous matches;
- easy edit immediately after an imported card needs correction;
- refine copy and spacing from tester screenshots.

### 3. First-use polish
- tune onboarding motion and pacing from real-device feedback;
- refine empty-state presentation;
- add only lightweight contextual hints where testers actually get stuck.

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
- centralized entitlement layer for the six-month full-access period and annual subscription;
- Google Play Billing subscription integration and restore/resubscribe flows.

## Monetization model — UPDATED
BdaySquirrel does **not** have a Free tier vs Pro tier and does **not** hide individual features behind feature-level paywalls.

The intended model is:
1. A new production user receives the **complete application with all functionality unlocked for the first six calendar months**.
2. The six-month period starts from the production entitlement/trial start, not from internal debug/test builds.
3. During those six months there are no ads, no feature restrictions and no artificial limits on birthdays/reminders/import/backup.
4. After the six-month full-access period, continued normal use requires an **annual subscription**.
5. Target subscription price: **$2.99 per year**, with Google Play handling localized storefront pricing where applicable.
6. The subscription is managed through Google Play Billing and renews according to the user's Play subscription settings until cancelled.
7. Subscription entitlement must be restored automatically after reinstall/device change when the same eligible Google Play account is used.
8. Trial expiration or subscription expiration must never delete local birthday data. A user without an active entitlement should still be able to reach subscription/restore flows and a safe backup/export path.
9. The app should warn clearly before the six-month period ends and before access changes, rather than surprise the user at the exact expiry moment.

### Monetization architecture rule
Do not build future features as `free` vs `pro` variants. Features are either part of BdaySquirrel or not.

Before the public release, introduce a small centralized entitlement layer with states such as:
- `FULL_TRIAL_ACTIVE`
- `SUBSCRIPTION_REQUIRED`
- `SUBSCRIPTION_ACTIVE`

All screens should read one entitlement source instead of implementing billing checks individually. Google Play Billing integration should remain isolated from birthday/reminder/domain logic so billing state cannot corrupt or delete local birthday data.

Debug/test builds must not start or consume the production six-month access period.

## Product direction
- Android birthday reminder app.
- Local-first and privacy-focused.
- No ads.
- Complete functionality for the first six calendar months.
- After that, annual subscription for continued normal use.
- Target subscription price: $2.99/year.
- No feature-split Free/Pro model.
- Main promise: enter birthdays once and reliably receive reminders without needing to keep the app open.
