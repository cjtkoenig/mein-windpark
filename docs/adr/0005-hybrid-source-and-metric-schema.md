# Hybrid source and metric schema

WindKlar will store source and data-quality metadata directly on source-backed master-data tables for wind installations and wind park aggregates, while user-facing impact values such as yearly production, CO2 savings, household equivalents, and municipal participation live in a separate metric model. This keeps official/derived Stammdaten simple to query and lets calculated or simulated values carry their own unit, period, source, quality label, and calculation note.
