# BdaySquirrel — current progress

Last updated: 2026-08-16

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.3.1`
- Latest previously CI-verified package: `6e07c08f7ea9299a3714ecdd6a8bd886a8f953c1` — Android CI run #143 passed unit tests and debug APK assembly.

## Implemented
- Main birthday list in the approved Retrowave / pixel BdaySquirrel visual style.
- Local Room database for birthdays.
- Add, edit and delete birthday cards.
- Optional photo, note and optional unknown birth year.
- One branded custom birthday picker is now used for both modes:
  - day + month only when the year is unknown;
  - day + month + year when the year is known.
- The custom picker adapts to short / square displays, switches to compact spacing and can scroll inside the dialog instead of overflowing the screen.
- Known-year picker prevents future birth dates and provides a horizontally scrollable year strip.
- Yearless mode keeps February 29 available.
- Birthday cards now show zodiac sign plus birth year metadata.
- Birthday cards can be opened in a separate read-only profile sheet instead of forcing edit mode.
- The profile sheet shows birthday date, birth year, zodiac sign, upcoming age, next birthday countdown and the saved note, with a separate Edit action.
- Zodiac boundary logic has unit coverage.
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
- Notification permission is requested contextually after birthday data exists instead of automatically on every fresh install.
- Local ZIP backup/export and restore/import including birthday data, notes and available photos.
- Android automatic cloud backup disabled to preserve the local-first model.
- GitHub Actions runs tests and builds the debug APK.

## Onboarding / contacts import
- Short branded first-run onboarding with animated squirrel.
- Onboarding copy was simplified after tester feedback: removed the local-storage/privacy explainer and unnecessary English product-language terms from the user-facing flow.
- Start choices:
  - import birthdays from Android contacts;
  - add the first birthday manually;
  - restore an existing BdaySquirrel ZIP backup.
- Onboarding is skippable and is not shown again after completion.
- Existing installations with birthday data skip first-run onboarding automatically.
- `READ_CONTACTS` is requested only after the user explicitly chooses contact import.
- Contacts birthday reader supports birthdays with a known year and Android's yearless `--MM-dd` format, including February 29.
- Contact import preview before database changes.
- Multi-select plus select-all / clear-all controls.
- Likely duplicates are detected by normalized name + day + month, marked in the preview and left unselected by default.
- Imported contacts without a birth year stay yearless in BdaySquirrel.
- Empty / denied / retry states are handled in the import flow, including a shortcut to Android app settings.
- Successful import returns directly to the populated BdaySquirrel experience instead of a separate completion page.
- Empty main screen offers both contact import and manual birthday creation.
- Contact import can also be reopened later from Settings.
- Added unit coverage for contact birthday date parsing.

## Current QA focus
1. Adaptive birthday picker on the square/short-screen device that exposed the overflow bug.
2. Known-year custom picker usability and year-strip scrolling.
3. Yearless picker including February 29.
4. Read-only birthday profile opening from the card and transition into edit mode.
5. Zodiac boundaries and birth-year display for known / unknown years.
6. Simplified onboarding on a clean install.
7. Contacts permission: allow, deny, retry and system-settings path.
8. Contact import with known-year and yearless birthdays.
9. Existing animation, backup and reminder regressions.

## Roadmap — next work

### 1. Real-device QA of 0.3.1 — NEXT
Test the new picker/profile package on the target devices and fix any layout or interaction edge cases.

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
