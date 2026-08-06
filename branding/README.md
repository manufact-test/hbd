# BdaySquirrel Brand System

**Status:** Approved visual direction, version 1.0  
**Visual style name:** Pixel Carved Retrowave  
**Primary platform:** Android  
**Brand personality:** friendly, warm, memorable, reliable, playful without feeling childish

![BdaySquirrel primary logo](assets/logo/bdaysquirrel-primary-logo.svg)

## 1. Brand idea

BdaySquirrel is a birthday reminder app that helps people store important dates and remember to congratulate the people they care about.

The core brand metaphor is simple:

> A squirrel carefully collects and stores important birthdays the same way a real squirrel collects and stores acorns.

This idea connects the product function, mascot and visual language into one recognizable system. The squirrel represents memory, care and preparation. The acorn represents a saved birthday. A candle placed in the acorn makes the birthday purpose immediately understandable.

BdaySquirrel should feel:

- distinctive and easy to recognize;
- friendly, but not designed only for children;
- warm and emotionally positive;
- slightly nostalgic;
- dependable and calm;
- modern despite its pixel-art influence;
- private and personal rather than corporate.

## 2. Visual direction: Pixel Carved Retrowave

Pixel Carved Retrowave is the proprietary visual direction of BdaySquirrel. It combines:

- crisp pixel art;
- the atmosphere of 1980s arcade interfaces;
- warm, friendly colors;
- carved and stepped geometry;
- a clean contemporary mobile UI;
- short game-like interactions used only where they improve feedback.

The interface should look as if its components were cut from the panels of a friendly old arcade machine. It should not look like generic neon cyberpunk, a full retro game, or a children’s cartoon.

### Defining visual traits

- stepped or diagonally cut corners instead of generic rounded rectangles;
- 2 dp pixel-like borders;
- hard shadows offset by 4 dp, without soft blur;
- a 4 dp spacing grid;
- dark plum and indigo surfaces;
- coral-orange as the main brand accent;
- cream typography instead of pure white;
- mint and lavender used as small functional accents;
- minimal gradients;
- sparse pixel stars, confetti and dithering.

## 3. Design principles

### 3.1 Friendly, not childish

Warmth comes from the coral squirrel, cream typography, gentle microcopy and restrained mascot expressions. Avoid oversized cartoon eyes, exaggerated baby proportions and excessive decoration.

### 3.2 Carved geometry

Standard pill-shaped UI should not define the product. Cards, buttons and panels should use small stepped cuts, diagonal corners or pixel notches. Shapes must remain simple enough to scan quickly.

### 3.3 Clean before decorative

Pixel styling must never reduce usability. A normal product screen should use no more than one large decorative illustration and a few small pixel details. Functional information always comes first.

### 3.4 Dark by default

The primary product theme is dark. The plum background strengthens the Retrowave character, makes the coral mascot stand out and creates a calm evening-friendly atmosphere.

### 3.5 Decoration must communicate

- color communicates status;
- badges communicate urgency;
- animation confirms an action;
- the mascot explains empty or exceptional states;
- borders distinguish interactive elements;
- celebration effects appear only when there is something to celebrate.

## 4. Color system

### 4.1 Foundation colors

| Token | HEX | Primary use |
|---|---|---|
| Night Plum | `#171226` | Main application background |
| Deep Indigo | `#241A3A` | Navigation and large secondary surfaces |
| Card Violet | `#2D2144` | Cards, fields and modal surfaces |
| Raised Violet | `#392A55` | Raised, selected or emphasized surfaces |
| Dark Outline | `#100B1C` | Borders, mascot outline and hard shadows |

### 4.2 Brand colors

| Token | HEX | Primary use |
|---|---|---|
| Squirrel Coral | `#FF7A3D` | Primary brand color, mascot and primary actions |
| Peach | `#FFB36B` | Mascot highlights and warm secondary accents |
| Acorn Brown | `#A85B38` | Acorns and earthy illustration details |
| Candle Cream | `#FFE49A` | Candle, flame highlights and celebration details |

### 4.3 Supporting accents

