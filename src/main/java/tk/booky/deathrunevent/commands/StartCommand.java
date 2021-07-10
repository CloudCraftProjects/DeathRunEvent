package tk.booky.deathrunevent.commands;
// Created by booky10 in DeathRunEvent (22:12 02.07.21)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tk.booky.deathrunevent.DeathRunEventManager;

import java.util.Collections;
import java.util.List;

public class StartCommand extends Command {

    public StartCommand() {
        super("start", "Dieser Befehl startet das DeathRunEvent.", "/start <time>", Collections.emptyList());
        setPermission("dre.command.start");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) return true;

        if (DeathRunEventManager.isRunning()) {
            sender.sendMessage(Component.text("Das Event läuft schon!", NamedTextColor.RED));
        } else if (DeathRunEventManager.isStarting()) {
            sender.sendMessage(Component.text("Das Event wird schon gestartet!", NamedTextColor.RED));
        } else if (args.length >= 1) {
            try {
                DeathRunEventManager.startEvent(Long.parseLong(args[0]));
                sender.sendMessage(Component.text("Das Event wird gestartet!", NamedTextColor.GREEN, TextDecoration.ITALIC));
            } catch (NumberFormatException exception) {
                sender.sendMessage(Component.text(args[0] + " ist keine Zahl!", NamedTextColor.RED));
            }
        } else {
            sender.sendMessage(Component.text("Bitte gebe die Dauer (in Minuten) mit an!", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
