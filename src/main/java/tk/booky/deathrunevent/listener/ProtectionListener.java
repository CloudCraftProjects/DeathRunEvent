package tk.booky.deathrunevent.listener;
// Created by booky10 in DeathRunEvent (15:04 08.07.21)

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            int z = event.getEntity().getLocation().getBlockZ();
            if (Math.abs(z) < 32) event.setDamage(0);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        int z = event.getBlock().getZ();
        if (Math.abs(z) < 32) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        int z = event.hasBlock() ? event.getClickedBlock().getZ() : event.getPlayer().getLocation().getBlockZ();
        if (Math.abs(z) < 32) event.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        int z = event.getEntity().getLocation().getBlockZ();
        if (Math.abs(z) < 32) event.setCancelled(true);
    }
}
