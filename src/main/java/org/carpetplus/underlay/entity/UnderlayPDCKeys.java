package org.carpetplus.underlay.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Registry of NamespacedKeys used in PersistentDataContainer for linking entities to underlays.
 */
public final class UnderlayPDCKeys {

    private final NamespacedKey underlayIdKey;
    private final NamespacedKey entityRoleKey;

    public UnderlayPDCKeys(@NotNull Plugin plugin) {
        this.underlayIdKey = new NamespacedKey(plugin, "underlay_id");
        this.entityRoleKey = new NamespacedKey(plugin, "entity_role");
    }

    @NotNull
    public NamespacedKey getUnderlayIdKey() {
        return underlayIdKey;
    }

    @NotNull
    public NamespacedKey getEntityRoleKey() {
        return entityRoleKey;
    }

    public static final String ROLE_BLOCK_DISPLAY = "BLOCK_DISPLAY";
    public static final String ROLE_INTERACTION = "INTERACTION";
}
