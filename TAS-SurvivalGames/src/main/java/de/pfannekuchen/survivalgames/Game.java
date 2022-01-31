package de.pfannekuchen.survivalgames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import de.pfannekuchen.survivalgames.stats.PlayerStats;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

/**
 * Class that controls the Game
 * @author Pancake
 */
public class Game {

	/** List of ready players */
	public static ArrayList<Player> readyplayers = new ArrayList<>();
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	public static boolean isRunning;
	/** Alive players of the game */
	public static List<Player> alivePlayers = new LinkedList<>();
	
	public static final Vector[] v = new Vector[] {
		new Vector(-12.5, 127.5, 107.5),
		new Vector(-14.5, 127.5, 116.5),
		new Vector(-23.5, 127.5, 118.5),
		new Vector(-31.5, 127.5, 116.5),
		new Vector(-33.5, 127.5, 107.5),
		new Vector(-32.5, 127.5, 98.5),
		new Vector(-22.5, 127.5, 96.5),
		new Vector(-13.5, 127.5, 98.5)
	};
	
	/**
	 * Whether Interactions such as block breaking, damage and interactions should be cancelled
	 * @param entity Player to interact
	 * @return Whether the event should be cancelled or not
	 */
	public static boolean shouldAllowInteraction(Entity entity) {
		if (entity instanceof Player && Game.isRunning && !alivePlayers.contains((Player) entity))
			return true;
		return !entity.isOp() && !Game.isRunning;
	}

