# Changelog

All notable changes to CraftGuard will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1] - 2026-02-09

### Added

- **Enhanced Placeholder Support**: Now supports both `{placeholder}` and `<placeholder>` formats in custom messages for greater flexibility.
- **Maintenance**: Removed unused `skills` folder from the repository for cleaner organization.

### Changed

- **Help Command Accessibility**: `/cg` and `/cg help` can now be used by all players, regardless of their permissions.
- **Unified Feature Naming**: Consolidated `crafting-table` and `crafting` under the single `crafting` feature key for consistency.

### Fixed

- **Message Placeholder Bug**: Resolved an issue where `{world}` and `{type}` placeholders were not being replaced correctly in "feature-blocked" messages.
- **Resource Optimization**: Ensured explicit cache clearing and resource disposal occur during plugin disable or reload to prevent potential memory leaks.


---

## [1.2.0] - 2026-02-09

### Added

- **Granular Feature Control**: Every workstation and portal now has its own toggle.
  - Supported types: `crafting`, `nether-portal`, `end-portal`, `anvil`, `furnace`, `blast-furnace`, `smoker`, `enchanting`, `brewing`, `smithing`, `loom`, `cartography`, `grindstone`, `stonecutter`.
- **New Command Syntax**: Updated `/cg <world> <type> <on|off|toggle>` for better clarity and control.
- **Split Portal Control**: Nether and End portals can now be toggled independently.
- **New Listeners**: Implemented `PortalListener` and `WorkstationListener` for broad feature blocking.
- **Granular Bypass Permissions**: Fixed permissions to `craftguard.bypass.<type>` or `craftguard.bypass.*`.
- **Granular Placeholders**: Added new PAPI placeholders for all feature types.

### Changed

- **Hardcoded Permissions**: Permissions are now hardcoded and removed from `config.yml` for consistency.
- **Redesigned Help Menu**: cleaner layout and better readability.
- **World State Migration**: automatically migrates existing `worlds.yml` to the new granular format.

### Fixed

- Improved command validation and error messaging.

---

## [1.1.1] - 2026-02-07

### Fixed

- **Crafting Bypass Bug**: Changed default `craftguard.bypass` permission from `op` to `false`.
  - Fixes issue where server operators could not test crafting restrictions.
  - Operators now need to explicitly grant themselves the bypass permission.

---

## [1.1.0] - 2026-02-07

### Added

- **Reload Command**: Added `/cg reload` to reload configuration without restarting the server.
  - Requires `craftguard.admin` permission.
  - Reloads `config.yml` and `worlds.yml` instantly.

---

## [1.0.2] - 2026-02-01

### Fixed

- **Critical Lifecycle Issues**: Fixed memory leaks and stale references during plugin reloads
  - Static instance now properly nullified on disable
  - Explicit event listener unregistration to prevent duplicate events
  - Added null-safe checks in `onDisable` to prevent crashes
- **State Persistence Improvements**:
  - Changed `worldStatesCache` to `ConcurrentHashMap` for thread safety
  - Fixed PlaceholderAPI stale references by changing `persist()` to `false`
  - Re-checks PAPI availability on every reload/enable
- **Code Quality**:
  - Removed unused imports and fields
  - Updated versioning for consistency

---

## [1.0.1] - 2026-02-01

### Added

- **Help Command System**: Added `/cg` and `/cg help` commands that display plugin information
  - Shows plugin version dynamically
  - Lists all available commands
  - Displays configured permission nodes
  - Fully customizable help messages in config.yml as a list format
  - Includes "help" in tab completion suggestions

### Changed

- **Help Messages Format**: Restructured help section in config.yml from individual keys to a cleaner list format
  - Before: `help-header`, `help-description`, etc.
  - After: Single `help:` list with all lines
  - Easier to customize and maintain
  - Supports empty lines with `""`
- **Message Prefix Configuration**: Changed error message reference from `paper-plugin.yml` to `plugin.yml`
- Updated version from `1.0.0-SNAPSHOT` to `1.0.1-SNAPSHOT`

### Fixed

- Improved consistency in configuration file documentation

---

## [1.0.0] - 2026-02-01

### Added - Initial Release

#### Core Features

- **Per-World Crafting Control**: Enable, disable, or toggle crafting in any world
  - `/cg <world> on` - Enable crafting in a world
  - `/cg <world> off` - Disable crafting in a world
  - `/cg <world> toggle` - Toggle crafting state
- **Bypass Permissions**: Allow specific players to craft regardless of world settings
- **Default Crafting State**: Configurable default state for new worlds

#### Configuration System

- **100% Configurable Messages**: All user-facing messages customizable in config.yml
  - Support for legacy color codes (`&a`, `&c`, etc.)
  - Support for MiniMessage formatting (`<green>`, `<red>`, etc.)
  - Placeholder support: `{world}`, `{state}`, `{player}`, `{version}`
