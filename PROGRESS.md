# BdaySquirrel — current progress

Last updated: 2026-09-01

## Repository / branch
- Repository: `manufact-test/hbd`
- Active development branch: `feat/android-mvp-scaffold`
- Current app version: `0.5.0`
- Baseline before 0.5.0: `87b7af79edb593a6a4b9a1f7701a3223e4950a27` — Android CI run #171 passed unit tests, debug APK assembly and artifact upload.
- `0.3.1` has been verified by the project owner on a real Android device, including the adaptive birthday picker and read-only birthday profile.
- `0.4.0` Find & Calendar is implemented on the active branch and was CI-verified before the 0.5.0 milestone started.

## Stable implemented foundation
- Main birthday list in the approved Retrowave / pixel BdaySquirrel visual style.
- Local Room database for birthdays.
- Add, edit and delete birthday cards.
- Optional photo, note and optional unknown birth year.
- One branded custom birthday picker for both modes:
  - day + month only when the year is unknown;
  - day + month + year when the year is known.
- Picker adapts to short / square displays, uses compact spacing where needed and scrolls inside the dialog instead of overflowing.
- Known-year picker prevents future birth dates and provides a horizontally scrollable year strip.
- Yearless mode supports February 29.
- Birthday cards show zodiac sign and birth year metadata.
- Normal tap on a birthday card opens a separate read-only profile.
- Profile shows birthday date, birth year, zodiac sign, upcoming age, countdown and note, with a separate Edit action.
- Zodiac boundary logic and date edge cases have unit coverage.
- Sorting by nearest birthday and age calculation.
- Approved squirrel branding and launcher icon pipeline.
- Correct system status-bar inset handling.
- Animated Retrowave background, animated hero panel, new-card pixel burst and special birthday-today state.
- Settings screen.
- Birthday reminders with configurable time and offsets:
  - same day;
  - 1 day before;
  - 3 days before;
  - 7 days before.
- Reminder rescheduling after birthday/settings changes, reboot, time change, timezone change and app update.
- Real-device notification delivery verified by the project owner.
- Notification permission is requested contextually after birthday data exists.
- Local ZIP backup/export and restore/import including birthday data, notes and available photos.
- Android automatic cloud backup disabled.
- GitHub Actions runs tests and builds the debug APK.

## Stable onboarding / contacts import
- Short branded first-run onboarding with animated squirrel.
- User-facing onboarding copy is concise and Russian-first; unnecessary privacy/storage explanations and English UI terminology were removed.
- Start choices:
  - import birthdays from Android contacts;
  - add the first birthday manually;
  - restore an existing BdaySquirrel ZIP backup.
- Onboarding is skippable and not shown again after completion.
- Existing installations with birthday data skip first-run onboarding automatically.
- `READ_CONTACTS` is requested only after the user explicitly chooses contact import.
- Contacts birthday reader supports known-year and Android yearless `--MM-dd` birthdays, including February 29.
- Contact import preview before database changes.
- Multi-select plus select-all / clear-all controls.
- Likely duplicates are detected by normalized name + day + month, marked and left unselected by default.
- Imported contacts without a birth year stay yearless.
- Empty / denied / retry permission states are handled, including a shortcut to Android app settings.
- Successful import returns directly to the populated main screen.
- Empty main screen offers contact import or manual creation.
- Contact import can be reopened later from Settings.
- Contact birthday parsing has unit coverage.

## Find & Calendar — 0.4.0
- Fast in-memory search across person names and saved notes.
- Multi-word search is case-insensitive and keeps the complete collection untouched.
- The nearest-birthday list remains the default view.
- A clear `По месяцам` switch opens birthday-only month browsing.
- Month chips show birthday counts and support a quick jump to any populated month or all months.
- Opening the month view jumps to the current populated month or the next relevant one, wrapping to the start of the year when needed.
- Month sections keep existing birthday cards, tap-to-open profiles, edit/delete actions and visual effects.
- Search and month empty states provide direct recovery actions.
- Search, grouping, sorting, counts and relevant-month selection have unit coverage.

## Birthday Greetings — 0.5.0
- Greeting suggestions live inside the existing read-only birthday profile.
- The feature is entirely local and deterministic:
  - no AI/LLM dependency;
  - no network API;
  - no backend/service account;
  - no new Android network permission.
- Suggestions use only already available birthday-domain data:
  - saved person name;
  - zodiac sign derived from day/month;
  - upcoming age only when the birth year is known;
  - upcoming celebration year as part of the deterministic variant seed.
- Three user-facing tones:
  - `Коротко`;
  - `Тепло`;
  - `С юмором`.
