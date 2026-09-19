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
 * Event fired when an underlay is removed/destroyed.
 */
public class UnderlayRemoveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public enum RemoveReason {
        BREAK_TOP_BLOCK,
        BREAK_UNDERLAY,
        EXPLOSION,
        PLUGIN,
        COMMAND
    }

    private final Underlay underlay;
    private final RemoveReason reason;
    private final Player remover;
    private boolean cancelled = false;

    public UnderlayRemoveEvent(@NotNull Underlay underlay, @NotNull RemoveReason reason, @Nullable Player remover) {
        this.underlay = Objects.requireNonNull(underlay, "underlay cannot be null");
        this.reason = Objects.requireNonNull(reason, "reason cannot be null");
        this.remover = remover;
    }

    @NotNull
    public Underlay getUnderlay() {
        return underlay;
    }

    @NotNull
    public RemoveReason getReason() {
        return reason;
    }

    @Nullable
    public Player getRemover() {
        return remover;
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
