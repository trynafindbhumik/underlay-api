package org.carpetplus.underlay.api.event;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Event fired when an underlay is unloaded from active memory / chunk.
 */
public class UnderlayUnloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Underlay underlay;

    public UnderlayUnloadEvent(@NotNull Underlay underlay) {
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