- Each zodiac sign has a maintained local phrase pool with deliberately soft wording; astrology is used as playful personalization, not as factual personality inference.
- Universal opening, wish, closing and optional age phrase pools combine with zodiac-specific wording to create many variants without storing generated text.
- `Ещё вариант` changes the deterministic variant index; reopening the same person/tone starts from a stable base suggestion rather than random network-generated output.
- `Скопировать` places the complete suggestion in the Android clipboard.
- Unknown birth year never invents an age.
- Existing notes are intentionally not parsed or inserted into greetings in this version because the note field can contain arbitrary private or practical information.
- No `BirthdayEntity` fields were added, so there is no Room migration.
- Backup/export/import formats remain unchanged.
- Reminder scheduling remains unchanged.
- The greeting engine is isolated from Compose UI and covered by unit tests for determinism, variant uniqueness, known/unknown age handling and all 12 supported zodiac signs.

## Current QA status
Version 0.5.0 is implemented on top of the CI-verified 0.4.0 baseline. The active branch CI must continue to pass:
1. all debug unit tests;
2. debug APK assembly;
3. artifact upload.

Real-device UX verification should focus on:
1. profile scrolling on short / square displays;
2. tone-selector readability;
3. long greeting wrapping;
4. `Ещё вариант` responsiveness;
5. Android clipboard feedback;
6. no regressions to profile editing or birthday-card navigation.

Keep regression coverage for:
1. reminders and rescheduling;
2. backup/export/import round trips;
3. contact import and duplicate handling;
4. leap-day / yearless dates;
5. square / short-screen picker layout;
6. animation performance on mid-range Android devices.

## Roadmap — next work

### 1. 0.6.0 — Android home-screen widget — NEXT
Add a useful glanceable surface outside the app:
- next birthday or next few birthdays;
- countdown in days;
- birthday-today state;
- tap opens the relevant profile/app;
- update automatically when birthday data changes and as dates roll over;
- preserve the BdaySquirrel pixel/Retrowave identity without making the widget noisy;
- no network dependency.

### 2. 0.6.x — Data / import polish
Only add complexity where real data needs it:
- search/filter inside very long contact-import previews;
- stronger duplicate/conflict handling if ambiguous matches appear;
- easier correction immediately after import;
- graceful re-import when contacts have changed;
- backup format/version compatibility checks before public release.

### 3. 0.7.0 — Release reliability pass
- notification delivery checks on common Android vendors and battery-management modes;
- animation/performance pass on mid-range devices;
- backup/export/import regression pass;
- date, timezone, leap-day and notification scheduling regression suite;
- greeting-layout regression on small displays;
- production error/crash review;
- accessibility and small-screen pass.

### 4. 0.8.0 — Production release + monetization foundation
- production signing and release pipeline;
- Google Play package/release configuration;
- privacy policy and store listing assets;
- centralized entitlement layer for the six-month full-access period;
- annual Google Play subscription product at the target price of $2.99/year;
- purchase, restore, expired, cancelled and resubscribe flows;
- clear pre-expiry reminders before the six-month period ends;
- debug/test builds must never consume the production access period;
- expiration must never delete local birthday data;
- safe backup/export path remains available when subscription is inactive.

## Monetization model
BdaySquirrel does **not** have a Free tier vs Pro tier and does **not** hide individual features behind feature-level paywalls.

The intended model is:
1. A new production user receives the complete application with all functionality unlocked for the first **six calendar months**.
2. The six-month period starts from the production entitlement/trial start, not from internal debug/test builds.
3. During those six months there are no ads, feature restrictions or artificial birthday/import/backup limits.
4. After the six-month full-access period, continued normal use requires an **annual subscription**.
5. Target subscription price: **$2.99 per year**, with Google Play handling localized storefront pricing where applicable.
6. Subscription is managed through Google Play Billing and renews according to the user's Play subscription settings until cancelled.
7. Entitlement should restore after reinstall/device change for the same eligible Google Play account.
8. Trial or subscription expiration never deletes birthday data.
9. Users should be warned clearly before access changes.

### Monetization architecture rule
Do not build future features as `free` vs `pro` variants. Features are either part of BdaySquirrel or not.

Before public release, use one centralized entitlement source with states such as:
- `FULL_TRIAL_ACTIVE`
- `SUBSCRIPTION_REQUIRED`
- `SUBSCRIPTION_ACTIVE`

Google Play Billing must remain isolated from birthday/reminder/domain logic so billing state cannot corrupt or delete local birthday data.

## Product direction
- Android birthday reminder app.
- Local-first and privacy-focused internally, without overloading normal users with privacy copy in everyday UI.
- No ads.
- Complete functionality for the first six calendar months.
- After that, annual subscription for continued normal use.
- Target subscription price: $2.99/year.
- No feature-split Free/Pro model.
- Main promise: add birthdays once, find people quickly, get useful greeting ideas and reliably receive reminders without keeping the app open.
