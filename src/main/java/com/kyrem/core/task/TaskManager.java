package com.kyrem.core.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages Bukkit scheduler tasks for a single plugin.
 * Each plugin should create its own instance.
 * {@link #cancelAll()} only cancels tasks registered through this instance.
 */
public class TaskManager {

    private final JavaPlugin plugin;
    private final List<BukkitTask> tasks = new ArrayList<>();

    public TaskManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask runSync(Runnable task) {
        BukkitTask bt = Bukkit.getScheduler().runTask(plugin, task);
        tasks.add(bt);
        return bt;
    }

    public BukkitTask runAsync(Runnable task) {
        BukkitTask bt = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        tasks.add(bt);
        return bt;
    }

    public BukkitTask runDelayed(Runnable task, long delayTicks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        tasks.add(bt);
        return bt;
    }

    public BukkitTask runRepeating(Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        tasks.add(bt);
        return bt;
    }

    public BukkitTask runDelayedAsync(Runnable task, long delayTicks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        tasks.add(bt);
        return bt;
    }

    public BukkitTask runRepeatingAsync(Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        tasks.add(bt);
        return bt;
    }

    public void cancelAll() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }
}
