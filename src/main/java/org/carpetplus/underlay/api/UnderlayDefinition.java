package org.carpetplus.underlay.api;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Defines valid combinations of base materials (e.g., carpets, moss) and overlay materials (e.g., chests, fences).
 */
public final class UnderlayDefinition {

    private final String id;
    private final Set<Material> allowedBaseMaterials;
    private final Predicate<Material> overlayFilter;

    public UnderlayDefinition(
            @NotNull String id,
            @NotNull Set<Material> allowedBaseMaterials,
            @NotNull Predicate<Material> overlayFilter
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.allowedBaseMaterials = Collections.unmodifiableSet(
                Objects.requireNonNull(allowedBaseMaterials, "allowedBaseMaterials cannot be null")
        );
        this.overlayFilter = Objects.requireNonNull(overlayFilter, "overlayFilter cannot be null");
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Set<Material> getAllowedBaseMaterials() {
        return allowedBaseMaterials;
    }

    public boolean isBaseSupported(@NotNull Material material) {
        return allowedBaseMaterials.contains(material);
    }

    public boolean isOverlaySupported(@NotNull Material material) {
        return overlayFilter.test(material);
    }

    public boolean isValidCombination(@NotNull Material base, @NotNull Material overlay) {
        return isBaseSupported(base) && isOverlaySupported(overlay);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnderlayDefinition that = (UnderlayDefinition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
