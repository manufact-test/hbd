# BdaySquirrel Brand System

**Status:** Approved visual direction, version 1.0  
**Visual style:** Pixel Carved Retrowave  
**Primary platform:** Android  
**Brand personality:** friendly, warm, memorable, reliable, playful without feeling childish

![BdaySquirrel primary logo](assets/logo/bdaysquirrel-primary-logo.svg)

## 1. Brand idea

BdaySquirrel is a private, local-first birthday reminder app that helps people store important dates and remember to congratulate the people they care about.

The central metaphor is simple:

> A squirrel carefully collects and stores important birthdays the same way a real squirrel collects and stores acorns.

The squirrel represents memory, care and preparation. The acorn represents a saved birthday. A candle placed in the acorn makes the birthday purpose immediately understandable.

BdaySquirrel should feel distinctive, friendly, warm, slightly nostalgic, dependable and modern despite its pixel-art influence.

## 2. Visual direction: Pixel Carved Retrowave

Pixel Carved Retrowave combines:

- crisp pixel art;
- the atmosphere of 1980s arcade interfaces;
- warm, friendly colors;
- carved and stepped geometry;
- a clean contemporary mobile UI;
- short game-like interactions used only where they improve feedback.

The product should look as if its components were cut from the panels of a friendly old arcade machine. It must not look like generic neon cyberpunk, a complete retro game or a children’s cartoon.

### Defining traits

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

### Friendly, not childish

Warmth comes from the coral squirrel, cream typography, gentle microcopy and restrained mascot expressions. Avoid oversized cartoon eyes, exaggerated baby proportions and excessive decoration.

### Carved geometry

Cards, buttons and panels use small stepped cuts, diagonal corners or pixel notches. Shapes remain simple enough to scan quickly.

### Clean before decorative

Pixel styling must never reduce usability. Functional information always comes first.

### Dark by default

The primary theme is dark. The plum background strengthens the Retrowave character, makes the coral mascot stand out and creates a calm evening-friendly atmosphere.

### Decoration must communicate

Color communicates status, badges communicate urgency, animation confirms actions, the mascot explains empty states and borders distinguish interactive elements.

## 4. Color system

### Foundation colors

| Token | HEX | Primary use |
|---|---|---|
| Night Plum | `#171226` | Main application background |
| Deep Indigo | `#241A3A` | Navigation and large secondary surfaces |
| Card Violet | `#2D2144` | Cards, fields and modal surfaces |
| Raised Violet | `#392A55` | Raised, selected or emphasized surfaces |
| Dark Outline | `#100B1C` | Borders, mascot outline and hard shadows |

### Brand colors

| Token | HEX | Primary use |
|---|---|---|
| Squirrel Coral | `#FF7A3D` | Primary brand color, mascot and primary actions |
| Peach | `#FFB36B` | Mascot highlights and warm secondary accents |
| Acorn Brown | `#A85B38` | Acorns and earthy illustration details |
| Candle Cream | `#FFE49A` | Candle, flame highlights and celebration details |

### Supporting accents

| Token | HEX | Primary use |
|---|---|---|
| Mint | `#6FE7C8` | Positive status and birthdays coming soon |
| Lavender | `#A98BFF` | Secondary actions and Retrowave accents |
| Sky Pixel | `#6EC8FF` | Occasional information states |
| Danger | `#FF5A6E` | Errors, deletion and destructive actions |

### Text colors

| Token | HEX | Primary use |
|---|---|---|
| Cream | `#FFF4D8` | Main headings and high-priority text |
| Soft Cream | `#E8DEC7` | Normal body text |
| Muted Text | `#B8B1CA` | Supporting information |
| Disabled Text | `#777086` | Disabled controls and low-emphasis metadata |
| Dark Text | `#21162B` | Text displayed on light or coral surfaces |

### Recommended distribution

- 65–70% Night Plum;
- 15–20% Deep Indigo and Card Violet;
- 8–10% Cream and supporting text colors;
- 5–7% Squirrel Coral;
- no more than 3% Mint, Lavender and other accents combined.

### Brand gradient

Gradients are exceptional rather than foundational.

```css
linear-gradient(
  135deg,
  #FF7A3D 0%,
  #FF9A55 48%,
  #A98BFF 100%
)
```

Allowed only for onboarding artwork, the Pro page, major celebration illustrations or a single promotional card.

## 5. Typography

### Display and short headings

**Typeface:** Unbounded  
**Recommended weights:** 500, 600 and occasional 700

Use for screen titles, large dates, countdowns, onboarding headlines and short celebration messages.

### Interface typography

**Typeface:** Manrope  
**Recommended weights:** 400, 500, 600 and occasional 700

Use for names, dates, body copy, settings, buttons, form fields, hints and system messages.

### Wordmark

The BdaySquirrel wordmark uses custom pixel lettering with stepped corners, large counters and strong readability. It must not be replaced by an arbitrary downloadable arcade font.

### Type scale

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

## 6. Grid, spacing and sizing

The base spacing unit is **4 dp**.

Recommended values: `4, 8, 12, 16, 20, 24, 32, 40, 48 dp`.

- Standard phone side padding: 20 dp;
- large-device side padding: 24 dp;
- minimum touch area: 48 dp;
- standard primary button height: 52–56 dp;
- prominent primary action: 56 dp.

## 7. Geometry and components

### Cards

- Card Violet background;
- 2 dp Raised Violet or Dark Outline border;
- small stepped or diagonal corner cuts;
- 4 dp hard shadow;
- 16–20 dp internal padding.

### Primary buttons

