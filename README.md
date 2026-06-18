# windklar

`windklar` is a Kotlin Multiplatform app for Android and iOS that makes wind energy more transparent for users in Germany.

The app helps users discover nearby wind parks, search for specific parks, revisit recent parks, understand local production context for a wind park and its `Gemeinde`, and read practical answers to common questions and skeptical critiques about wind energy.

## Product Documentation

- [WindKlar PRD](docs/product/WindKlar_PRD.md)
- [Domain Context](CONTEXT.md)
- [Architecture Decisions](docs/adr)

## Product Scope

The current MVP direction follows the Figma screens and route model below.

### Startseite (`Start`)

- Full-screen hero entry with CTA.
- No bottom navigation on this screen.

### Karte (`Map`)

- Display wind parks on a map.
- Include search directly in the map flow (not as a bottom-nav tab).
- Support filter chips, map actions, preview card/sheet behavior, and park detail entry.
- Allow favoriting parks.

### Favoriten (`Favorites`)

- Show saved parks with key park metadata.
- Open park detail from favorites.

### FAQ (`Faq`)

- Explain core wind-energy topics in practical language.
- Support collapsed and expanded accordion states.

### Stats (`Stats`)

- Show municipality and production context with chart-based UI.
- Implement charts as Compose/Canvas UI, not static exported images.

### Profil (`Profile`)

- Show profile/settings/about/logout structure.
- Use mocked profile/settings data for now.
- There are no real user accounts or authentication in the current MVP.
- Treat logout as a placeholder action until account/auth behavior is introduced.
- Persist profile settings through a preferences/settings store later if needed.

### Windanlage melden (`ReportWindTurbine`, optional in MVP)

- Report form flow (`Karte Plus`) with image upload, location, type, description, and submit action.

### Park Detail / Production (cross-flow)

- Show production-related data for a selected wind park.
- Connect the park view with relevant municipality (`Gemeinde`) context.
- Be reachable from map search/results, map preview, and favorites.

## Navigation Model

Bottom navigation is owned by `AppNavHost` and should not be duplicated in feature screens.

Current route model:

- `Start`
- `Map`
- `Stats`
- `Favorites`
- `Faq`
- `Profile`
- `ReportWindTurbine` (only if report flow is in MVP)

Search is part of the map flow (dedicated route launched from map search or modal/overlay on top of `Map`), not a bottom-nav item.

## Data Strategy

The app is local-first for now. Data is stored on the device with SQLite.

- Shared SQLDelight schema files live in `composeApp/src/commonMain/sqldelight`.
- UI code must not call SQLite or SQL directly.
- Data access should follow this boundary:

```text
UI -> ViewModel/UseCase -> Repository -> Local DB/DAO
```

Current schema mapping targets:

- favorites -> `composeApp/src/commonMain/sqldelight/app/data/local/db/Favorite.sq`
- search history -> `composeApp/src/commonMain/sqldelight/app/data/local/db/SearchHistory.sq`
- parks -> `composeApp/src/commonMain/sqldelight/app/data/local/db/WindPark.sq`
- production/stats -> `composeApp/src/commonMain/sqldelight/app/data/local/db/Production.sq`

There is no backend dependency, cross-device sync, analytics, user account, or authentication requirement in the current baseline.

## Project Structure

This is a Kotlin Multiplatform project targeting Android and iOS.

- `composeApp`: shared Kotlin and Compose Multiplatform code.
- `iosApp`: native iOS launcher.

Shared app code lives under `composeApp/src/commonMain/kotlin/app`:

- `navigation/`: routes and app navigation host.
- `feature/map/`: map screen, state, and view model.
- `feature/search/`: search logic/state used from the map flow.
- `feature/detail/`: wind park detail and production context flow.
- `feature/faq/`: FAQ screen, state, and content.
- `feature/favorites/`, `feature/stats/`, `feature/profile/`, `feature/report/`: remaining MVP feature packages.
- `core/`: shared UI primitives, models, and utilities.
- `data/`: repository interfaces, local entities/DAO contracts, and seed import contracts.

Platform-specific code should stay thin:

- `commonMain`: shared UI screens, state, repository interfaces, and cross-platform logic.
- `androidMain`: Android-only integrations, such as permissions or Android-specific map SDK bindings.
- `iosMain`: iOS-only integrations.

## Development Conventions

- Use `FeatureScreen`, `FeatureViewModel`, and `FeatureUiState` naming.
- Keep one package per feature.
- Prefer shared code in `commonMain` unless a platform API requires otherwise.
- Keep navigation behavior explicit, including back behavior.
- Add or adjust repository contracts when data behavior changes.
- Do not add or update tests in this seminar project.
- Replace temporary placeholders progressively with production UI and state.
- Do not import Figma-generated React/Tailwind code. Use Figma as layout reference and implement directly in Compose.

Shared reusable UI components to prefer in `commonMain`:

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

## Current Baseline

- Navigation shell and feature placeholders exist.
- Data layer interfaces exist.
- SQLite implementation is still scaffold-level.
- SQL schema files are placeholders and need real table and query definitions.
- Profile UI currently uses mocked state; profile settings persistence and authentication are not implemented yet.

## UI Implementation Order (Current Roadmap)

1. Startseite (`Start`)
2. Karte (`Map`) with integrated Search and bottom-nav shell
3. Windanlage melden (`Karte Plus` / `ReportWindTurbine`)
4. Favoriten (`Favorites`)
5. FAQ (`Faq`) with collapsed/expanded states
6. Stats (`Stats`)
7. Profil (`Profile`)

## Build And Run

### Android

Run the Android app from the IDE run configuration, or build it from the terminal:

```shell
./gradlew :composeApp:assembleDebug
```

On Windows:

```shell
.\gradlew.bat :composeApp:assembleDebug
```

### iOS

Open `iosApp` in Xcode and run the iOS app from there. The shared UI and app logic are provided by the `composeApp` module.

## Definition Of Done

A vertical feature slice is considered done when:

- The UI flow works through the Android and iOS entry points.
- State and actions are wired through ViewModel and repository boundaries.
- Required local persistence is implemented, not only declared through interfaces.
- Navigation behavior is explicit.
- Non-obvious behavior is documented in focused code comments.
