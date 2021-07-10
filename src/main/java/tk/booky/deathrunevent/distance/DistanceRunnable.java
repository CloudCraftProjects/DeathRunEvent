package tk.booky.deathrunevent.distance;
// Created by booky10 in DeathRunEvent (20:53 02.07.21)

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tk.booky.deathrunevent.DeathRunEventMain;
import tk.booky.deathrunevent.DeathRunEventManager;

public class DistanceRunnable {

    public static void start(Plugin plugin, long updateInterval, long saveInterval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, DistanceRunnable::executeUpdate, 0, updateInterval);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, DistanceManager::save, 0, saveInterval);
    }

    private static void executeUpdate() {
        Bukkit.getOnlinePlayers().forEach(DistanceManager::update);
        DistanceManager.sortDistances();

        if (DeathRunEventManager.isRunning() && DeathRunEventManager.getRemainingTime() <= 0) {
            Bukkit.getScheduler().runTask(DeathRunEventMain.main, DeathRunEventManager::stopEvent);
            DistanceManager.save();
        }
    }
}
