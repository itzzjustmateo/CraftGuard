# GitHub Issue Drafts

## 1) Typo Fix: Correct invalid MiniMessage tags in default config messages

**Title:** Fix invalid MiniMessage color tags in `messages.feature-enabled` and `messages.feature-disabled`

**Type:** Maintenance / UX

**Problem**
The default config uses `<a>...</a>` and `<c>...</c>` in success messages. These are not valid MiniMessage tags and can produce incorrect formatting behavior.

**Scope**
- Update default message strings in `src/main/resources/config.yml`.
- Replace invalid tags with valid MiniMessage color tags (e.g., `<green>`, `<red>`) or consistent legacy formatting.
- Keep wording and placeholders unchanged.

**Acceptance Criteria**
- [ ] `messages.feature-enabled` uses valid formatting tags only.
- [ ] `messages.feature-disabled` uses valid formatting tags only.
- [ ] No invalid shorthand tags like `<a>` / `<c>` remain in default config messages.
- [ ] Existing placeholders `{type}`, `{world}`, `{state}`, `{player}` remain intact.
- [ ] Plugin builds successfully after change.

**Effort Estimate**
- **Size:** XS
- **Estimate:** 0.5–1 hour

---

## 2) Bug Fix: Prevent duplicate workstation block handling

**Title:** Avoid duplicate notifications/audit entries when workstation interactions are blocked

**Type:** Bug

**Problem**
A workstation interaction may be processed in both `PlayerInteractEvent` and `InventoryOpenEvent`, which can lead to duplicate user notifications and/or duplicate audit log entries for one action.

**Scope**
- Refine workstation event handling to ensure each blocked interaction is handled once.
- Keep permission, WorldGuard bypass, and feature-state checks functionally equivalent.
- Preserve existing user-facing behavior (single block message when blocked).

**Acceptance Criteria**
- [ ] A single blocked workstation interaction results in at most one player notification.
- [ ] A single blocked workstation interaction results in at most one audit log entry.
- [ ] No regressions for allowed interactions (UI opens as expected).
- [ ] Bypass permissions and WorldGuard bypass still skip blocking.
- [ ] Tests (or reproducible checks) cover at least one duplicate-prone workstation case.

**Effort Estimate**
- **Size:** M
- **Estimate:** 3–6 hours

---

## 3) Documentation Discrepancy: Align command usage metadata with actual syntax

**Title:** Update `plugin.yml` command usage to match implemented syntax

**Type:** Documentation / Metadata

**Problem**
`plugin.yml` currently advertises `/<command> [world] [on|off|toggle]`, but implementation expects `/<command> <world> <type> <on|off|toggle>` for management actions.

**Scope**
- Update command `usage` in `src/main/resources/plugin.yml` to reflect current parser behavior.
- Verify help examples and command docs remain consistent (README/config help text).

**Acceptance Criteria**
- [ ] `plugin.yml` usage string includes `<type>` argument.
- [ ] Usage string does not conflict with `/cg help` or `/cg reload` subcommands.
- [ ] Any related docs that mention old syntax are updated for consistency.
- [ ] Plugin builds successfully after documentation/metadata updates.

**Effort Estimate**
- **Size:** S
- **Estimate:** 1–2 hours

---

## 4) Test Improvement: Add coverage for command and listener regression paths

**Title:** Introduce baseline automated tests for command parsing and workstation blocking flow

**Type:** Test Improvement

**Problem**
The repository currently lacks a test suite, increasing regression risk for command parsing and event-driven restrictions.

**Scope**
- Add initial test setup (e.g., Maven test dependencies + test source folder).
- Add unit/integration-style tests for:
  - command parsing / action routing (`on`, `off`, `toggle`, invalid usage),
  - workstation block flow around duplicate-prone interactions.
- Ensure tests are runnable in CI/local with clear command.

**Acceptance Criteria**
- [ ] Test infrastructure is added and documented (how to run tests).
- [ ] At least one test verifies command usage validation behavior.
- [ ] At least one test verifies blocked workstation interactions do not double-notify/double-log.
- [ ] `mvn test` executes successfully in repository environment (or documented constraints).
- [ ] No production behavior changes outside testability improvements.

**Effort Estimate**
- **Size:** L
- **Estimate:** 1–2 days
