# AGENTS.md

## Purpose
`Mein Windpark` is a Kotlin Multiplatform app (Android + iOS) that makes wind energy transparent for users in Germany.

## Project Context
This project is part of a university seminar. The app is developed for the Umweltbundesamt as the seminar "customer".

App users are everyone who wants to get to know wind turbines and wind parks, not only expert or institutional users.

Primary user value:
- Discover nearby wind parks.
- Search wind parks quickly and revisit recent parks.
- Understand local production context for a park and its municipality (`Gemeinde`).
- Read a practical FAQ that addresses common wind-energy questions and skepticism.

## Product Scope (Current MVP Direction)
Current Figma-aligned screen set:

1. `Startseite` (`Start`)  
   - Full-screen hero with CTA.
   - No bottom navigation.

2. `Karte` (`Map`)  
   - Map with integrated search, filter chips, map actions, and park preview/sheet patterns.
   - Allows opening park detail and favoriting parks.

3. `Favoriten` (`Favorites`)  
   - Saved parks list with metadata and detail entry.

4. `FAQ` (`Faq`)  
   - Accordion-based content in collapsed/expanded states.

5. `Stats` (`Stats`)  
   - Municipality and production context with chart-based UI.

6. `Profil` (`Profile`)  
   - Welcome/settings/about/logout structure.
   - No real user accounts or authentication in the current MVP.
   - Profile/settings data is mocked for now; logout is a placeholder action.

7. `Windanlage melden` (`ReportWindTurbine`, optional in MVP)  
   - Report form (`Karte Plus`) with image upload, location, type, description, submit.

Cross-flow:
- `Park Detail / Production` for a selected wind park and related municipality data.
- Reachable from map search/results, map preview, and favorites.

Important product decision:
- `Search` is part of `Map` flow (route launched from map search or modal/overlay on top of map), not a bottom-nav tab.

## Data Strategy
- Local-first storage for now: SQLite on device.
- Shared schema files live in:
  - `composeApp/src/commonMain/sqldelight`
- Data layer boundaries:
  - UI -> ViewModel/UseCase -> Repository -> Local DB/DAO
- UI must not call DB/SQL directly.

## Project Structure (Working Contract)
- `composeApp`: shared Kotlin/Compose code for Android + iOS.
- `iosApp`: native iOS launcher.

Inside `composeApp/src/commonMain/kotlin/app`:
- `navigation/`: routes + app nav host.
- `feature/map/`, `feature/search/`, `feature/detail/`, `feature/faq/`, `feature/favorites/`, `feature/stats/`, `feature/profile/`, `feature/report/`: feature UI/state/viewmodel.
- `core/`: shared UI primitives, models, utilities.
- `data/`: repository interfaces, local entities/DAO contracts, seed import contracts.

Platform-specific code must stay thin:
- `androidMain`: Android-only integrations.
- `iosMain`: iOS-only integrations.

## Naming + Conventions
- Use `FeatureScreen`, `FeatureViewModel`, `FeatureUiState`.
- Keep one package per feature.
- Prefer shared code in `commonMain` unless platform API forces otherwise.
- Keep architecture explicit and boring: avoid hidden side effects.
- Bottom nav is owned by `AppNavHost`; do not duplicate bottom-nav ownership inside feature screens.
- Route model should be:
  - `Start`
  - `Map`
  - `Stats`
  - `Favorites`
  - `Faq`
  - `Profile`
  - `ReportWindTurbine` (only if report flow belongs in MVP)
- Do not import Figma-generated React/Tailwind code; use Figma only as layout reference and implement in Compose.
- Chart screens must be real Compose UI (`Compose`/`Canvas`), not static images.

Shared UI building blocks to prefer in `commonMain`:
- `WindKlarTopBar`
- `WindKlarBottomNav`
- `GradientHeaderScaffold`
- `PrimaryButton`
- `SecondaryButton`
- `MetricChip`
- `InfoCard`
- `FaqAccordionItem`
- `ParkListItem`
- `MapPreviewCard`

## Quality Bar for New Tasks
For each feature task:
1. Keep changes scoped to the relevant feature package.
2. Add/adjust repository contract if data behavior changes.
3. Keep navigation behavior explicit (including back behavior).
4. Do not add or update tests. Automated tests are not part of the stated seminar goal for this app.
5. Keep placeholders temporary: replace with production UI/state progressively.

## Current Baseline Notes
- Navigation shell and feature placeholders exist.
- Data layer interfaces exist, but SQLite implementation is still scaffold-level.
- SQL schema files are placeholders and need real table/query definitions.
- Profile UI currently uses mocked state; profile settings persistence and authentication are not implemented yet.

Current schema mapping references:
- favorites -> `composeApp/src/commonMain/sqldelight/app/data/local/db/Favorite.sq`
- search history -> `composeApp/src/commonMain/sqldelight/app/data/local/db/SearchHistory.sq`
- parks -> `composeApp/src/commonMain/sqldelight/app/data/local/db/WindPark.sq`
- production/stats -> `composeApp/src/commonMain/sqldelight/app/data/local/db/Production.sq`

## Non-Goals (for now)
- No backend dependency required for baseline feature development.
- No cross-device sync yet.
- No actual user account or authentication system yet.
- No analytics/telemetry requirement yet.
- No automated test implementation. Do not spend project time writing unit, integration, UI, or snapshot tests.

## UI Build Order (Current Roadmap)
1. implement `Startseite`
2. implement `Karte` with Search and bottom nav shell
3. implement report form `Karte Plus` (`Windanlage melden`)
4. implement `Favoriten`
5. implement `FAQ` with collapsed/expanded state
6. implement `Stats`
7. implement `Profil`

## Definition of Done (per vertical slice)
A feature slice is done when:
- UI flow works on Android + iOS entry points.
- State and actions are wired through ViewModel/repository boundaries.
- Required local persistence is implemented (not just interfaces).
- Behavior is documented in code-level comments where non-obvious.
