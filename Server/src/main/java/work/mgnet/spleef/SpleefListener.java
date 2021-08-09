package work.mgnet.spleef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.iciclerun.IcicleConfiguration;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class SpleefListener implements Listener {
	
	public static HashMap<Location, Material> snow = new HashMap<Location, Material>();
	
	@EventHandler
	public void removeBlock(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL && Tournament.CURRENTGAME == Games.SPLEEF && e.getHitBlock() != null) {
			snow.put(e.getHitBlock().getLocation(), e.getHitBlock().getType());
			e.getHitBlock().breakNaturally();
		} else if (e.getEntityType() == EntityType.SNOWBALL && Tournament.CURRENTGAME == Games.SPLEEF && e.getHitEntity() != null) {
			((Player) e.getHitEntity()).damage(0.1);
			((Player) e.getHitEntity()).setHealth(20);
			((Player) e.getHitEntity()).setVelocity(Vector.getRandom().normalize().setY(2).multiply(.3f));

		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRocket(ProjectileLaunchEvent e) {
		if (e.getEntityType() == EntityType.FIREWORK && Tournament.CURRENTGAME == Games.SPLEEF) {
			FireworkMeta meta = ((Firework) e.getEntity()).getFireworkMeta();
			meta.setPower(2);
			((Firework) e.getEntity()).setFireworkMeta(meta);
			((Firework) e.getEntity()).setPassenger((Entity) e.getEntity().getShooter());
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (Tournament.CURRENTGAME == Games.SPLEEF) {
			e.setDropItems(false);
			snow.put(e.getBlock().getLocation(), e.getBlock().getType());
			try {
				if (e.getPlayer().getInventory().getItemInOffHand().getType() == Material.SNOWBALL) {
					if (new Random().nextInt(4) == 2) {
						e.getPlayer().getInventory().getItemInOffHand().add(1);
					
						if (new Random().nextInt(100) == 2) {
							e.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
							e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
							e.getPlayer().sendMessage(Prefix.SPLEEF + "§5Rare Drop: §lEnder Pearl");
						} else if (new Random().nextInt(50) == 2) {
							e.getPlayer().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET));
							e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
							e.getPlayer().sendMessage(Prefix.SPLEEF + "§5Rare Drop: §lRocket");
						}
					
					}
				} else {
					e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.SNOWBALL));
				}
			} catch (Exception e2) {
				e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.SNOWBALL));
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (Tournament.CURRENTGAME == Games.SPLEEF) {
			Bukkit.broadcast(Component.text(Prefix.SPLEEF + IcicleConfiguration.cubeDeathMessages.get(new Random().nextInt(IcicleConfiguration.cubeDeathMessages.size())).replaceAll("%", e.getEntity().getName())));
			ArrayList<Player> spleefers = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getGameMode() == GameMode.SURVIVAL) spleefers.add(player);
			}
			spleefers.remove(e.getEntity());
			e.getEntity().sendMessage(Prefix.SPLEEF + "§6You earned " + ((3 - spleefers.size()) > 0 ? (3 - spleefers.size()) : 0) + " points for your spleefing!");
			StatisticManager.addPoints(e.getEntity().getName(), (3 - spleefers.size()) > 0 ? (3 - spleefers.size()) : 0);
			if (spleefers.size() == 1) {
				Configuration.restrictBreakAndPlace = true;
				Configuration.restrictInteract = true;
				Configuration.restrictInventory = true;
				Bukkit.broadcast(Component.text(Prefix.SPLEEF + "§6% was the climber of the hill and earned 5 points!".replaceAll("%", spleefers.get(0).getName())));
				StatisticManager.addPoints(spleefers.get(0).getName(), 5);
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
					p.getInventory().clear();
					p.teleport(p.getWorld().getSpawnLocation());
					p.setGameMode(GameMode.SPECTATOR);
				}
				try {
					UtilListener.updateTickrate(20f);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
					
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							
							SpleefRun.replaceBlocks();
							p.teleport(p.getWorld().getSpawnLocation());
							
							Tournament.CURRENTGAME = Games.NONE;
							p.setGameMode(GameMode.ADVENTURE);
						}
					}
				}, 100L);
			}
		}
	}
	
}
