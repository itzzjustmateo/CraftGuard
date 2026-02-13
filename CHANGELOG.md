# Changelog

All notable changes to this project will be documented in this file.

This project uses [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and adheres to [Semantic Versioning](https://semver.org/).

## [1.4.0] - 2026-02-13

### Added

- **Interactive GUI system**: New chest-based interface for managing features.
  - Accessible via `/cg` (without arguments).
  - Categorized into **Workstations** (Crafting, Anvil, Furnace, etc.) and **Portals** (Nether, End).
  - Visual indicators: Green Wool for enabled, Red Wool for disabled.
  - Automatic pagination and centered item layouts.
- Added a "Back to Main Menu" navigation option in all sub-GUIs.

### Changed

- Command `/cg` now opens the GUI for players; `/cg help` remains chat-based.
- Upgraded project version to 1.4.0.
- Refactored `CraftGuardCommand` to integrate with the new `GUIManager`.

---

## [1.3.0] - 2026-02-10

### Added

- **Container Control**: New module to restrict access to chests, shulker boxes, and other containers.
- **WorldGuard Integration**: Support for region-based bypasses using the custom flag `cg-interaction-bypass`.
- **Async Audit Logging**: High-performance logging system to track blocked interactions in `audit_log.txt`.
- Added `craftguard.bypass.containers` permission.

### Changed

- Upgraded project version to 1.3.0.
- Refactored internal listener logic for better extensibility.

---

## [1.2.2] - 2026-02-09

### Added
- Improved world name detection in PlaceholderAPI placeholders to handle underscores in world names (e.g., `world_nether`, `my_custom_world`).
- Updated `worlds.yml` template so features like `anvil` now default to `true` to prevent accidental global blocks after installing.

### Changed
- Refactored `ConfigManager` to use `ConcurrentHashMap` for all internal world state storage (thread-safe).
- Tab completion now avoids suggesting irrelevant features for subcommands like `help` or `reload`.
- Standardized feature naming: `crafting-table` and `crafting` are now unified as `crafting` in all filters, listeners, and configuration.

### Fixed
- Removed unused fields and cleaned up special-case checks for better command maintainability.
- Resource cleanup on plugin disable now fully removes stale references to prevent potential memory leaks.

---

## [1.2.1] - 2026-02-09

### Added
- Custom messages support both `{placeholder}` and `<placeholder>` formats.
- Removed unused `skills` folder for better repository organization.

### Changed
- `/cg` and `/cg help` commands are now available to all players, regardless of permissions.
- Unified filter and config naming: always use `crafting` as the key for filtering and toggling.

### Fixed
- Fixed bug where `{world}` and `{type}` in feature-blocked messages were not replaced.
- Ensured caches and resources are fully cleared on plugin disable/reload to prevent leaks.

---

## [1.2.0] - 2026-02-09

### Added
- Each workstation and portal now has its own toggle (`crafting`, `nether-portal`, `end-portal`, `anvil`, `furnace`, `blast-furnace`, `smoker`, `enchanting`, `brewing`, `smithing`, `loom`, `cartography`, `grindstone`, `stonecutter`).
- `/cg <world> <type> <on|off|toggle>` for precise feature control.
- Nether and End portal toggles are now split.
- `PortalListener` and `WorkstationListener` block features as required.
- Added permission `craftguard.bypass.<type>` (as well as `craftguard.bypass.*`) for granular bypass.
- Added PAPI placeholders for every feature type.

### Changed
- Permissions are now hardcoded, no longer defined in config.yml.
- Help menu redesigned for clarity and improved readability.
- Existing `worlds.yml` is migrated automatically to support granular toggles.

### Fixed
- Improved validation and error messaging in commands.

---

## [1.1.1] - 2026-02-07

### Fixed
- Changed default for `craftguard.bypass` permission from `op` to `false`, so server operators must grant themselves bypass intentionally.

---

## [1.1.0] - 2026-02-07

### Added
- `/cg reload` command to reload configuration files instantly (requires `craftguard.admin` permission).

---

## [1.0.2] - 2026-02-01

### Fixed
- Fixed memory leaks and dangling references on reload/disable.
  - Static instance is now nullified on disable.
  - Event listeners unregistered explicitly.
  - Added null checks in `onDisable`.
- Switched `worldStatesCache` to `ConcurrentHashMap` for thread safety.
- Prevented PAPI stale references by setting `persist()` to `false` and rechecking availability on reloads.
- Removed unused imports and fields; versioning consistency improved.

---

## [1.0.1] - 2026-02-01

### Added
- `/cg` and `/cg help` commands showing version, commands, permission nodes, and customizable messages (now a list in config).
- Help command now appears in tab completion suggestions.

### Changed
- Help messages format switched to a list under `help:` in config. (Supports empty lines as `""`).
- Message prefix now referenced from `plugin.yml`. Updated version: `1.0.1-SNAPSHOT`.

### Fixed
- Improved documentation consistency in configuration files.

---

## [1.0.0] - 2026-02-01

### Added
#### Core Features
- Per-World crafting control:
  - `/cg <world> on` - Enable
  - `/cg <world> off` - Disable
  - `/cg <world> toggle` - Toggle
- Bypass permissions for players.
- Configurable default crafting state for new worlds.

#### Configuration
- All plugin messages are configurable in `config.yml`.
  - Supports color codes and MiniMessage formatting.
  - Placeholders: `{world}`, `{state}`, `{player}`, `{version}`
- Toggle global message prefix.
- Customizable permissions for admin/bypass.
- Command action word aliases are configurable.
- Configurable event priority and option to ignore cancelled events.
- Debug logging and world state caching.
- Configurable auto-save interval.

#### Commands & Permissions
- `/craftguard`, `/cguard`, `/cg` commands.
- Tab completion for worlds and actions.
- Permissions:
  - `craftguard.admin` (default: OP)
  - `craftguard.bypass` (default: OP)

#### PlaceholderAPI
- Placeholders:
  - `%craftguard_world%`
  - `%craftguard_world_state%`
  - `%craftguard_world_<worldname>%`
- Works even if PlaceholderAPI is not installed.

#### Technical
- In-memory smart caching for world states.
- Two config files: `config.yml` & `worlds.yml`.
- Paper API 1.21.11 and Java 21 support.
- Build with Maven. Code is fully Javadoc commented.

#### Console/User
- Startup, shutdown, and configuration state messages.
- Commands and events registration confirmations.
- Notification when integration with PlaceholderAPI is detected.
- Players are notified when crafting is blocked (configurable).
- Debug logs, blocked crafting attempts, and bypass events.
- Clear errors for invalid commands/worlds.

---

## Version Comparison

| Feature                | v1.0.1 | v1.0.2 | v1.1.0 | v1.1.1 | v1.2.0 | v1.2.1 | v1.2.2 | v1.4.0 |
| ---------------------- | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |
| Per-World Control      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Configurable Messages  | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Message Prefix System  | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| PlaceholderAPI Support | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Debug Mode             | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Tab Completion         | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Help Command           | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| List-Based Help Config | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| State Persistence Fix  | ❌      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Memory Leak Fixes      | ❌      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Java 21 Support        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Paper 1.21.11 Support  | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Reload Command         | ❌      | ❌      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| OP Bypass Fix          | ❌      | ❌      | ❌      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Granular Toggles       | ❌      | ❌      | ❌      | ❌      | ✅      | ✅      | ✅      | ✅      |
| Public Help Command    | ❌      | ❌      | ❌      | ❌      | ❌      | ✅      | ✅      | ✅      |
| Thread-Safe Cache      | ❌      | ❌      | ❌      | ❌      | ❌      | ❌      | ✅      | ✅      |
| Interactive GUI        | ❌      | ❌      | ❌      | ❌      | ❌      | ❌      | ❌      | ✅      |

---

## Migration Guides

### Upgrade: 1.0.0 → 1.0.1

No breaking changes. All configuration continues to work. Optionally, update help messages to the new list format:

```yaml
messages:
  help:
    - "&7&m---- &r &aCraftGuard v{version} &7&m----"
    - "&7Your custom help text here"
    # ... more lines
```

No further action required.

---

## Notes

- **Semantic Versioning**: [MAJOR.MINOR.PATCH](https://semver.org)
  - MAJOR: breaking changes
  - MINOR: new, backward compatible features
  - PATCH: backward compatible bug fixes

- **Snapshot Builds**: Versions ending in `-SNAPSHOT` are development builds and may not be stable.

---

## Links

- [GitHub Repository](https://github.com/itzzmateo/CraftGuard)
- [Report a Bug](https://github.com/itzzmateo/CraftGuard/issues)
- [Feature Requests & Questions](https://github.com/itzzmateo/CraftGuard/discussions)

---

[1.4.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.2.2...v1.3.0
[1.2.2]: https://github.com/itzzmateo/CraftGuard/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.1.1...v1.2.0
[1.1.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.2...v1.1.0
[1.0.2]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/itzzmateo/CraftGuard/releases/tag/v1.0.0
