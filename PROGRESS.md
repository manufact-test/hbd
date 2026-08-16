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
- Real-device reminder delivery has been successfully verified by the project owner.
- Local ZIP backup/export and restore/import including birthday data, notes and available photos.
- Android automatic cloud backup disabled to preserve the local-first model.
- Unit tests for reminder date calculation, including leap day and year rollover.
- GitHub Actions runs tests and builds the debug APK.

## Current QA focus
1. Verify the new day/month-only picker on a real device, including February 29.
2. Verify the pixel burst and new-card pop do not affect card measurements or scrolling.
3. Verify today's birthday card stays visually prominent without making text/buttons harder to read.
4. Verify the animated background remains smooth on mid-range Android devices and does not distract from the list.
5. Re-check backup/export/import round trips.

## Roadmap — next work

### 1. First-run onboarding + contacts birthday import — NEXT
Build onboarding around the actual first user goal: get birthdays into BdaySquirrel quickly and enable reliable reminders.

Planned flow:
1. Short branded welcome screen with the squirrel and the promise: birthdays stay on the device and BdaySquirrel reminds the user automatically.
2. Choose how to start:
   - `Import from contacts`;
   - `Add manually`;
   - `Restore backup` for an existing user.
3. For contact import, request Contacts permission only after the user explicitly chooses import.
4. Read contacts that contain birthday information and show a preview before changing the BdaySquirrel database.
5. Let the user multi-select birthdays, including `Select all` / `Clear all`.
6. Preserve birthdays without a known year.
7. Detect likely duplicates against birthdays already stored in BdaySquirrel and clearly mark them before import.
8. Show a compact import result summary and allow immediate editing of imported cards.
9. Explain birthday notifications in-context and request notification permission only when it becomes relevant.
10. Finish onboarding on the populated main screen rather than with a dead-end success page.

Rules:
- onboarding must be skippable;
- manual birthday entry must always work without Contacts permission;
- contacts and birthdays are never uploaded anywhere;
- declining either Contacts or notification permission must not block the app;
- onboarding is shown only for a genuine first run, with a Settings action available later for import/restore.

### 2. Main-screen onboarding polish
- useful empty state when there are no birthdays yet;
- prominent `Import from contacts` and `Add birthday` actions in the empty state;
- after import, naturally transition into the normal birthday list;
- lightweight contextual hints instead of a long tutorial;
- keep the Retrowave/pixel personality and mascot motion without slowing down setup.

### 3. Bulk-import polish
- clear import result summary;
- duplicate/conflict handling;
- easy edit after import;
- graceful handling when Contacts contains no birthdays;
- retry/open system settings path when Contacts permission is permanently denied.

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
