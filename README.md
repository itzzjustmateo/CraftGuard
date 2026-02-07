<p align="center">
  <h1 align="center">CraftGuard</h1>
  <img src="https://img.shields.io/badge/CraftGuard-v1.1.1-brightgreen?style=for-the-badge" alt="CraftGuard Logo" />
  <a href="https://papermc.io/"><img src="https://img.shields.io/badge/Paper-1.21.11-blue?style=for-the-badge" alt="Paper" /></a>
  <a href="https://www.oracle.com/java/"><img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge" alt="Java" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License" /></a>
</p>

> **⚠️ About FlameAntiCraft/FlameCraft**  
> CraftGuard picks up where my old anti-crafting plugin, [FlameAntiCraft (FlameCraft)](https://modrinth.com/plugin/flameanticraft), left off.  
> FlameAntiCraft/FlameCraft won’t get any more updates—if you want new features, improvements, or fixes, CraftGuard is the place to be!

**Tired of players crafting where you don't want them to? CraftGuard lets you control crafting, world by world.**

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [Configuration](#-configuration) • [For Developers](#-for-developers)

---

## 📋 What is CraftGuard?

CraftGuard is a simple plugin that lets you turn crafting on or off for specific worlds in your Minecraft server.
If you’re running minigames, creative hubs, or want extra control in survival, this plugin makes it easy—with clear commands and tons of customization.

### Why use CraftGuard?

- ⚡ **Lightweight** – Won’t slow down your server
- 🎛️ **Fully Customizable** – Tweak every message, permission, and setting
- 🌎 **Choose Your Worlds** – Decide exactly where crafting can happen
- 🧩 **PlaceholderAPI Support** – Easy integration with other plugins and leaderboards
- 🎨 **Modern Chat Formatting** – Color codes and MiniMessage support
- 👨‍💻 **Dev-Friendly** – Source is tidy, readable, and built with Maven

---

## ✨ Features

### Core

- Toggle crafting in any world—on, off, or toggle with a simple command
- Grant bypass permissions to certain players so they can always craft
- All messages can be edited (including colors, MiniMessage, etc.)
- Message prefix system (enable/disable globally)
- Tab-completion for commands and world names
- Debug mode for pinpointing plugin problems
- Seamless PlaceholderAPI integration

### Advanced Settings

- Customizable permission nodes
- Change command action words (on/off/enable/etc)
- Set plugin event priority—choose when CraftGuard checks crafting
- Caching & auto-save tweaks for large servers

---

## 📦 How to Install

### Requirements

- **Paper** 1.21.11 or newer
- **Java** 21+
- (Optional) PlaceholderAPI for advanced placeholders

### Quick Start

1. **Download** the latest `craftguard-1.1.1.jar` from [Releases](../../releases)
2. **Put** it in your server's `plugins/` folder
3. **Restart** your server
4. **Edit** the config: `plugins/CraftGuard/config.yml`
5. **Ready!** Use `/cg help` for commands

### What’s created automatically

- `config.yml` — main settings & messages
- `worlds.yml` — current crafting states for each world

By default, crafting is allowed everywhere.
Use `/cg <world> off` to lock down crafting in a world.

---

## 🎮 Commands

**Base command:** `/craftguard` (or `/cguard`, `/cg`)

| Command                  | What it does                 | Who can do it      |
| :----------------------- | :--------------------------- | :----------------- |
| `/cg` or `/cg help`      | Shows help menu              | Everyone           |
| `/cg <world> on`         | Allow crafting in a world    | `craftguard.admin` |
| `/cg <world> off`        | Block crafting in a world    | `craftguard.admin` |
| `/cg <world> toggle`     | Flip crafting on/off         | `craftguard.admin` |

### Some command examples

```bash
# Show help:
/cg
/cg help

# Enable crafting for "survival"
/cg survival on
/cg survival enable
/cg survival true

# Disable crafting for "minigames"
/cg minigames off
/cg minigames disable
/cg minigames false

# Toggle crafting for "lobby"
/cg lobby toggle
```

### Permissions

| Permission           | What it does                  | Default |
| :------------------- | :---------------------------- | :------ |
| `craftguard.admin`   | Manage settings               | OP      |
| `craftguard.bypass`  | Ignore crafting restrictions  | OP      |

> You can rename permission nodes in your config if you want.

---

## ⚙️ Configuration

The default config (`config.yml`) is commented throughout, but here's the basics:

```yaml
prefix:
  enabled: true
  text: "&a[CraftGuard] &7"

messages:
  crafting-enabled: "Crafting has been &aenabled &7in world &e{world}&7."
  crafting-disabled: "Crafting has been &cdisabled &7in world &e{world}&7."
  # More message options...

settings:
  notify-on-craft-attempt: true
  default-crafting-state: true
  event-priority: HIGHEST

permissions:
  admin: "craftguard.admin"
  bypass: "craftguard.bypass"

advanced:
  debug-mode: false
  cache-world-states: true
  auto-save-interval: 0
```

**Need help configuring?**  
Check:

- [Full Configuration Guide](docs/CONFIGURATION.md)
- [Message Prefix Help](docs/PREFIX.md)
- [PlaceholderAPI Guide](docs/PLACEHOLDERS.md)

---

## 🔌 PlaceholderAPI Support

If PlaceholderAPI is installed, you can use these placeholders:

| Placeholder                     | What it shows                | Example    |
| :------------------------------ | :--------------------------- | :--------- |
| `%craftguard_world%`            | Player’s current world       | `world`    |
| `%craftguard_world_state%`      | Crafting enabled/disabled    | `enabled`  |
| `%craftguard_world_<worldname>%`| State for specific world     | `disabled` |

**Example:**

```yaml
# To show on a scoreboard:
scoreboard-line: "Crafting: %craftguard_world_state%"
```

---

## 👨‍💻 Developers

### Building CraftGuard from Source

You'll need:

- Java 21 JDK
- Maven 3.6+
- Git

```bash
git clone https://github.com/yourusername/CraftGuard.git
cd CraftGuard
mvn clean package
# Find the JAR in target/
```

#### Plugin Folder Structure

```plaintext
CraftGuard/
├── src/main/java/me/devflare/CraftGuard/
│   ├── CraftGuard.java              # Main plugin
│   ├── commands/                    # Command logic
│   ├── config/                      # Config access
│   ├── listeners/                   # Event listeners
│   ├── placeholders/                # PlaceholderAPI expansion
│   └── utils/                       # Utility code
├── src/main/resources/
│   ├── config.yml
│   ├── worlds.yml
│   └── paper-plugin.yml
└── pom.xml
```

#### Useful Classes

- **CraftGuard.java** – Plugin startup/shutdown, registers everything.
- **ConfigManager.java** – Loads/caches config and world state. Example methods:
  - `isCraftingEnabled(String world)`
  - `setCraftingEnabled(String world, boolean enabled)`
  - `getMessageWithPrefix(String path)`
  - `getAdminPermission()`
  - `isDebugMode()`
- **CraftGuardCommand.java** – Handles commands/tab completion.
- **CraftingListener.java** – Cancels crafting as needed.
- **MessageUtil.java** – Color/placeholder/minimessage formatting.

#### Using as a Dependency

CraftGuard doesn’t have a formal API, but you can depend on it or use its placeholders.

```xml
<dependency>
  <groupId>me.devflare</groupId>
  <artifactId>craftguard</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```

Basic plugin access example:

```java
CraftGuard plugin = (CraftGuard) Bukkit.getPluginManager().getPlugin("CraftGuard");
ConfigManager config = plugin.getConfigManager();
boolean enabled = config.isCraftingEnabled("world");
config.setCraftingEnabled("world", true);
```

### Want to Contribute?

If you find a bug or have a cool idea, open a pull request or a GitHub issue.

1. Fork & clone this repo
2. Create a branch (`git checkout -b your-feature`)
3. Make changes & commit
4. Push branch and submit PR

**Coding Tips:**

- 4 spaces for indentation
- Use standard Java naming
- Add JavaDoc for public methods
- Keep your methods short and direct

**Testing:**  
- Make sure it builds (`mvn clean package`)
- Test it yourself on Paper 1.21.11
- Check all commands/configs behave correctly

---

## 📝 License

This plugin uses the MIT License—see [LICENSE](LICENSE) for all legalese.

---

## 🤝 Support & Feedback

### How to Get Help

- **Bugs or Issues?** [GitHub Issues](../../issues)
- **Chat or Suggest Ideas:** [Discussions](../../discussions)
- **Discord:** Coming soon

#### Reporting a Bug

When reporting, please tell us:

- Your CraftGuard version
- Your Paper version
- Your Java version
- How to reproduce the bug
- What happened and what you expected
- Relevant config entries
- Any errors in your console

#### Feature Requests

Open an [enhancement issue](../../issues/new?labels=enhancement) and explain your idea and how you’d use it.

---

## 🎯 Roadmap

Here's what's on the horizon:

- [ ] bStats usage tracking
- [ ] Public plugin API
- [ ] In-game GUI for settings
- [ ] Per-player crafting permissions
- [ ] Restrict specific recipes
- [ ] Crafting cooldowns
- [ ] World group support (manage multiple worlds at once)
- [ ] MySQL / SQLite / PostgreSQL / h2 / MongoDB / MariaDB / Oracle backend for big networks

### Recent Versions

---

## [1.1.0] - 2026-02-07

### Added

- **Reload Command**: Added `/cg reload` to reload configuration without restarting the server.
  - Requires `craftguard.admin` permission.
  - Reloads `config.yml` and `worlds.yml`.
  - Updates messages and settings instantly.

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

## 🙏 Credits

**Special thanks to everyone who contributed, directly or indirectly, to CraftGuard:**

- **PaperMC Team** – For their outstanding server software that powers it all.
- **PlaceholderAPI Developers** – For enabling seamless plugin integration and placeholders.
- **Kyori Adventure Project** – For bringing beautiful, modern chat formatting to the Minecraft ecosystem.
- **Community Testers & Feedback Providers** – Every player, server admin, and community member who reported bugs, suggested improvements, or supported the project with their ideas and encouragement.
- **Open Source Contributors** – Those who have written code, improved docs, or helped with translations.
- **All inspiration from the Minecraft plugin community** – Your creativity drives CraftGuard forward.

*Thank you for helping make CraftGuard better for everyone!*

---

<p align="center">
  <strong>Built by Minecraft fans, for Minecraft fans</strong><br>
  <a href="#craftguard">⬆ Back to Top</a>
</p>
