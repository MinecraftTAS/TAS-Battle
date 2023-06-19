package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleLobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

/**
 * Basic restrictions to the world
 * @author Pancake
 */
public class BasicRestrictions implements Listener {

    /**
     * Initialize basic restrictions
     * @param plugin Plugin
     */
    public BasicRestrictions(TASBattleLobby plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler // no join message
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);
    }

    @EventHandler // no quit message
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
    }

    @EventHandler // cancel damage
    public void onEntityDamage(EntityDamageEvent e) {
        e.setCancelled(!e.getEntity().isOp());
    }

    @EventHandler // cancel inventory click
    public void onClickEvent(InventoryClickEvent e) {
        e.setCancelled(!e.getWhoClicked().isOp());
    }

    @EventHandler // cancel block place
    public void onPlayerBreak(BlockBreakEvent e) {
        e.setCancelled(!e.getPlayer().isOp());
    }

    @EventHandler // cancel block break
    public void onPlayerPlace(BlockPlaceEvent e) {
        e.setCancelled(!e.getPlayer().isOp());
    }

    @EventHandler // cancel interact
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(!e.getPlayer().isOp());
    }

    @EventHandler // prevent drops
    public void onPlayerDrop(PlayerDropItemEvent e) {
        e.setCancelled(!e.getPlayer().isOp());
    }

    @EventHandler // prevent item consume
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(!e.getPlayer().isOp());
    }

    @EventHandler // prevent pickup
    public void onEntityPickup(EntityPickupItemEvent e) {
        e.setCancelled(!e.getEntity().isOp());
    }

    @EventHandler // prevent hunger
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(!e.getEntity().isOp());
    }

}
