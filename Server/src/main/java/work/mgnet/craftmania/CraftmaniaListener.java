package work.mgnet.craftmania;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;

public class CraftmaniaListener implements Listener {
	
	public static int rounds = 1;
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (Tournament.CURRENTGAME != Games.CRAFTMANIA) return;
		if (e.getRecipe().getResult().getType() != CraftmaniaRun.recipe) {
			e.getWhoClicked().getInventory().addItem(CraftmaniaConfiguration.items.get(CraftmaniaRun.recipe));
			return;
		}
		CraftmaniaRun.recipe = null;
		for (Player p : Bukkit.getOnlinePlayers()) {
			try{ 
				p.getOpenInventory().getTopInventory().clear();
			} catch (Exception e22) {
				
			}
			p.closeInventory();
			p.getInventory().clear();
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_WORK_FLETCHER, 1.0f, 1.0f);
		}
		Bukkit.broadcast(Component.text(Prefix.CRAFTMANIA + e.getWhoClicked().getName() + " was the fastest!"));
		rounds++;
		if (rounds < 5) Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				CraftmaniaRun.setCrafting();
			}
		}, 40L);
		else {
			Tournament.CURRENTGAME = Games.NONE;
			Bukkit.broadcast(Component.text(Prefix.CRAFTMANIA + "The Game is done!"));
			Configuration.restrictInventory = true;
			Configuration.restrictCrafting = true;
			Configuration.restrictInteract = true;
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
			}
		}
		StatisticManager.addPoints(e.getWhoClicked().getName(), 1);
	}
	
	@EventHandler
	public void onInv(InventoryCloseEvent e) {
		if (Tournament.CURRENTGAME == Games.CRAFTMANIA && e.getInventory().getType() == InventoryType.WORKBENCH && CraftmaniaRun.recipe != null) {
			Bukkit.getScheduler().runTaskLater(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					e.getPlayer().openWorkbench(null, true);
				}
			}, 5L);
		}
	}
	
}
