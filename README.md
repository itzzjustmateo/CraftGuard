<p align="center">
  <h1 align="center">CraftGuard</h1>
  <img src="https://img.shields.io/badge/CraftGuard-v1.2.2-brightgreen?style=for-the-badge" alt="CraftGuard Logo" />
  <a href="https://papermc.io/"><img src="https://img.shields.io/badge/Paper-1.21.11-blue?style=for-the-badge" alt="Paper" /></a>
  <a href="https://www.oracle.com/java/"><img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge" alt="Java" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License" /></a>
</p>

> **⚠️ About FlameAntiCraft/FlameCraft**  
> CraftGuard picks up where my old anti-crafting plugin, [FlameAntiCraft (FlameCraft)](https://modrinth.com/plugin/flameanticraft), left off.  
> FlameAntiCraft/FlameCraft won’t get any more updates—if you want new features, improvements, or fixes, CraftGuard is the place to be!

**Tired of players crafting where you don't want them to? CraftGuard lets you control crafting, workstations, and portals, world by world.**

[Features](#features) • [Installation](#installation) • [Commands](#commands) • [Configuration](#configuration) • [For Developers](#for-developers)

---

## 📋 What is CraftGuard?

CraftGuard is a powerful plugin that lets you turn crafting, workstation usage (furnaces, anvils, etc.), and portal access (nether/end) on or off for specific worlds.
Whether you're running minigames, creative hubs, or want extra survival control, CraftGuard provides granular management with ease.

### Why use CraftGuard?

- ⚡ **Lightweight** – Won’t slow down your server
- 🎛️ **Granular Control** – Toggle specific workstations and portals individually
- 🌎 **Per-World Settings** – Decide exactly what features are allowed in each world
- 🧩 **PlaceholderAPI Support** – Advanced placeholders for every feature type
- 🎨 **Modern Chat Formatting** – Color codes and MiniMessage support
- 👩‍🔧 **Automatic Migration** – Effortlessly upgrades your old settings to the new system

---

## ✨ Features

### Core
- **Granular Toggles**: Control crafting, nether portals, end portals, anvils, furnaces, and 10+ other workstation types.
- **Per-World States**: Different settings for every world on your server.
- **Bypass Permissions**: Allow specific players to ignore restrictions by feature type.
- **Redesigned Help**: Clean, easy-to-read help menu with precise information.
- **Modern Syntax**: Simplified command structure for managing your server.

### Supported Features
- `crafting`
- `nether-portal`
- `end-portal`
- `anvil`
- `furnace`, `blast-furnace`, `smoker`
- `enchanting`, `brewing`, `smithing`
- `loom`, `cartography`, `grindstone`, `stonecutter`

---

## 📦 How to Install

### Requirements
- **Paper** 1.21.11 or newer
- **Java** 21+
- (Optional) PlaceholderAPI for advanced placeholders

### Quick Start
1. **Download** the latest `craftguard-1.2.0.jar` from [Releases](../../releases)
2. **Put** it in your server's `plugins/` folder
3. **Restart** your server
4. **Edit** the config: `plugins/CraftGuard/config.yml`
5. **Ready!** Use `/cg help` for commands

---

## 🎮 Commands

**Base command:** `/craftguard` (or `/cguard`, `/cg`)

| Command                  | What it does                 | Who can do it      |
| :----------------------- | :--------------------------- | :----------------- |
| `/cg` or `/cg help`      | Shows help menu              | Everyone           |
| `/cg <world> <type> on`    | Allow a feature in a world   | `craftguard.admin` |
| `/cg <world> <type> off`   | Block a feature in a world   | `craftguard.admin` |
| `/cg <world> <type> toggle`| Flip feature on/off          | `craftguard.admin` |
| `/cg reload`               | Reload configuration         | `craftguard.admin` |

### Command Examples
```bash
# Block anvils in "survival"
/cg survival anvil off

# Enable nether portals in "world"
/cg world nether-portal on

# Toggle crafting in "lobby"
/cg lobby crafting toggle
```

### Permissions

| Permission                 | Description                   | Default |
| :------------------------- | :---------------------------- | :------ |
| `craftguard.admin`         | Manage all settings           | OP      |
| `craftguard.bypass.<type>` | Bypass specific restriction   | false   |
| `craftguard.bypass.*`      | Bypass all restrictions       | false   |

---

## 🔌 PlaceholderAPI Support

Placeholders follow a simple pattern:
`%craftguard_world_state_<type>%` (for player's current world)
`%craftguard_world_<worldname>_<type>%` (for specific world)

**Examples:**
- `%craftguard_world_state_crafting%` -> `enabled`
- `%craftguard_world_survival_anvil%` -> `disabled`
- `%craftguard_world_state_nether-portal%` -> `enabled`

---

## 👨‍💻 Developers

### Building from Source
```bash
git clone https://github.com/itzzjustmateo/CraftGuard.git
cd CraftGuard
mvn clean package
```

### ConfigManager API
You can access the `ConfigManager` via `CraftGuard.getInstance().getConfigManager()`.
- `isFeatureEnabled(String world, String type)`
- `setFeatureEnabled(String world, String type, boolean enabled)`

---

## 📝 License
MIT License — see [LICENSE](LICENSE).

---

## 🤝 Support
- **Issues?** [GitHub Issues](../../issues)
- **Discussions:** [Feature Requests](../../discussions)
- **Discord:** [Join our Discord](https://dc.devflare.de)