- Squirrel Coral background;
- Dark Text label;
- 2 dp Dark Outline border;
- 4 dp hard shadow;
- stepped or subtly octagonal shape.

Pressed state moves down by 2–4 dp while reducing the lower shadow, creating the feeling of pressing an arcade key.

### Secondary buttons

Use a transparent or Card Violet background, Mint or Lavender border and Cream text.

### Input fields

Use Card Violet, a 2 dp border, a minimum height of 52 dp, Mint or Coral focus border and Danger error border. Avoid default Material pill shapes.

## 8. Iconography

Icons follow a consistent pixel system:

- 24 × 24 px construction grid;
- 2 px line or block thickness;
- square endings;
- minimal internal details;
- clear silhouettes at small sizes;
- consistent visual density.

Use Cream for default active icons, Muted Text for inactive icons and Coral, Mint or Lavender for selected states.

## 9. Logo and mascot

### Primary concept

The primary symbol is a pixel-art squirrel shown in profile. Its large curled tail suggests the letter **S**. The squirrel holds an acorn with a small birthday candle.

The mark communicates the squirrel mascot, the initial S, stored important dates, birthdays, care and preparation.

### Mascot personality

The squirrel is curious, warm, clever, energetic, organized and slightly playful, but never overly childish.

### Mascot colors

- body: Squirrel Coral;
- highlights: Peach;
- outline: Dark Outline;
- acorn: Acorn Brown;
- candle and flame: Candle Cream;
- rare highlight: Cream or Mint.

### Approved SVG assets

- `assets/logo/bdaysquirrel-primary-logo.svg` — primary horizontal logo;
- `assets/logo/bdaysquirrel-squirrel-icon.svg` — standalone squirrel mascot icon.

**SVG is the only maintained logo format in this repository.** Do not commit raster logo exports.

### Future SVG variants

- stacked logo;
- Android launcher icon;
- compact notification mark;
- light monochrome mark;
- dark monochrome mark;
- favicon;
- splash-screen version.

## 10. App icon direction

The launcher icon should use a Night Plum background, a large squirrel silhouette, an S-shaped tail and the acorn with birthday candle. Do not place the full brand name, fine text, tiny confetti or thin outlines inside the launcher icon.

## 11. Illustration system

Illustrations use an integer pixel grid and a limited palette. Recurring objects include the squirrel, acorns, candles, cakes, gifts, envelopes, stars, balloons and calendar sheets.

A small illustration should usually stay within 5–7 colors. All artwork must feel as if it belongs to the same game world.

## 12. Background decoration

Optional patterns include sparse square stars, pixel dust, checker dithering, a thin grid, subtle horizontal Retrowave lines and tiny acorn motifs.

Recommended opacity: **3–8%**.

## 13. Birthday card states

### Today

Coral border, `TODAY` badge, small celebration highlight and restrained pixel confetti.

### Coming soon

Mint badge, standard dark card and emphasized day countdown.

### Later

Lavender or neutral badge, calm presentation and no glow.

### Missed or incomplete

Muted presentation and a small Danger indicator without aggressive warning styling.

## 14. Empty and exceptional states

Empty states should feel inviting rather than broken. The mascot may sit beside an empty acorn box, rest when no birthdays are coming, hold a silent bell when notifications are disabled or study a damaged calendar after an import error.

## 15. Motion system

Recommended timing:

- press feedback: 100–140 ms;
- state transition: 180–240 ms;
- card entrance: 240–320 ms;
- mascot motion: 500–900 ms;
- celebration sequence: up to 1500 ms.

Allowed motion includes a gentle tail movement, blinking, a tiny jump, storing an acorn, candle flicker, pixel confetti, star twinkle and button press depth.

Avoid permanent background motion, endless confetti, long blocking transitions, excessive shaking, 3D camera effects and motion blur.

## 16. Navigation

Bottom navigation uses Deep Indigo, a pixel-like top border, approximately 72–80 dp height and no more than four primary destinations: Birthdays, Calendar, Add and Settings.

The Add action may be Coral and slightly elevated, but should use a carved square or octagonal shape rather than a generic circular floating action button.

## 17. Notifications

The notification icon should be simple and monochrome: either the squirrel head and tail silhouette or an acorn with a candle.

Copy must remain warm, brief and immediately useful.

## 18. Pro presentation

Use Peach, Candle Cream, small Lavender accents, an acorn with a star and the brand gradient in moderation. Pro should feel like optional support and enhancement, not a gold casino interface.

## 19. Accessibility

- maintain high contrast for important text;
- never use color as the only status indicator;
- keep touch targets at least 48 dp;
- support system font scaling;
- respect Reduce Motion;
- avoid high-frequency flashing;
- prefer Cream over pure white on dark surfaces.

## 20. Brand voice

BdaySquirrel communicates concisely, calmly and warmly, with gentle playfulness and no corporate language, pressure or excessive emoji.

Good examples:

- “Birthday added.”
- “Everything is saved.”
- “3 days until the celebration.”
- “The squirrel remembered it.”
- “Notifications are on.”

## 21. Prohibited visual choices

Do not use:

- generic neon cyberpunk;
- acid-pink backgrounds;
- gradients everywhere;
- glass cards or heavy blur;
- soft realistic shadows;
- generic Material cards;
- excessive rounded pills;
- realistic 3D squirrels;
- childish mascot proportions;
- unreadable retro-game fonts;
- pure black as the main background;
- pure white as the main text color;
- raster logo assets in the repository.

## 22. Final brand formula

**Friendly squirrel + important dates + carved pixel geometry + warm Retrowave atmosphere.**

> A friendly squirrel that carefully stores important dates in a warm pixel world.
