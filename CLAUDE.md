# KyremCore — Shared Library for Minecraft Plugins

## Your First Job
Before writing any code, read the original source files from:
```
~/IdeaProjects/CrimeCore/src/main/java/
```
Specifically, read and fully understand:
- The entire `util/` package → **EXCEPT PlantUtil.java**, copy all others
- `database/Database.java`
- The menu system package (whatever it's called)
- The task manager class
- The language manager class
- The `commands/` package → copy only abstract/base classes, NOT any command that references CrimeCore-specific logic

Do NOT start coding until you've read all those files.

---

## Goal
Build a standalone, reusable Gradle library (`kyrem-core`) that any of Kyrem's Minecraft plugins can import via JitPack.
This library contains **zero plugin-specific logic** — only generic, reusable infrastructure extracted from CrimeCore.

---

## Project Setup

### Coordinates
```
group:    com.github.KyremWorks
artifact: kyrem-core
version:  1.0.0
```

### Gradle (build.gradle)
- Java 17
- `maven-publish` plugin
- Dependencies:
  - `compileOnly`: Paper API (or Spigot API) — same version as CrimeCore, do NOT shade it
  - `implementation`: MySQL Connector/J — same version as CrimeCore
  - `implementation`: HikariCP (connection pooling for MySQL)

### Publishing block
JitPack builds directly from the GitHub repo — no manual publish step needed.
Just make sure the build.gradle has a valid `group` and the repo is public on GitHub.

```groovy
plugins {
    id 'java-library'
    id 'maven-publish'
}

group = 'com.github.KyremWorks'
version = '1.0.0'

publishing {
    publications {
        maven(MavenPublication) {
            from components.java
        }
    }
}
```

To release a new version: push to GitHub and create a Git tag (e.g. `1.0.0`).
JitPack will build it automatically on first import.

---

## Target Package Structure

Replicate the same sub-package names found in CrimeCore, but under the new root `com.kyrem.core`:

```
com.kyrem.core
├── KyremCore.java                  ← Static entry point: KyremCore.init(JavaPlugin plugin)
│                                     Registers MenuListener, nothing else
│
├── util/                           ← All classes from CrimeCore's util/ EXCEPT PlantUtil.java
│                                     Keep class names and method signatures identical
│
├── database/
│   └── Database.java               ← Taken from CrimeCore's Database.java
│                                     Replace any hardcoded table creation with a generic interface
│                                     Use HikariCP if CrimeCore uses raw DriverManager — upgrade it
│                                     Expose: connect(), disconnect(), getConnection(), executeUpdate(), executeQuery()
│
├── menu/                           ← Taken from CrimeCore's menu system
│   ├── Menu.java                   ← Abstract base class, remove CrimeCore-specific items
│   ├── MenuItem.java               ← Keep as-is
│   └── MenuListener.java           ← Keep as-is, but register via KyremCore.init(), not per-plugin
│
├── task/
│   └── TaskManager.java            ← Taken from CrimeCore's task manager
│                                     Must accept JavaPlugin in constructor
│                                     Expose: runSync(), runAsync(), runDelayed(), runRepeating(), cancelAll()
│
├── language/
│   └── LanguageManager.java        ← Taken from CrimeCore's language manager
│                                     .yml path must be passed in constructor — no hardcoded paths
│                                     Support: reload(), get(String key), get(String key, Map<String,String> placeholders)
│
└── commands/
    ├── BaseCommand.java            ← Abstract class: CommandExecutor + TabCompleter
    │                                 Handles permission checks, player-only guard, subcommand routing
    ├── SubCommand.java             ← Interface only
    └── CommandManager.java         ← Registers commands via JavaPlugin param — no hardcoded plugin ref
```

---

## Rules You MUST Follow

1. **Read first, code second** — base every class on what you actually find in CrimeCore, don't invent implementations
2. **Strip CrimeCore-specific logic** — anything referencing plugin name, specific tables, specific commands → remove or parameterize
3. **No plugin-specific references** anywhere — no hardcoded names, table names, or command names
4. **Database.java** — if CrimeCore uses raw `DriverManager`, upgrade to HikariCP; pool config settable via constructor
5. **MenuListener** — registered once by `KyremCore.init()`, uses a `HashMap<Inventory, Menu>` to route clicks
6. **TaskManager** — one instance per plugin, `cancelAll()` only cancels that plugin's tasks
7. **LanguageManager** — `.yml` path passed in constructor
8. **Commands package** — pure abstractions only, zero CrimeCore command logic
9. **KyremCore.java** — does NOT extend JavaPlugin; static `init(JavaPlugin)` only
10. **No fat jar / no shading** — consuming plugins import via Gradle dependency

---

## How Consuming Plugins Will Use This

### build.gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KyremWorks:kyrem-core:1.0.0'
}
```

### onEnable()
```java
@Override
public void onEnable() {
    KyremCore.init(this);
    this.db = new Database(host, port, dbName, user, password);
    this.lang = new LanguageManager(this, "lang/messages.yml");
    this.tasks = new TaskManager(this);
}
```

---

## Deliverables
1. `build.gradle` as described above
2. `settings.gradle` with `rootProject.name = 'kyrem-core'`
3. All classes listed above, implemented based on CrimeCore originals
4. `README.md` — how to add the dependency in a consuming plugin via JitPack
