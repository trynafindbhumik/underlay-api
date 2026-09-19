package org.carpetplus.underlay.api;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for registering and validating supported underlay definitions.
 */
public final class UnderlayRegistry {

    private static final UnderlayRegistry INSTANCE = new UnderlayRegistry();

    private final Map<String, UnderlayDefinition> definitions = new ConcurrentHashMap<>();

    private UnderlayRegistry() {}

    @NotNull
    public static UnderlayRegistry getInstance() {
        return INSTANCE;
    }

    public void registerDefinition(@NotNull UnderlayDefinition definition) {
        Objects.requireNonNull(definition, "definition cannot be null");
        definitions.put(definition.getId(), definition);
    }

    public boolean unregisterDefinition(@NotNull String id) {
        return definitions.remove(id) != null;
    }

    @NotNull
    public Optional<UnderlayDefinition> getDefinition(@NotNull String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    @NotNull
    public Collection<UnderlayDefinition> getAllDefinitions() {
        return Collections.unmodifiableCollection(definitions.values());
    }

    public boolean isValidCombination(@NotNull Material base, @NotNull Material overlay) {
        for (UnderlayDefinition def : definitions.values()) {
            if (def.isValidCombination(base, overlay)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        definitions.clear();
    }
}
