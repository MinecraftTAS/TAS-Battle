package de.pfannekuchen.ffa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Simple events for the FFA Plugin
 * @author Pancake
  */
public class Events implements Listener {

	/** Map that holds all players kit votes */
	public static HashMap<UUID, String> kits = new HashMap<>();
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	private static boolean isRunning;
	/** Allow voting */
	public static boolean shouldAllowVoting = true;
	/** Alive players of the game */
	private static List<Player> alivePlayers = new LinkedList<>();
	
	private static Events instance;
	public Events() { Events.instance = this; }
	public static Events instance() { return instance; }

	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerBlockPlace(BlockPlaceEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerDamage(EntityDamageEvent e) { if (!e.getEntity().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	
	/**
	 * Whenever a player join, play a sound, send them a Message and start the game if can
	 * @param e Join Event
	 */
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		p.sendActionBar(Component.text("\u00A7cLC to view a kit. RC to vote a kit."));
		for (String map : FFA.availableKits.keySet()) {
			ItemStack item = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
			item.editMeta(c -> {
				c.displayName(Component.text("\u00A7f" + map));
			});
			p.getInventory().addItem(item);
		}
		e.joinMessage(null);
	}
	
	/**
	 * Whenever a player clicks on a Kit item open or select the Kit 
	 * @param e Interaction Event
	 * @throws IOException Well shit
	 * @throws IllegalStateException Well shit
	 */
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e) throws IllegalStateException, IOException {
		if (isRunning || startingTask != null || e.getItem() == null) return;
		if (e.getItem().getType() == Material.CYAN_STAINED_GLASS_PANE && e.getItem().getItemMeta().hasDisplayName()) {
			String name = PlainTextComponentSerializer.plainText().serialize(e.getItem().getItemMeta().displayName()).replaceAll("\u00A7f", "");
			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				byte[][] inventorySave = Serialization.playerInventoryToBase64(e.getPlayer().getInventory());
				Serialization.base64ToPlayerInventory(e.getPlayer(), FFA.availableKits.get(name));
				new BukkitRunnable() {
					
					@Override
					public void run() {
						try {
							Serialization.base64ToPlayerInventory(e.getPlayer(), inventorySave); // Revert back Inventory
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
					}
				}.runTaskLater(FFA.instance(), 20L * 6L);
			} else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.getPlayer().sendMessage(Component.text("\u00A7b\u00bb \u00A77You selected kit " + name));
				onPlayerVoteKitEvent(e.getPlayer(), name);
			}
		}
	}
	
	/**
	 * Clear a player from the game or vote list if they leave the server
	 */
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		e.quitMessage(null);
		if (kits.containsKey(e.getPlayer().getUniqueId())) kits.remove(e.getPlayer().getUniqueId());
		if (startingTask != null) {
			int playersLeft = Bukkit.getOnlinePlayers().size() - 1;
			if (playersLeft < 2) {
				/* Cancel the game starting */
				startingTask.cancel();
				startingTask = null;
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will not start."));
			}
		}
		if (alivePlayers.contains(e.getPlayer()) && isRunning) onPlayerOut(e.getPlayer());
	}
	
	/**
	 * Whenever a player dies, remove them from the game
	 * @param e Death Event for Players
	 */
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		e.deathMessage(null);
		if (alivePlayers.contains(e.getEntity()) && isRunning) onPlayerOut(e.getEntity());
	}
	
	/**
	 * Whenever a player selects a kit with RC
	 * @throws IOException Uhh, cry?
	 */
	public void onPlayerVoteKitEvent(Player p, String kitToVote) throws IOException {
		if (kits.containsKey(p.getUniqueId())) kits.remove(p.getUniqueId()); // Undo Vote
		kits.put(p.getUniqueId(), kitToVote);
		if (kits.size() >= Bukkit.getOnlinePlayers().size() && Bukkit.getOnlinePlayers().size() >= 2) {
			/* Find most voted kit */
			int mostVotes = -1;
			List<String> votedKits = new ArrayList<>();
			for (Map.Entry<UUID, String> entry : kits.entrySet()) {
				int votes = Collections.frequency(kits.values(), entry.getValue());
				if (votes > mostVotes) {
					votedKits.clear();
					votedKits.add(entry.getValue());
				} else if (votes == mostVotes) {
					votedKits.add(entry.getValue());
				}
			}
			int finalKitIndex = new Random().nextInt(votedKits.size());
			File kit = new File(FFA.instance().getDataFolder(), votedKits.get(finalKitIndex));
			byte[][] items = new byte[3][];
			items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
			items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
			items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
			FFA.serializedSelectedKit = items;
			FFA.selectedKitName = kit.getName();
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Kit \uu00A7a" + kit.getName() + "\u00A77 was selected"));
			onKitSelectedEvent();
		}
	}
	
	/**
	 * Whenever a player dies or quits the server
	 * @param player The Player that died
	 */
	public void onPlayerOut(Player player) {
		alivePlayers.remove(player);
		Player killer = player.getKiller();
		if (killer == null)
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
		else 
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 was slain by \u00A7a" + killer.getName()));
		if (alivePlayers.size() <= 1) {
			Player winner = alivePlayers.get(0);
			if (winner != null)
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + winner.getName() + "\u00A77 won the game!"));
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(FFA.instance(), 20L * 5L);
		}
	}
	
	/**
	 * Whenever a kit gets selected, a countdown of 10 seconds will start, after which the players will be spread across the map.
	 */
	public boolean onKitSelectedEvent() {
		if (Bukkit.getOnlinePlayers().size() >= 2 /* Check if the game is startable */)  {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The selected kit is: \u00A7b" + FFA.selectedKitName + "\u00A77."));
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will start in 10 seconds."));
			startingTask = new BukkitRunnable() {
				@Override public void run() {
					/* Start the game */
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						Random rng = new Random();
						isRunning = true;
						for (Player p : Bukkit.getOnlinePlayers()) {
							Serialization.base64ToPlayerInventory(p, FFA.serializedSelectedKit); // load kit
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getWorld().setDifficulty(Difficulty.HARD);
							p.setExp(0.0f);
							alivePlayers.add(p);
							/* Teleport the player */
							while (true) {
								int x = rng.nextInt(801) - 400;
								int z = rng.nextInt(801) - 400;
								Block b = p.getWorld().getHighestBlockAt(x, z);
								if (b != null && b.getType() != Material.AIR) {
									p.teleport(new Location(p.getWorld(), x, b.getY() + 1, z));
									break;
								}
							}
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(FFA.instance(), 10*20L);
		} else {
			return false;
		}
		return true;
	}

}
