# BdaySquirrel

BdaySquirrel is an Android birthday reminder app with a friendly pixel-art Retrowave identity.

## Current milestone

Current app version: **0.5.0**.

The core experience is working and real-device verified:
- local birthday database;
- add / edit / delete birthdays;
- birthdays with or without a known year;
- branded adaptive birthday picker;
- zodiac sign and birth-year metadata;
- read-only person profile with notes and birthday details;
- configurable birthday notifications;
- contacts birthday import with preview, multi-select and duplicate detection;
- first-run onboarding;
- local ZIP backup / restore;
- animated Retrowave UI;
- fast search by name and notes;
- birthday browsing by calendar month.

Version **0.5.0 — Birthday Greetings** adds offline greeting suggestions inside the person profile:
- no AI, network API or server dependency;
- deterministic local generation from the saved name, birthday-derived zodiac sign and optional upcoming age;
- three tones: short, warm and light humor;
- multiple repeatable variants per person and tone;
- one-tap copy to the Android clipboard;
- no Room schema or backup-format changes.

The next product milestone is the Android home-screen birthday widget.

After that the planned sequence is:
1. Android home-screen birthday widget;
2. import/data polish where real-world testing requires it;
3. release reliability pass;
4. production signing, Google Play release and monetization foundation.

See `PROGRESS.md` for the detailed roadmap and implementation status.

## Product model

- All functionality is available to every active user; there is no separate Free vs Pro feature set.
- New production users receive the complete app for the first six calendar months.
- After the six-month full-access period, continued normal use requires an annual subscription.
- Target subscription price: **$2.99 per year**.
- Subscription management and entitlement restoration are handled through Google Play Billing.
- No ads are planned.
- Trial/subscription expiration never deletes local birthday data.
- Debug/test builds do not start or consume the production six-month access period.

## Project structure

- `branding/` — brand system, design tokens, logo and mascot assets
- `app/` — Android application source code
- `PROGRESS.md` — current implementation status, QA focus and product roadmap

## Visual direction

**Pixel Carved Retrowave** — dark plum surfaces, coral-orange squirrel mascot, cream typography, mint and lavender accents, carved pixel geometry and restrained arcade-inspired motion.
