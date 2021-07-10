package tk.booky.deathrunevent.listener;
// Created by booky10 in DeathRunEvent (21:39 02.07.21)

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.booky.deathrunevent.distance.DistanceManager;
import tk.booky.deathrunevent.scoreboard.ScoreBoardManager;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ScoreBoardManager.addPlayer(event.getPlayer());
        DistanceManager.update(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ScoreBoardManager.removePlayer(event.getPlayer());
        DistanceManager.update(event.getPlayer());
    }
}
