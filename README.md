# CraftGuard

<div align="center">

![CraftGuard Logo](https://img.shields.io/badge/CraftGuard-v1.0.0-brightgreen?style=for-the-badge)
[![Paper](https://img.shields.io/badge/Paper-1.21.11-blue?style=for-the-badge)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

**A lightweight, highly configurable Minecraft plugin for managing crafting permissions on a per-world basis.**

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [Configuration](#-configuration) • [For Developers](#-for-developers)

</div>

---

## 📋 Overview

CraftGuard is a powerful yet simple plugin that gives server administrators complete control over crafting permissions in different worlds. Whether you want to disable crafting in minigame worlds, lobby areas, or specific survival worlds, CraftGuard makes it easy with an intuitive command system and 100% configurable options.

### Why CraftGuard?

- ✅ **Lightweight** - Minimal performance impact with smart caching
- ✅ **100% Configurable** - Every message, permission, and setting can be customized
- ✅ **Per-World Control** - Enable or disable crafting in specific worlds
- ✅ **PlaceholderAPI Support** - Integrate with your existing placeholder system
- ✅ **Modern Formatting** - Supports both legacy color codes and MiniMessage
- ✅ **Developer Friendly** - Clean, well-documented code with Maven build system

---

## ✨ Features

### Core Features

- **Per-World Crafting Control** - Enable, disable, or toggle crafting in any world
- **Bypass Permissions** - Allow specific players to craft regardless of world settings
- **Configurable Messages** - Customize all messages with color codes and MiniMessage
- **Message Prefix System** - Global prefix for all messages with enable/disable toggle
- **Tab Completion** - Smart suggestions for commands and world names
- **Debug Mode** - Detailed logging for troubleshooting
- **PlaceholderAPI Integration** - Custom placeholders for other plugins

### Advanced Configuration

- **Custom Permissions** - Define your own permission nodes
- **Action Aliases** - Customize command syntax (on/off/enable/disable/toggle)
- **Event Priority** - Control when CraftGuard checks crafting events
- **Caching Options** - Performance tuning for large servers
- **Auto-Save Settings** - Configure when world states are saved

---

## 📦 Installation

### Requirements

- **Minecraft Server**: Paper 1.21.11 or higher
- **Java**: 21 or higher
- **Optional**: PlaceholderAPI (for placeholder support)

### Quick Start

1. **Download** the latest `craftguard-1.21.11-1.0.0-SNAPSHOT.jar` from [Releases](../../releases)
2. **Place** the JAR file in your server's `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin in `plugins/CraftGuard/config.yml`
5. **Enjoy!** Use `/cg help` to see available commands

### First-Time Setup

After installation, CraftGuard will create two configuration files:

- `config.yml` - Messages, settings, permissions, and advanced options
- `worlds.yml` - Per-world crafting states (auto-generated)

By default, crafting is **enabled** in all worlds. Use `/cg <world> off` to disable crafting in specific worlds.

---

## 🎮 Commands

### Main Command

All commands use the base command `/craftguard` (aliases: `/cguard`, `/cg`)

| Command | Description | Permission |
|---------|-------------|------------|
| `/cg` | Show help information | None |
| `/cg help` | Show help information | None |
| `/cg <world> on` | Enable crafting in a world | `craftguard.admin` |
| `/cg <world> off` | Disable crafting in a world | `craftguard.admin` |
| `/cg <world> toggle` | Toggle crafting in a world | `craftguard.admin` |

### Command Examples

```bash
# Show help
/cg
/cg help

# Enable crafting in the world "survival"
/cg survival on
/cg survival enable
/cg survival true

# Disable crafting in the world "minigames"
/cg minigames off
/cg minigames disable
/cg minigames false

# Toggle crafting in the world "lobby"
/cg lobby toggle
```

### Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `craftguard.admin` | Manage crafting settings | OP |
| `craftguard.bypass` | Bypass crafting restrictions | OP |

> **Note**: Permission nodes can be customized in `config.yml`

---

## ⚙️ Configuration

### Basic Configuration

The `config.yml` file is fully documented with comments. Here's a quick overview:

```yaml
# Message prefix (appears before all player messages)
prefix:
  enabled: true
  text: "&a[CraftGuard] &7"

# Customize all messages
messages:
  crafting-enabled: "Crafting has been &aenabled &7in world &e{world}&7."
  crafting-disabled: "Crafting has been &cdisabled &7in world &e{world}&7."
  # ... and many more

# Plugin settings
settings:
  notify-on-craft-attempt: true
  default-crafting-state: true
  event-priority: HIGHEST

# Custom permissions
permissions:
  admin: "craftguard.admin"
  bypass: "craftguard.bypass"

# Advanced options
advanced:
  debug-mode: false
  cache-world-states: true
  auto-save-interval: 0
```

### Configuration Guides

For detailed configuration examples and guides, see:

- [Configuration Guide](docs/CONFIGURATION.md) - Complete configuration reference
- [Message Prefix Guide](docs/PREFIX.md) - Customize message prefixes
- [PlaceholderAPI Guide](docs/PLACEHOLDERS.md) - Using placeholders

---

## 🔌 PlaceholderAPI Integration

CraftGuard provides custom placeholders when PlaceholderAPI is installed:

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%craftguard_world%` | Current world name | `world` |
| `%craftguard_world_state%` | Current world crafting state | `enabled` |
| `%craftguard_world_<worldname>%` | Specific world state | `disabled` |

### Example Usage

```yaml
# In another plugin's config
scoreboard-line: "Crafting: %craftguard_world_state%"
```

---

## 👨‍💻 For Developers

### Building from Source

#### Prerequisites

- Java 21 JDK
- Maven 3.6+
- Git

#### Clone and Build

```bash
# Clone the repository
git clone https://github.com/yourusername/CraftGuard.git
cd CraftGuard

# Build with Maven
mvn clean package

# The compiled JAR will be in target/
# craftguard-1.21.11-1.0.0-SNAPSHOT.jar
```

### Project Structure

```
CraftGuard/
├── src/main/java/me/devflare/CraftGuard/
│   ├── CraftGuard.java              # Main plugin class
│   ├── commands/
│   │   └── CraftGuardCommand.java   # Command handler
│   ├── config/
│   │   └── ConfigManager.java       # Configuration management
│   ├── listeners/
│   │   └── CraftingListener.java    # Event listener
│   ├── placeholders/
│   │   └── CraftGuardExpansion.java # PlaceholderAPI integration
│   └── utils/
│       └── MessageUtil.java         # Message formatting
├── src/main/resources/
│   ├── config.yml                   # Default configuration
│   ├── worlds.yml                   # World states template
│   └── paper-plugin.yml             # Plugin metadata
└── pom.xml                          # Maven configuration
```

### Key Classes

#### CraftGuard.java
Main plugin class handling initialization and lifecycle management.

#### ConfigManager.java
Manages configuration files, caching, and provides methods to access all settings.

**Key Methods:**
- `isCraftingEnabled(String world)` - Check if crafting is enabled
- `setCraftingEnabled(String world, boolean enabled)` - Set crafting state
- `getMessageWithPrefix(String path)` - Get formatted message with prefix
- `getAdminPermission()` - Get admin permission node
- `isDebugMode()` - Check if debug logging is enabled

#### CraftGuardCommand.java
Handles all command execution and tab completion.

#### CraftingListener.java
Listens to `CraftItemEvent` and enforces crafting restrictions.

#### MessageUtil.java
Formats messages with color codes, MiniMessage, and PlaceholderAPI support.

### API Usage

While CraftGuard doesn't currently expose a public API, you can interact with it through PlaceholderAPI or by adding it as a dependency:

```xml
<dependency>
    <groupId>me.devflare</groupId>
    <artifactId>craftguard</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Then access the plugin instance:

```java
CraftGuard plugin = (CraftGuard) Bukkit.getPluginManager().getPlugin("CraftGuard");
ConfigManager config = plugin.getConfigManager();

// Check if crafting is enabled in a world
boolean enabled = config.isCraftingEnabled("world");

// Enable crafting in a world
config.setCraftingEnabled("world", true);
```

### Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

#### Code Style

- Use 4 spaces for indentation
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Keep methods focused and concise

#### Testing

Before submitting a PR:
- Build the project (`mvn clean package`)
- Test on a Paper 1.21.11 server
- Verify all commands work as expected
- Check that configuration changes are backward compatible

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Support

### Getting Help

- **Issues**: [GitHub Issues](../../issues)
- **Discussions**: [GitHub Discussions](../../discussions)
- **Discord**: [Join our Discord](#) *(coming soon)*

### Reporting Bugs

When reporting bugs, please include:

1. CraftGuard version
2. Paper version
3. Java version
4. Steps to reproduce
5. Expected vs actual behavior
6. Relevant config sections
7. Console errors (if any)

### Feature Requests

Have an idea? Open a [feature request](../../issues/new?labels=enhancement) with:

- Clear description of the feature
- Use case / why it's needed
- Example configuration (if applicable)

---

## 🎯 Roadmap

### Planned Features

- [ ] bStats integration for usage statistics
- [ ] Public API for other plugins
- [ ] In-game GUI for configuration
- [ ] Per-player crafting permissions
- [ ] Recipe-specific restrictions
- [ ] Crafting cooldowns
- [ ] World groups (apply settings to multiple worlds)
- [ ] MySQL/SQLite support for world states

### Version History

#### v1.0.0 (Current)
- Initial release
- Per-world crafting control
- 100% configurable messages and settings
- PlaceholderAPI integration
- Message prefix system
- Debug mode
- Tab completion

---

## 🙏 Acknowledgments

- **Paper Team** - For the excellent Paper API
- **PlaceholderAPI** - For placeholder integration
- **Kyori Adventure** - For modern text formatting
- **Community** - For feedback and suggestions

---

<div align="center">

**Made with ❤️ for the Minecraft community**

[⬆ Back to Top](#craftguard)

</div>