	/**
	 * Runs on player interaction
	 * @param p Player to interact
	 * @param item Item interacted with
	 * @param action Action used
	 * @throws Exception Happens..
	 */
	public static void onInteract(Player p, ItemStack item, Action action) throws Exception {
		if (isRunning || startingTask != null || item == null) return;
		if (item.getItemMeta().hasDisplayName()) {
			if (item.getType() == Material.RED_STAINED_GLASS_PANE) {
				ItemStack ready = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
				ready.editMeta(meta -> {
					meta.displayName(Component.text("\u00A72You are ready"));
					meta.lore(Arrays.asList(
						Component.text().build(),
						Component.text("\u00A75Click this item to change your ready status."),
						Component.text("\u00A75Once all players are ready, the game will begin."),
						Component.text().build()
					));
				});
				p.getInventory().setItem(8, ready);
				p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_DRAGON_HURT, Source.MASTER, 1.0f, 2.0f));
				onPlayerReady(p, true);
			} else if (item.getType() == Material.LIME_STAINED_GLASS_PANE) {
				ItemStack unready = new ItemStack(Material.RED_STAINED_GLASS_PANE);
				unready.editMeta(meta -> {
					meta.displayName(Component.text("\u00A7cYou are not ready!"));
					meta.lore(Arrays.asList(
						Component.text().build(),
						Component.text("\u00A75Click this item to change your ready status."),
						Component.text("\u00A75Once all players are ready, the game will begin."),
						Component.text().build()
					));
				});
				p.getInventory().setItem(8, unready);
				p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.MASTER, 1.0f, 2.0f));
				onPlayerReady(p, false);
			}
		}
	}
	/**
	 * Runs on player connect
	 * @param p Player to connect
	 */
	public static void onJoin(Player p) {
		p.setFallDistance(0.0f);
		p.teleport(p.getWorld().getSpawnLocation());
		// prepare scoreboard
		Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = s.registerNewObjective(p.getName(), "dummy", Component.text("SurvivalGames"), RenderType.INTEGER);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.getScore("discord.gg/hUcYSFnJsW").setScore(0);
		o.getScore(" ").setScore(1);
		o.getScore("\u00A7aKDR: \u00A7f" + String.format("%.2f", PlayerStats.getKills(p) / ((double) PlayerStats.getDeaths(p)))).setScore(2);
		o.getScore("\u00A7bDeaths: \u00A7f" + PlayerStats.getDeaths(p)).setScore(3);
		o.getScore("\u00A7bKills: \u00A7f" + PlayerStats.getKills(p)).setScore(4);
		o.getScore("  ").setScore(5);
		o.getScore("\u00A7aWLR: \u00A7f" + String.format("%.2f", PlayerStats.getWins(p) / ((double) PlayerStats.getLosses(p)))).setScore(6);
		o.getScore("\u00A7bLosses: \u00A7f" + PlayerStats.getLosses(p)).setScore(7);
		o.getScore("\u00A7bWins: \u00A7f" + PlayerStats.getWins(p)).setScore(8);
		o.getScore("   ").setScore(9);
		o.getScore("\u00A7bYour Stats").setScore(10);
		p.setScoreboard(s);
		if (isRunning) {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
			p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
			p.setGameMode(GameMode.SPECTATOR);
			return;
		}
		if (startingTask != null) {
			p.sendMessage(Component.text("\u00A7b\u00bb \u00A7aThe game will start soon!"));
		}
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		// prepare the ready/unready item
		ItemStack ready = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ready.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7cYou are not ready!"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75Click this item to change your ready status."),
				Component.text("\u00A75Once all players are ready, the game will begin."),
				Component.text().build()
			));
		});
		p.getInventory().setItem(8, ready);
		p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
	}
	
	/**
	 * Runs on player disconnect
	 * @param p Player to disconnect
	 */
	public static void onQuit(Player p) {
		if (readyplayers.contains(p)) readyplayers.remove(p);
		
		if (startingTask != null) {
			int playersLeft = Bukkit.getOnlinePlayers().size() - 1;
			if (playersLeft < 2) {
				/* Cancel the game starting */
				startingTask.cancel();
				startingTask = null;
				for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will not start."));
			}
		}
		if (alivePlayers.contains(p) && isRunning) onPlayerOut(p);
	}


	/**
	 * Whenever a player selects a kit with RC
	 * @throws IOException Uhh, cry?
	 */
	public static void onPlayerReady(Player p, boolean status) throws IOException {
		if (readyplayers.contains(p) && !status) readyplayers.remove(p);
		if (status && !readyplayers.contains(p)) readyplayers.add(p);
		if (readyplayers.size() >= Bukkit.getOnlinePlayers().size() && Bukkit.getOnlinePlayers().size() >= 2) {
			/* Find most voted kit */
			startGame();
		}
	}

	/**
	 * Whenever a player dies or quits the server
	 * @param player The Player that died
	 */
	public static void onPlayerOut(Player player) {
		alivePlayers.remove(player);
		PlayerStats.addDeath(player);
		PlayerStats.addLoss(player);
		player.setGameMode(GameMode.SPECTATOR);
		Player killer = player.getKiller();
		if (killer == null)
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
		else {
			if (killer == player) {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
			} else {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 was slain by \u00A7a" + killer.getName()));
				PlayerStats.addKill(killer);
				killer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
			}
		}
		if (alivePlayers.size() <= 1) {
			try {
				SurvivalGames.updateTickrate(1.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Player winner = alivePlayers.get(0);
			for (Player player2 : Bukkit.getOnlinePlayers()) {
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
			}
			if (winner != null) {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + winner.getName() + "\u00A77 won the game!"));
				winner.showTitle(Title.title(Component.text("\u00A7cYou won!"), Component.empty()));
				PlayerStats.addWin(winner);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						SurvivalGames.updateTickrate(20.0f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(SurvivalGames.instance(), 16L);
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(SurvivalGames.instance(), 6L+16L);
		}
	}

	/**
	 * Whenever a kit gets selected, a countdown of 10 seconds will start, after which the players will be spread across the map.
	 */
	public static boolean startGame() {
		if (Bukkit.getOnlinePlayers().size() >= 2 /* Check if the game is startable */)  {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will start in 10 seconds."));
			for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.BLOCK, .6f, 1.2f), Sound.Emitter.self());
			startingTask = new BukkitRunnable() {
				@Override public void run() {
					/* Start the game */
					try {
						SurvivalGames.updateTickrate(4);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
						isRunning = true;
						int i = -1;
						for (Player p : Bukkit.getOnlinePlayers()) {
							i++;
							p.getInventory().clear();
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
							p.getWorld().setDifficulty(Difficulty.HARD);
							p.setExp(0.0f);
							alivePlayers.add(p);
							/* Teleport the player */
							p.setFallDistance(0.0f);
							p.teleport(new Location(p.getWorld(), v[i].getX(), v[i].getY(), v[i].getZ()));
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(SurvivalGames.instance(), 10*20L);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Runs on player death
	 * @param p Player that died
	 */
	public static void onDeath(Player p) {
		p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_SCREAM, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
		if (alivePlayers.contains(p) && isRunning) onPlayerOut(p);
	}

	public static final ItemStack[] items = new ItemStack[] {
		// put twice for higher odds
		new ItemStack(Material.WOODEN_SWORD),
		new ItemStack(Material.STONE_SWORD),
		new ItemStack(Material.IRON_SWORD),
		new ItemStack(Material.GOLDEN_SWORD),
		new ItemStack(Material.DIAMOND_SWORD),
		new ItemStack(Material.WOODEN_SWORD),
		new ItemStack(Material.STONE_SWORD),
		new ItemStack(Material.IRON_SWORD),
		new ItemStack(Material.GOLDEN_SWORD),
		new ItemStack(Material.DIAMOND_SWORD),
		
		new ItemStack(Material.LEATHER_HELMET),
		new ItemStack(Material.LEATHER_CHESTPLATE),
		new ItemStack(Material.LEATHER_LEGGINGS),
		new ItemStack(Material.LEATHER_BOOTS),
		
		new ItemStack(Material.IRON_HELMET),
		new ItemStack(Material.IRON_CHESTPLATE),
		new ItemStack(Material.IRON_LEGGINGS),
		new ItemStack(Material.IRON_BOOTS),
		
		new ItemStack(Material.CHAINMAIL_HELMET),
		new ItemStack(Material.CHAINMAIL_CHESTPLATE),
		new ItemStack(Material.CHAINMAIL_LEGGINGS),
		new ItemStack(Material.CHAINMAIL_BOOTS),
		
		new ItemStack(Material.GOLDEN_HELMET),
		new ItemStack(Material.GOLDEN_CHESTPLATE),
		new ItemStack(Material.GOLDEN_LEGGINGS),
		new ItemStack(Material.GOLDEN_BOOTS),
		
		new ItemStack(Material.IRON_PICKAXE),
		new ItemStack(Material.IRON_AXE),
		new ItemStack(Material.STONE_PICKAXE),
		new ItemStack(Material.STONE_AXE),
		new ItemStack(Material.IRON_PICKAXE),
		new ItemStack(Material.IRON_AXE),
		new ItemStack(Material.STONE_PICKAXE),
		new ItemStack(Material.STONE_AXE),
		
		new ItemStack(Material.OAK_PLANKS, 32),
		new ItemStack(Material.SPRUCE_PLANKS, 32),
		new ItemStack(Material.DARK_OAK_PLANKS, 32),
		new ItemStack(Material.ACACIA_PLANKS, 32),
		new ItemStack(Material.APPLE, 2),
		new ItemStack(Material.GOLDEN_APPLE, 1),
		new ItemStack(Material.CAKE),
		new ItemStack(Material.CARROT, 2),
		new ItemStack(Material.LAPIS_LAZULI, 2),
		new ItemStack(Material.ENCHANTING_TABLE),
		new ItemStack(Material.EXPERIENCE_BOTTLE, 32),
		
		new ItemStack(Material.FLINT_AND_STEEL),
		new ItemStack(Material.FISHING_ROD),
		new ItemStack(Material.BOW),
		new ItemStack(Material.ARROW, 2),
		new ItemStack(Material.ENDER_PEARL),
	};
	
	/**
	 * Fills a Container with loot
	 * @param clickedBlock Container to fill
	 */
	public static void fillLoot(Container clickedBlock) {
		int invSize = clickedBlock.getInventory().getSize();
		clickedBlock.getInventory().clear();
		Random r = new Random();
		for (int c = 0; c < 4; c++) {
			ItemStack i = items[r.nextInt(items.length)];
			if (i.getAmount() == 32) {
				i.setAmount(r.nextInt(48) + 16);
			} else if (i.getAmount() == 2) {
				i.setAmount(r.nextInt(16) + 1);
			}
			clickedBlock.getInventory().setItem(r.nextInt(invSize), i);
		}
	}

}
