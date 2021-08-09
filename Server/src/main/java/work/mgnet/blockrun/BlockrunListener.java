package work.mgnet.blockrun;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;

public class BlockrunListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		e.setDeathSound(Sound.ENTITY_ENDERMAN_DEATH);
		e.setShouldPlayDeathSound(true);
		if (Tournament.CURRENTGAME == Games.BLOCKRUN) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					e.getEntity().teleport(BlockrunRun.map.clone());
				}
			}, 5L);
			Bukkit.broadcastMessage(Prefix.BLOCKRUN + BlockrunConfiguration.deathMessages
					.get(new Random().nextInt(BlockrunConfiguration.deathMessages.size()))
					.replaceAll("%", e.getEntity().getName()));
			ArrayList<Player> runner = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getGameMode() == GameMode.ADVENTURE)
					runner.add(player);
			}
			runner.remove(e.getEntity());
			e.getEntity().sendMessage(Prefix.BLOCKRUN + "§6You earned "
					+ ((3 - runner.size()) > 0 ? (3 - runner.size()) : 0) + " points for your... uh..  talent?");
			StatisticManager.addPoints(e.getEntity().getName(), (3 - runner.size()) > 0 ? (3 - runner.size()) : 0);
			if (runner.size() == 1) {
				Bukkit.broadcastMessage(Prefix.BLOCKRUN
						+ "§6% didn't fall and earned 5 points!".replaceAll("%", runner.get(0).getName()));
				StatisticManager.addPoints(runner.get(0).getName(), 5);
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);

					p.resetMaxHealth();
					p.setHealth(20.0);
					p.getInventory().clear();
					p.setGameMode(GameMode.SPECTATOR);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {

					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {

							BlockrunRun.resetBlocks();

							Tournament.CURRENTGAME = Games.NONE;
							p.teleport(p.getWorld().getSpawnLocation());

							p.resetMaxHealth();
							p.setHealth(20.0);

							p.setGameMode(GameMode.ADVENTURE);
						}
					}
				}, 100L);
			}
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.hasItem() && Tournament.CURRENTGAME == Games.BLOCKRUN && e.getAction() == Action.RIGHT_CLICK_AIR) {
			if (e.getItem().getType() == Material.ENCHANTED_BOOK) {
				e.getItem().setAmount(e.getItem().getAmount() - 1);
				try {
					if (e.getItem().getAmount() == 0) e.getPlayer().getInventory().remove(Material.ENCHANTED_BOOK);
				} catch (Exception e4) {
					
				}
				e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(1D).setY(0.7D));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
		
				@Override
				public void run() {
					e.getPlayer().resetMaxHealth();
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				}
		}, 5L);
	}

}