| Token | HEX | Primary use |
|---|---|---|
| Mint | `#6FE7C8` | Positive status and birthdays coming soon |
| Lavender | `#A98BFF` | Secondary actions and Retrowave accents |
| Sky Pixel | `#6EC8FF` | Occasional information states |
| Danger | `#FF5A6E` | Errors, deletion and destructive actions |

### 4.4 Text colors

| Token | HEX | Primary use |
|---|---|---|
| Cream | `#FFF4D8` | Main headings and high-priority text |
| Soft Cream | `#E8DEC7` | Normal body text |
| Muted Text | `#B8B1CA` | Supporting information |
| Disabled Text | `#777086` | Disabled controls and low-emphasis metadata |
| Dark Text | `#21162B` | Text displayed on light or coral surfaces |

### 4.5 Recommended color distribution

On a typical screen:

- 65–70% Night Plum;
- 15–20% Deep Indigo and Card Violet;
- 8–10% Cream and supporting text colors;
- 5–7% Squirrel Coral;
- no more than 3% Mint, Lavender and other accents combined.

Coral is the primary accent, so it should remain scarce enough to guide attention.

### 4.6 Brand gradient

Gradients are exceptional rather than foundational.

```css
linear-gradient(
  135deg,
  #FF7A3D 0%,
  #FF9A55 48%,
  #A98BFF 100%
)
```

Allowed uses:

- onboarding or splash artwork;
- the Pro page;
- a major celebration illustration;
- a single promotional card;
- rare highlighted badges.

Do not use it across standard cards, form fields, navigation or multiple buttons on the same screen.

## 5. Typography

### 5.1 Display and short headings

**Typeface:** Unbounded  
**Recommended weights:** 500, 600 and occasional 700

Use for:

- screen titles;
- large dates;
- day countdowns;
- short celebration messages;
- onboarding headlines;
- Pro presentation.

Do not use Unbounded for paragraphs or dense settings screens.

### 5.2 Interface typography

**Typeface:** Manrope  
**Recommended weights:** 400, 500, 600 and occasional 700

Use for:

- names and dates;
- body copy;
- settings;
- buttons;
- form fields;
- hints and system messages.

### 5.3 Wordmark

The BdaySquirrel wordmark uses custom pixel lettering. It should preserve stepped corners, large counters and strong readability. It must not resemble an arbitrary downloadable arcade font.

### 5.4 Type scale

| Style | Size | Typeface and weight |
|---|---:|---|
| Display | 32–36 sp | Unbounded 600 |
| H1 | 26–28 sp | Unbounded 600 |
| H2 | 21–22 sp | Unbounded 500 |
| H3 | 18–20 sp | Manrope 700 |
| Body Large | 17–18 sp | Manrope 500 |
| Body | 15–16 sp | Manrope 400 |
| Label | 13–14 sp | Manrope 600 |
| Caption | 11–12 sp | Manrope 500 |

Pixel aesthetics must never be used as a reason to make interface text too small.

## 6. Grid, spacing and sizing

The base spacing unit is **4 dp**.

Recommended spacing values:

`4, 8, 12, 16, 20, 24, 32, 40, 48 dp`

Screen side padding:

- 20 dp on standard phones;
- 24 dp on large devices.

Touch targets:

- minimum interactive area: 48 dp;
- standard primary button height: 52–56 dp;
- prominent primary action: 56 dp.

## 7. Geometry and components

### 7.1 Cards

Standard card treatment:

- background: Card Violet;
- 2 dp border using Raised Violet or Dark Outline;
- small stepped or diagonal corner cuts;
- 4 dp hard shadow;
- 16–20 dp internal padding.

Avoid complex silhouettes. A card must still read as a stable rectangular information block.

### 7.2 Primary buttons

- background: Squirrel Coral;
- text: Dark Text;
- 2 dp Dark Outline border;
- 4 dp hard shadow;
- 56 dp height for the main action;
- stepped or subtly octagonal shape.

Pressed state:

- move the button down by 2–4 dp;
- reduce or remove the lower shadow;
- create the physical feeling of pressing an arcade key.

### 7.3 Secondary buttons

