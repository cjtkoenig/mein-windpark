# Shared or data-driven map for MVP

WindKlar will not require separate Android and iOS native map implementations for the MVP. The map should stay shared or data-driven, preferably OSM-compatible when a suitable integration is available, so the Kotlin Multiplatform app can demonstrate Germany-wide wind data, clustering, filtering, and detail entry without splitting core map behavior across platform-specific stacks.
