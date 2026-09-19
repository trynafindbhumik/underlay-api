# Underlay API Framework (`underlay-api`)

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java Target](https://img.shields.io/badge/java-21%2B-blue.svg)]()
[![Paper Target](https://img.shields.io/badge/paper-1.19.4%20to%2026.%2A-orange.svg)]()
[![License](https://img.shields.io/badge/license-MIT-green.svg)]()

`underlay-api` is a high-performance, generic Java library and framework for Paper Minecraft servers (1.19.4 to 26.*) designed to architect **server-side block coexistence systems**. It enables logical underlay blocks (such as carpets, moss layers, snow, and furniture bases) to exist beneath solid blocks (such as chests, fences, and banners) without client mod dependencies.

Based on the research paper:
> *"Beyond the Block: Architecting a Server-Side Coexistence System for Minecraft Carpets"*

---

## 🌟 Key Features

* **Universal Vanilla Compatibility**: Relies on Paper's `BlockDisplay` (`org.bukkit.entity.BlockDisplay`) and `Interaction` (`org.bukkit.entity.Interaction`) entities. No client-side mods required for vanilla players.
* **Persistent NBT Linkage**: Links transient entities to persistent records using Bukkit's `PersistentDataContainer` (PDC).
* **High-Performance SQLite Storage Engine**: Features embedded SQLite persistence with Write-Ahead Logging (WAL) and automated versioned schema migrations (`underlay_schema_history`).
* **Chunk-Based Spatial Caching**: Aggressively lazy-loads underlay data on `ChunkLoadEvent` and despawns entities on `ChunkUnloadEvent` to scale up to tens of thousands of active underlays.
* **Extensible Rule Framework**: Decouples block rules into predicate-driven `UnderlayDefinition` objects registered via `UnderlayRegistry`.
* **Folia & Multi-Thread Compatible**: Designed with `UnderlayTaskScheduler` for asynchronous IO and region-safe Bukkit execution.

---

## 📦 Supported Server Platforms

* **Paper** (1.19.4 – 26.*)
* **Purpur**
* **Folia** (Multi-threaded regional server)
* **Pufferfish / Gale / Canvas**
* **Spigot** (1.19.4+)

---

## 🚀 Getting Started

### Gradle Dependency

```kotlin
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("org.carpetplus:underlay-api:1.0.0-SNAPSHOT")
}
```

---

## 💡 Code Examples

### 1. Registering Block Definitions

```java
import org.carpetplus.underlay.api.UnderlayDefinition;
import org.carpetplus.underlay.api.UnderlayRegistry;
import org.bukkit.Material;

Set<Material> baseCarpets = Set.of(
    Material.WHITE_CARPET, Material.RED_CARPET, Material.MOSS_CARPET
);

UnderlayDefinition carpetDefinition = new UnderlayDefinition(
    "carpet_underlay",
    baseCarpets,
    overlayMaterial -> overlayMaterial == Material.CHEST || overlayMaterial.name().endsWith("_FENCE")
);

UnderlayRegistry.getInstance().registerDefinition(carpetDefinition);
```

### 2. Creating an Underlay Programmatically

```java
import org.carpetplus.underlay.api.UnderlayService;
import org.bukkit.Location;
import org.bukkit.Material;

UnderlayService service = ...; // Inject service instance

Location location = player.getLocation().getBlock().getLocation();

service.createUnderlay(
    location,
    Material.WHITE_CARPET, "minecraft:white_carpet",
    Material.CHEST, "minecraft:chest[facing=north]"
).thenAccept(underlay -> {
    player.sendMessage("Virtual underlay created successfully with ID: " + underlay.getId());
});
```

### 3. Listening to Custom Events

```java
import org.carpetplus.underlay.api.event.UnderlayInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UnderlayListener implements Listener {

    @EventHandler
    public void onUnderlayInteract(UnderlayInteractEvent event) {
        if (event.isSneaking()) {
            event.getPlayer().sendMessage("Sneak-clicked virtual underlay at " + event.getUnderlay().getX() + ", " + event.getUnderlay().getZ());
        }
    }
}
```

---

## 🛠️ Building & Testing

### Build standard JAR

```bash
./gradlew build
```

### Run JUnit 5 Test Suite

```bash
./gradlew test
```

---

## 📜 License

MIT License. Developed for the CarpetPlus Ecosystem.
