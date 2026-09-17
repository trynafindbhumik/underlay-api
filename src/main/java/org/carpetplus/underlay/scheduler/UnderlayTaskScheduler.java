package org.carpetplus.underlay.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Task scheduler wrapper for managing asynchronous IO and main/regional thread sync tasks.
 */
public class UnderlayTaskScheduler {

    private final Plugin plugin;
    private final Executor asyncExecutor;

    public UnderlayTaskScheduler(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.asyncExecutor = Executors.newFixedThreadPool(4, r -> {
            Thread thread = new Thread(r, "Underlay-Worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void runAsync(@NotNull Runnable runnable) {
        asyncExecutor.execute(runnable);
    }

    public void runSync(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }
}
