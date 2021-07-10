package tk.booky.deathrunevent.scoreboard;
// Created by booky10 in DeathRunEvent (21:41 02.07.21)

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreBoardManager {

    private static final BoardRunnable RUNNABLE = new BoardRunnable();

    public static BoardRunnable start() {
        return RUNNABLE.start();
    }

    public static void addPlayer(Player player) {
        RUNNABLE.setScoreboard(player);
    }

    public static void removePlayer(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public static void reloadBoards() {
        RUNNABLE.reload();
    }
}
