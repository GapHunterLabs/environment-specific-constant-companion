<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Environment-Specific Constant Companion Changelog

## [Unreleased]

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
