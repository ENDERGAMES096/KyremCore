# kyrem-core

Shared infrastructure library for Kyrem's Minecraft plugins. Provides reusable utilities, database access, menu system, task management, language support, and command abstractions — with zero plugin-specific logic.

---

## Adding the dependency

### 1. Add JitPack to your repositories

**Groovy (`build.gradle`)**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

**Kotlin DSL (`build.gradle.kts`)**
```kotlin
repositories {
    maven("https://jitpack.io")
}
```

### 2. Add the dependency

**Groovy**
```groovy
dependencies {
    implementation 'com.github.KyremWorks:kyrem-core:1.0.0'
}
```

**Kotlin DSL**
```kotlin
dependencies {
    implementation("com.github.KyremWorks:kyrem-core:1.0.0")
}
```

---

## Basic usage

```java
@Override
public void onEnable() {
    // Must be called first — registers the MenuListener
    KyremCore.init(this);

    // Database (HikariCP-backed MySQL)
    this.db = new Database("localhost", "3306", "my_db", "root", "password");

    // Language files (path is relative to plugin data folder)
    this.lang = new LanguageManager(this, "lang/messages.yml");

    // Task manager (tracks and cancels this plugin's tasks only)
    this.tasks = new TaskManager(this);
}

@Override
public void onDisable() {
    db.disconnect();
    tasks.cancelAll();
}
```

---

## Packages

| Package | Description |
|---|---|
| `com.kyrem.core` | `KyremCore.init(JavaPlugin)` — entry point |
| `com.kyrem.core.util` | `ChatUtils`, `ConfigUtils`, `CustomConfig`, `CustomMath`, `DefaultFontInfo`, `InventoryUtils`, `ItemBuilder`, `ItemUtils`, `Util` |
| `com.kyrem.core.database` | `Database` (HikariCP), `ResultSetConsumer` |
| `com.kyrem.core.menu` | `Menu`, `MenuItem`, `PaginatedMenu`, `PlayerMenuUtility`, `MenuListener` |
| `com.kyrem.core.task` | `TaskManager`, `TypedDistributedTask` |
| `com.kyrem.core.language` | `LanguageManager` |
| `com.kyrem.core.commands` | `SubCommand` (interface), `ParentCommand`, `BaseCommand`, `CommandManager` |

---

## Commands quick-start

```java
// 1. Define a sub-command
public class ReloadSubCommand implements SubCommand {
    @Override public String getName() { return "reload"; }
    @Override public String getDescription() { return "Reload configuration"; }
    @Override public String getSyntax() { return "/myplugin reload"; }
    @Override public String getPermission() { return "myplugin.reload"; }
    @Override public int getArgumentsQuantity() { return 0; }

    @Override
    public void perform(Player player, String[] args) {
        // ... reload logic
        player.sendMessage("Reloaded!");
    }

    @Override
    public List<String> getTabCompletation(Player player, String[] args) {
        return List.of();
    }
}

// 2. Define the top-level command
public class MyPluginCommand extends BaseCommand {
    public MyPluginCommand() {
        addSubCommand(new ReloadSubCommand());
        addSubCommand(new HelpSubCommand());
    }
}

// 3. Register in onEnable() — command must be in plugin.yml
CommandManager.register(this, "myplugin", new MyPluginCommand());
```

---

## Releasing a new version

1. Push your changes to GitHub.
2. Create a Git tag: `git tag 1.0.1 && git push origin 1.0.1`
3. JitPack builds automatically on the first import of the new tag.
