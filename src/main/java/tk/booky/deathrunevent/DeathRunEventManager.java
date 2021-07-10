package tk.booky.deathrunevent;
// Created by booky10 in DeathRunEvent (22:20 02.07.21)

import com.google.common.io.Files;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tk.booky.deathrunevent.distance.DistanceManager;
import tk.booky.deathrunevent.scoreboard.ScoreBoardManager;
import tk.booky.deathrunevent.utils.Pair;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Color.*;

public class DeathRunEventManager {

    private static boolean running, starting;
    private static long stopping;

    public static long getRemainingTime() {
        return stopping - System.currentTimeMillis();
    }

    public static void startEvent(long time) {
        if (isRunning() || isStarting()) return;
        setStarting(true);

        World world = Bukkit.getWorlds().get(0);
        int timeId = Bukkit.getScheduler().runTaskTimer(DeathRunEventMain.main,
                () -> world.setTime(world.getTime() + 20), 20, 1).getTaskId();

        new BukkitRunnable() {
            private int countdown = 30;

            @Override
            public void run() {
                switch (countdown--) {
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        Title.Times countdownTimes = Title.Times.of(Ticks.duration(5), Ticks.duration(10), Ticks.duration(5));
                        Title countdownTitle = Title.title(Component.text(countdown + 1, NamedTextColor.GOLD, TextDecoration.BOLD), Component.empty(), countdownTimes);

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.AMBIENT, 100000f, 1f);
                            player.showTitle(countdownTitle);
                        }

                        if (countdown == 0) {
                            Bukkit.broadcast(Component.text("Das Event startet in 1 Sekunde!", NamedTextColor.GREEN));
                            break;
                        }
                    case 30:
                    case 15:
                        Bukkit.broadcast(Component.text("Das Event startet in " + (countdown + 1) + " Sekunden!", NamedTextColor.GREEN));
                        break;
                    case 0:
                        Title.Times startTimes = Title.Times.of(Ticks.duration(10), Ticks.duration(100), Ticks.duration(20));
                        Title startTitle = Title.title(Component.text("CloudCraft"), Component.text("DeathRun Event", NamedTextColor.GREEN), startTimes);

                        Component message = Component
                                .text("Das Event hat begonnen!\n", NamedTextColor.GREEN)
                                .append(Component.text(" » Lauf in Richtung Süden!", NamedTextColor.GRAY, TextDecoration.ITALIC));

                        Bukkit.getConsoleSender().sendMessage(message);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.AMBIENT, 100000f, 1f);
                            player.sendMessage(message);
                            player.showTitle(startTitle);
                        }

                        Bukkit.getScheduler().runTaskLater(DeathRunEventMain.main,
                                () -> world.getWorldBorder().setSize(59999968), 20 * 100);
                        world.getWorldBorder().setSize(world.getWorldBorder().getSize() + 2000, 100);

                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                        world.setGameRule(GameRule.DO_MOB_SPAWNING, true);

                        stopping = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(time);
                        ScoreBoardManager.start().reload();

                        setStarting(false);
                        setRunning(true);

                        Bukkit.getScheduler().cancelTask(timeId);
                        Bukkit.getScheduler().cancelTask(getTaskId());
                    default:
                        break;
                }
            }
        }.runTaskTimer(DeathRunEventMain.main, 20, 20);
    }

    public static void stopEvent() {
        try {
            DistanceManager.save();
            Files.copy(
                    new File(DeathRunEventMain.main.getDataFolder(), "config.yml"),
                    new File(DeathRunEventMain.main.getDataFolder(), System.currentTimeMillis() + ".config.yml")
            );
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.addPotionEffect(nightVision);
        }

        OfflinePlayer[] topPlayers = DistanceManager.getTopPlayers(1);
        Pair<Component, Integer>[] topNames = DistanceManager.getTopNames(3);

        Title.Times times = Title.Times.of(Ticks.duration(10), Ticks.duration(100), Ticks.duration(20));
        Title title = Title.title(Component.text("Der Gewinner:", NamedTextColor.WHITE, TextDecoration.BOLD), topNames[0].key().style(Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)), times);

        Player winner = topPlayers[0] == null ? null : topPlayers[0].getPlayer();
        Component top = Component.empty();

        int i = 1;
        for (Pair<Component, Integer> target : topNames) {
            top = top.append(Component
                    .text(
                            "\n  " + (i++) + ". Platz: ",
                            i == 2 ? NamedTextColor.GOLD : NamedTextColor.WHITE,
                            TextDecoration.BOLD
                    )
                    .append(target.key().append(Component.text(" - " + target.value() + " Blöcke")))
            );
        }

        Component message = Component
                .text("Das Event wurde beendet!\n", NamedTextColor.RED)
                .append(Component.text("\nTop " + (i - 1) + ":", NamedTextColor.YELLOW))
                .append(top);

        if (winner != null) {
            Color[] colors = new Color[]{WHITE, SILVER, GRAY, BLACK, RED, MAROON, YELLOW, OLIVE, LIME, GREEN, AQUA, TEAL, BLUE, NAVY, FUCHSIA, PURPLE, ORANGE};
            FireworkMeta meta = (FireworkMeta) new ItemStack(Material.FIREWORK_ROCKET).getItemMeta();

            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder().withColor(colors).withFade(colors).withTrail().withFlicker().build());
            meta.addEffect(FireworkEffect.builder().withColor(colors).withFade(colors).withTrail().build());
            meta.addEffect(FireworkEffect.builder().withColor(colors).withFade(colors).build());
            meta.addEffect(FireworkEffect.builder().withColor(colors).build());

            new BukkitRunnable() {
                private final Location location = winner.getEyeLocation();
                private int count = 10;

                @Override
                public void run() {
                    if (count-- == 0) {
                        cancel();
                    } else {
                        location.getWorld().spawn(location, Firework.class).setFireworkMeta(meta);
                    }
                }
            }.runTaskTimer(DeathRunEventMain.main, 0, 20);
        } else {
            top = top.append(Component.text("\n\nDer Gewinner ist leider offline!", NamedTextColor.RED, TextDecoration.BOLD));
        }

        Bukkit.getConsoleSender().sendMessage(top);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100000f, 1f);
            player.sendMessage(top);
            player.showTitle(title);
        }

        setRunning(false);
        setStopping(-1);
    }

    public static boolean isRunning() {
        return running;
    }

    public static boolean isStarting() {
        return starting;
    }

    public static long getStopping() {
        return stopping;
    }

    public static void setRunning(boolean running) {
        DeathRunEventManager.running = running;
    }

    public static void setStarting(boolean starting) {
        DeathRunEventManager.starting = starting;
    }

    public static void setStopping(long stopping) {
        DeathRunEventManager.stopping = stopping;
    }
}
