# BdaySquirrel 0.4.0 — Find & Calendar

## Goal
Make large birthday collections easy to navigate without turning BdaySquirrel into a generic calendar.

Status: implemented in version 0.4.0.

## Scope

- Fast search by person name and saved notes, with multi-word matching.
- Nearest-birthday view remains the default.
- Dedicated month browsing with month counts and a quick selector.
- Initial month jump targets the current or next populated month.
- Existing cards, animations, edit actions and profile navigation are reused in both views.
- Search and browse state are isolated from birthday data and reminder scheduling.
- Useful empty results provide a direct reset action.
- Unit coverage protects search, counts, sorting and relevant-month selection.

## Design principles

- Retrowave pixel identity remains unchanged.
- Finding birthdays should be faster, not more complicated.
- The calendar view is only for birthday discovery.
