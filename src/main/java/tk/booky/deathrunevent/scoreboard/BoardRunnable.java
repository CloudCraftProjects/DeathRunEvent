package tk.booky.deathrunevent.scoreboard;
// Created by booky10 in DeathRunEvent (21:42 02.07.21)

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tk.booky.deathrunevent.DeathRunEventMain;
import tk.booky.deathrunevent.DeathRunEventManager;
import tk.booky.deathrunevent.distance.DistanceManager;
import tk.booky.deathrunevent.utils.Pair;

import java.text.DecimalFormat;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class BoardRunnable extends BukkitRunnable {

    private static final Component SEPERATOR = text(" » "), TAB = text("  ");
    private static final Component TITLE = text("DeathRun Event", GOLD, BOLD);
    private static final DecimalFormat FORMAT = new DecimalFormat("00");
    private static final ChatColor[] COLORS = ChatColor.values();
    private Scoreboard scoreboard;

    @Override
    public void run() {
        if (DeathRunEventManager.isRunning()) {
            // Top 3 players
            Pair<Component, Integer>[] top = DistanceManager.getTopNames(3);
            getTeam(6).prefix(TAB.append(top[0].key().color(AQUA).append(SEPERATOR.append(text(top[0].value())))));
            getTeam(5).prefix(TAB.append(top[1].key().color(AQUA).append(SEPERATOR.append(text(top[1].value())))));
            getTeam(4).prefix(TAB.append(top[2].key().color(AQUA).append(SEPERATOR.append(text(top[2].value())))));

            // Remaining time
            long remaining = DeathRunEventManager.getRemainingTime();
            long hours = 0, minutes = 0, seconds = remaining / 1000;

            while (seconds >= 60) {
                seconds -= 60;
                minutes += 1;
            }

            while (minutes >= 60) {
                minutes -= 60;
                hours += 1;
            }

            getTeam(1).prefix(Component.text("  " + hours + ":" + FORMAT.format(minutes) + ":" + FORMAT.format(seconds), AQUA));
        }
    }

    public BoardRunnable start() {
        runTaskTimerAsynchronously(DeathRunEventMain.main, 0, 20);
        return this;
    }

    public BoardRunnable reload() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", TITLE);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int line = 0; line <= 8; line++) {
            Team scoreTeam = scoreboard.registerNewTeam("line_" + line);

            String entry = COLORS[line].toString();
            scoreTeam.addEntry(entry);

            objective.getScore(entry).setScore(line);
        }

        updateStaticLines();
        setScoreboards();

        return this;
    }

    public void setScoreboards() {
        Bukkit.getOnlinePlayers().forEach(this::setScoreboard);
    }

    public void setScoreboard(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void updateStaticLines() {
        getTeam(7).prefix(Component.text("Top 3:", YELLOW));
        getTeam(6).prefix(Component.text("  Warten auf Start... » 0", AQUA));
        getTeam(5).prefix(Component.text("  Warten auf Start... » 0", AQUA));
        getTeam(4).prefix(Component.text("  Warten auf Start... » 0", AQUA));

        getTeam(2).prefix(Component.text("Verbleibende Zeit:", YELLOW));
        getTeam(1).prefix(Component.text("  Warten auf Start...", AQUA));
    }

    protected Team getTeam(int line) {
        Team team = scoreboard.getTeam("line_" + line);

        if (team == null) {
            throw new NullPointerException("Couldn't find team named \"line_" + line + "\" on scoreboard!");
        } else {
            return team;
        }
    }
}