- transparent or Card Violet background;
- Mint or Lavender border;
- Cream text;
- same carved geometry as the primary button, but lower visual weight.

### 7.4 Input fields

- Card Violet background;
- 2 dp border;
- minimum height of 52 dp;
- no default rounded Material pill shape;
- Mint or Coral active border;
- Danger error border;
- placeholder color no dimmer than Muted Text.

## 8. Iconography

Icons follow a consistent pixel system:

- 24 × 24 px construction grid;
- 2 px line or block thickness;
- square line endings;
- no fuzzy anti-aliased edges in exported pixel assets;
- minimal internal details;
- clear silhouettes at small sizes;
- consistent visual density.

Recommended colors:

- Cream for default active icons;
- Muted Text for inactive icons;
- Coral, Mint or Lavender for selected and status states.

Not every utility icon needs to be decorative. Clarity is more important than novelty.

## 9. Logo and mascot

### 9.1 Primary concept

The primary symbol is a pixel-art squirrel shown in profile. Its large curled tail suggests the letter **S**. The squirrel holds an acorn with a small birthday candle.

This single mark communicates:

- the squirrel mascot;
- the initial S;
- stored important dates;
- birthdays;
- care and preparation.

### 9.2 Mascot personality

The squirrel is:

- curious;
- warm;
- clever;
- energetic;
- organized;
- slightly playful;
- never overly childish.

Core expressions may include calm happiness, excitement, surprise, thoughtfulness, sleepiness and celebration.

### 9.3 Mascot colors

- body: Squirrel Coral;
- highlights: Peach;
- outline: Dark Outline;
- acorn: Acorn Brown;
- candle and flame: Candle Cream;
- rare highlight: Cream or Mint.

### 9.4 Required future logo variants

- primary horizontal logo;
- stacked logo;
- symbol without wordmark;
- Android launcher icon;
- compact notification mark;
- light monochrome mark;
- dark monochrome mark;
- favicon;
- splash-screen version.

### 9.5 Current approved concept

The current primary concept is stored in:

- `assets/logo/bdaysquirrel-primary-logo.svg` — editable vector pixel reconstruction;
- `assets/logo/bdaysquirrel-primary-logo.png` — transparent raster export of the approved concept.

The concept is approved as the starting visual direction. Exact geometry may be refined later when constructing the final production icon family and small-size variants.

## 10. App icon direction

The launcher icon should remain recognizable at small sizes.

Recommended composition:

- Night Plum background;
- large squirrel silhouette;
- S-shaped tail;
- acorn with birthday candle;
- restrained Cream or Lavender highlight.

Do not place the full brand name, fine text, tiny confetti, a complex background or thin outlines inside the launcher icon.

## 11. Illustration system

Illustrations use an integer pixel grid and a limited palette.

Recurring objects:

- the squirrel mascot;
- acorns;
- candles and cakes;
- gifts and envelopes;
- pixel stars;
- balloons;
- calendar sheets;
- small planets and retro sparkles.

A small illustration should usually stay within 5–7 colors. All illustration assets must feel as if they belong to the same game world.

## 12. Background decoration

Backgrounds remain calm. Optional patterns include:

- sparse square stars;
- pixel dust;
- checker dithering;
- a thin grid;
- subtle horizontal Retrowave lines;
- tiny acorn motifs.

Recommended opacity: **3–8%**.

Decoration must never interfere with text contrast.

## 13. Birthday card states

A person card may contain:

- avatar;
- name;
- birth date;
- days remaining;
- age when known;
- a status badge;
- a quick action.

### Today

- Coral border;
- `TODAY` badge;
- small celebration highlight;
- restrained pixel confetti;
- highest visual priority.

### Coming soon

- Mint badge;
- standard dark card;
- day countdown emphasized over the full date.

### Later

- Lavender or neutral badge;
- calm presentation;
- no glow or celebration effect.

### Missed or incomplete

- muted presentation;
- small Danger indicator;
- no aggressive warning styling.

## 14. Empty and exceptional states

Empty states should feel inviting rather than broken.

Example concept:

