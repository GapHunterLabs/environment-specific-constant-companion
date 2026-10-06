<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Environment-Specific Constant Companion Changelog

## [Unreleased]

### Added

- A description page for the inspection in **Settings | Editor |
  Inspections**, which showed "Under construction".

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

## [0.1.1]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.0]

### Added

- New inspection: flags a hardcoded URL or `host:port` string literal
  in Java source that also appears, textually, as a value in one of
  the project's own config files (`.properties`/`.yml`/`.yaml`/
  `.env`) -- the value clearly belongs to config, and the code copy
  can now silently drift out of sync with it. Exact string match only;
  a bare port number or bare hostname is never matched.

[Unreleased]: https://github.com/GapHunterLabs/environment-specific-constant-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/environment-specific-constant-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/environment-specific-constant-companion/commits/0.1.0
