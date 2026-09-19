package org.carpetplus.underlay.api.event;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Event fired when an underlay is loaded into active memory / chunk.
 */
public class UnderlayLoadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Underlay underlay;

    public UnderlayLoadEvent(@NotNull Underlay underlay) {
        this.underlay = Objects.requireNonNull(underlay, "underlay cannot be null");
    }

    @NotNull
    public Underlay getUnderlay() {
        return underlay;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
