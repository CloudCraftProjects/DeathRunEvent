package tk.booky.deathrunevent.distance;
// Created by booky10 in DeathRunEvent (21:00 02.07.21)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import tk.booky.deathrunevent.DeathRunEventMain;
import tk.booky.deathrunevent.DeathRunEventManager;
import tk.booky.deathrunevent.utils.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DistanceManager {

    private static final Map<UUID, Integer> DISTANCES = new ConcurrentHashMap<>();
    private static final List<UUID> SORTED = new ArrayList<>();
    private static final Object LOCK = new byte[8];

    public static void update(HumanEntity entity) {
        int blocks;

        if (DeathRunEventManager.isRunning() && entity.getGameMode().equals(GameMode.SURVIVAL)) {
            blocks = Math.max(entity.getLocation().getBlockZ(), 0);
            DISTANCES.put(entity.getUniqueId(), blocks);
        } else {
            blocks = DISTANCES.getOrDefault(entity.getUniqueId(), 0);
        }

        if (DeathRunEventManager.isRunning() || entity.getGameMode().equals(GameMode.SPECTATOR)) {
            Component component = Component.text(blocks + " Blöcke", NamedTextColor.AQUA);
            Integer place = getPlace(entity);

            if (place != null) {
                entity.sendActionBar(component
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(place + ". Platz", NamedTextColor.AQUA)));
            } else {
                entity.sendActionBar(component);
            }
        }
    }

    public static int getDistance(UUID uuid) {
        return DISTANCES.getOrDefault(uuid, 0);
    }

    public static void sortDistances() {
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(DISTANCES.entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        synchronized (SORTED) {
            SORTED.clear();
            for (Map.Entry<UUID, Integer> entry : sorted) {
                SORTED.add(entry.getKey());
            }
            Collections.reverse(SORTED);
        }
    }

    public static OfflinePlayer[] getTopPlayers(int count) {
        OfflinePlayer[] top = new OfflinePlayer[count];
        Arrays.fill(top, null);

        int i = 0;
        for (UUID uuid : SORTED) {
            top[i++] = Bukkit.getOfflinePlayer(uuid);

            if (i >= count - 1) {
                break;
            }
        }

        return top;
    }

    @SuppressWarnings("unchecked")
    public static Pair<Component, Integer>[] getTopNames(int count) {
        Pair<Component, Integer>[] top = new Pair[count];
        Arrays.fill(top, Pair.of(Component.empty(), 0));

        int i = 0;
        for (UUID uuid : SORTED) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                top[i++] = Pair.of(player.displayName(), DISTANCES.getOrDefault(uuid, 0));

                if (i >= count - 1) {
                    break;
                }
            }
        }

        return top;
    }

    public static Integer getPlace(Entity entity) {
        int place = SORTED.indexOf(entity.getUniqueId()) + 1;
        return place == 0 ? null : place;
    }

    public static void load() {
        synchronized (LOCK) {
            DeathRunEventMain.main.reloadConfig();
            FileConfiguration config = DeathRunEventMain.main.getConfig();
            ConfigurationSection section = config.getConfigurationSection("distances");

            if (section != null) {
                for (String key : section.getKeys(false)) {
                    DISTANCES.put(UUID.fromString(key), section.getInt(key, 0));
                }
            }

            DeathRunEventManager.setRunning(config.getBoolean("running", false));
            DeathRunEventManager.setStopping(config.getLong("stopping", -1));
        }
    }

    public static void save() {
        synchronized (LOCK) {
            DeathRunEventMain.main.reloadConfig();
            FileConfiguration config = DeathRunEventMain.main.getConfig();

            for (Map.Entry<UUID, Integer> entry : DISTANCES.entrySet()) {
                config.set("distances." + entry.getKey().toString(), entry.getValue());
            }

            config.set("running", DeathRunEventManager.isRunning());
            config.set("stopping", DeathRunEventManager.getStopping());

            DeathRunEventMain.main.saveConfig();
        }
    }
}