> The squirrel sits beside an empty acorn box.  
> “It is quiet here. Shall we add the first birthday?”

Suggested mascot situations:

- no upcoming birthdays: the squirrel rests;
- notifications disabled: the squirrel holds a silent bell;
- import error: the squirrel studies a damaged calendar;
- setup complete: the squirrel places an acorn into storage.

## 15. Motion system

Motion is short, purposeful and optional.

Recommended timing:

- press feedback: 100–140 ms;
- state transition: 180–240 ms;
- card entrance: 240–320 ms;
- mascot motion: 500–900 ms;
- celebration sequence: up to 1500 ms.

Allowed motion:

- a gentle tail movement;
- blinking;
- a tiny jump;
- storing or revealing an acorn;
- candle flicker;
- pixel confetti;
- star twinkle;
- button press depth;
- an element assembling from pixels.

Avoid permanent background motion, endless confetti, long blocking transitions, excessive shaking, 3D camera effects and motion blur.

## 16. Navigation

The bottom navigation uses:

- Deep Indigo background;
- a pixel-like top border;
- approximately 72–80 dp total height;
- no more than four primary destinations.

Suggested destinations:

- Birthdays;
- Calendar;
- Add;
- Settings.

The Add action may be Coral and slightly elevated, but should use a carved square or octagonal shape rather than a generic circular floating action button.

## 17. Notifications

The notification icon should be simple and monochrome:

- squirrel head and tail silhouette; or
- acorn with a candle.

Notification copy should be warm, brief and immediately useful:

- “It is Anna’s birthday today.”
- “Maxim’s birthday is in 3 days.”
- “Olga’s birthday is tomorrow.”

The brand voice must never delay understanding of the notification.

## 18. Pro presentation

Pro should not resemble a gold casino interface.

Use:

- Peach;
- Candle Cream;
- small Lavender accents;
- an acorn with a star;
- the brand gradient in moderation.

The preferred Pro symbol is a warm golden acorn with a small star. Pro represents optional support and enhancements, not artificial hostility toward free users.

## 19. Accessibility

The decorative system must preserve practical accessibility:

- high contrast for important text;
- color is never the only status indicator;
- statuses also use text or icons;
- touch areas are at least 48 dp;
- system font scaling is supported;
- essential information never depends on animation;
- Reduce Motion is respected;
- no high-frequency flashing.

Cream is preferred over pure white on dark surfaces to reduce visual harshness.

## 20. Brand voice

BdaySquirrel communicates in a way that is:

- concise;
- calm;
- friendly;
- gently playful;
- free of corporate language;
- free of pressure;
- not overloaded with jokes or emoji.

Good examples:

- “Birthday added”
- “Everything is saved”
- “3 days until the celebration”
- “No birthdays here yet”
- “The squirrel remembered it”
- “Notifications are on”

Avoid:

- bureaucratic confirmation language;
- exaggerated error messages;
- pressure such as “URGENTLY congratulate your friend”;
- technical database terminology;
- excessive punctuation and emoji.

## 21. Do not use

The BdaySquirrel visual system does not use:

- generic neon cyberpunk;
- large acid-pink surfaces;
- gradients on every component;
- glassmorphism;
- heavy blur;
- soft realistic shadows;
- default Material cards as the final visual language;
- excessive pill shapes;
- a realistic 3D squirrel;
- baby-cartoon styling;
- unreadable retro game fonts;
- decoration in every empty space;
- pure black `#000000` as the main background;
- pure white `#FFFFFF` as the main text color.

## 22. Final brand image

BdaySquirrel should feel like a friendly game from an alternative version of the 1980s, rebuilt as a modern, reliable and private utility app.

Its core recognizable assets are:

- a dark plum background;
- a coral pixel squirrel;
- an S-shaped tail;
- an acorn with a birthday candle;
- cream typography;
- mint and lavender status accents;
- carved corners;
- hard pixel shadows;
- brief game-like feedback;
- a calm, clear product interface.

### Brand formula

**Friendly squirrel + important dates + carved pixel geometry + warm Retrowave atmosphere.**

> A friendly squirrel that carefully stores important dates in a warm pixel world.
