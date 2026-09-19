package org.carpetplus.underlay.api.event;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Event fired when a new virtual underlay is being created.
 */
public class UnderlayCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Underlay underlay;
    private final Player creator;
    private boolean cancelled = false;

    public UnderlayCreateEvent(@NotNull Underlay underlay, @Nullable Player creator) {
        this.underlay = Objects.requireNonNull(underlay, "underlay cannot be null");
        this.creator = creator;
    }

    @NotNull
    public Underlay getUnderlay() {
        return underlay;
    }

    @Nullable
    public Player getCreator() {
        return creator;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