- **Message Prefix System**: Global prefix for all player-facing messages
  - Can be enabled/disabled via `prefix.enabled`
  - Customizable prefix text
  - Automatically excluded from console and status messages
- **Custom Permissions**: Define your own permission nodes
  - Configurable admin permission (default: `craftguard.admin`)
  - Configurable bypass permission (default: `craftguard.bypass`)
- **Command Aliases**: Customizable action words
  - Enable aliases: `on`, `enable`, `true` (configurable)
  - Disable aliases: `off`, `disable`, `false` (configurable)
  - Toggle aliases: `toggle` (configurable)
- **Event Configuration**:
  - Configurable event priority (LOWEST to MONITOR)
  - Option to ignore already-cancelled events
- **Advanced Settings**:
  - Debug mode for detailed logging
  - World state caching for performance
  - Configurable auto-save interval

#### Commands & Permissions

- **Main Command**: `/craftguard` with aliases `/cguard` and `/cg`
- **Tab Completion**: Smart suggestions for worlds and actions
  - Configurable action suggestions
  - Option to include/exclude world names
  - Case-insensitive matching
- **Permissions**:
  - `craftguard.admin` - Manage crafting settings (default: OP)
  - `craftguard.bypass` - Bypass crafting restrictions (default: OP)

#### PlaceholderAPI Integration

- **Custom Placeholders** (requires PlaceholderAPI):
  - `%craftguard_world%` - Current world name
  - `%craftguard_world_state%` - Current world crafting state
  - `%craftguard_world_<worldname>%` - Specific world state
- **Soft Dependency**: Plugin works without PlaceholderAPI installed

#### Technical Features

- **Smart Caching**: World states cached in memory for performance
- **Two Configuration Files**:
  - `config.yml` - Messages, settings, permissions, advanced options
  - `worlds.yml` - Per-world crafting states (auto-generated)
- **Paper API 1.21.11** support
- **Java 21** compatibility
- **Maven Build System** with clean project structure
- **Well-Documented Code**: JavaDoc comments for all public methods

#### Console Messages

- Configurable startup/shutdown messages
- Configuration load confirmation
- Command registration status
- Event listener registration status
- PlaceholderAPI integration status

#### User Experience

- **Notification System**: Players receive messages when trying to craft in disabled worlds
  - Configurable via `notify-on-craft-attempt`
- **Debug Logging**: Optional detailed logging for troubleshooting
  - Command executions with world and action
  - Blocked crafting attempts
  - Permission bypass events
- **Error Handling**: Clear error messages for invalid commands and missing worlds

---

## Version Comparison

### Quick Feature Matrix

| Feature                | v1.0.1 | v1.0.2 | v1.1.0 | v1.1.1 | v1.2.0 | v1.2.1 |
|------------------------|--------|--------|--------|--------|--------|--------|
| Per-World Control      | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Configurable Messages  | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Message Prefix System  | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| PlaceholderAPI Support | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Debug Mode             | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Tab Completion         | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Help Command           | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| List-Based Help Config | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| State Persistence Fix  | ❌     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Memory Leak Fixes      | ❌     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Java 21 Support        | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Paper 1.21.11 Support  | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
| Reload Command         | ❌     | ❌     | ✅     | ✅     | ✅     | ✅     |
| OP Bypass Fix          | ❌     | ❌     | ❌     | ✅     | ✅     | ✅     |
| Granular Toggles       | ❌     | ❌     | ❌     | ❌     | ✅     | ✅     |
| Public Help Command    | ❌     | ❌     | ❌     | ❌     | ❌     | ✅     |

---

## Migration Guides

### Upgrading from 1.0.0 to 1.0.1

No breaking changes. Your existing configuration will continue to work. However, you may want to:

1. **Update help messages** (optional): The new list format is cleaner:

   ```yaml
   # Old format still works, but new format is recommended:
   messages:
     help:
       - "&7&m---- &r &aCraftGuard v{version} &7&m----"
       - "&7Your custom help text here"
       # ... more lines
   ```

2. No action required for functionality - the plugin will work exactly as before.

---

## Notes

- **Semantic Versioning**: We follow [SemVer](https://semver.org/) - MAJOR.MINOR.PATCH
  - MAJOR: Breaking changes
  - MINOR: New features (backward compatible)
  - PATCH: Bug fixes (backward compatible)

- **Snapshot Builds**: Versions ending in `-SNAPSHOT` are development builds and may be unstable.

---

## Links

- **GitHub Repository**: [CraftGuard on GitHub](https://github.com/itzzmateo/CraftGuard)
- **Issues**: [Report a Bug](https://github.com/itzzmateo/CraftGuard/issues)
- **Discussions**: [Feature Requests & Questions](https://github.com/itzzmateo/CraftGuard/discussions)

---

[1.2.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.1.1...v1.2.0
[1.1.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.2...v1.1.0
[1.0.2]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/itzzmateo/CraftGuard/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/itzzmateo/CraftGuard/releases/tag/v1.0.0
