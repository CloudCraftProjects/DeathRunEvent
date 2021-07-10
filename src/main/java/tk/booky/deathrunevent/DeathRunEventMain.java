package tk.booky.deathrunevent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tk.booky.deathrunevent.commands.StartCommand;
import tk.booky.deathrunevent.distance.DistanceManager;
import tk.booky.deathrunevent.distance.DistanceRunnable;
import tk.booky.deathrunevent.listener.JoinLeaveListener;
import tk.booky.deathrunevent.listener.ProtectionListener;
import tk.booky.deathrunevent.scoreboard.ScoreBoardManager;

public final class DeathRunEventMain extends JavaPlugin {

    public static DeathRunEventMain main;
    private final StartCommand command = new StartCommand();

    @Override
    public void onEnable() {
        main = this;
        saveDefaultConfig();

        DistanceManager.load();
        DistanceRunnable.start(this, 5, 100);

        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);

        command.register(Bukkit.getCommandMap());
        Bukkit.getCommandMap().register("dre", command);

        ScoreBoardManager.reloadBoards();
    }

    @Override
    public void onDisable() {
        DistanceManager.save();
        command.unregister(Bukkit.getCommandMap());
    }
}
