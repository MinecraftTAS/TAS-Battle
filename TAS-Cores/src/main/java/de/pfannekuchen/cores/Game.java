package de.pfannekuchen.cores;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import de.pfannekuchen.cores.stats.PlayerStats;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;

/**
 * Class that controls the Game
 * @author Pancake
 */
public class Game {

	/** Map that holds all players kit */
	public static HashMap<UUID, String> kits = new HashMap<>();
	/** Map that holds all players tickrate votes */
	public static HashMap<UUID, Integer> tickrates = new HashMap<>();
	/** List of ready players */
	public static ArrayList<Player> readyplayers = new ArrayList<>();
	/** List of players on interact cooldown */
	public static ArrayList<Player> cooldownplayers = new ArrayList<>();
	/** List of players on interact cooldown */
	public static ArrayList<Player> team1 = new ArrayList<>();
	/** List of players on interact cooldown */
	public static ArrayList<Player> team2 = new ArrayList<>();
	/** Positions of the Cores */
	public static Location l1;
	public static Location r1;
	public static Location l2;
	public static Location r2;
	public static Location spawn1;
	public static Location spawn2;
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	public static boolean isRunning;
	/** Alive players of the game */
	public static List<Player> alivePlayers = new LinkedList<>();
	/** The selected tickrate */
	public static int tickrate = 4;
	/** All available kits */
	public static HashMap<String, byte[][]> availableKits = new HashMap<>();
	
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
	 * Runs on the Server Start
	 * @throws Exception
	 */
	public static void onStartup() throws Exception {
		/* Read available Kits */
		for (File folder : Cores.instance().getDataFolder().listFiles()) {
			if (!folder.isDirectory()) continue;
			File kit = new File(Cores.instance().getDataFolder(), folder.getName());
			byte[][] items = new byte[4][];
			items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
			items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
			items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
			items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
			availableKits.put(folder.getName(), items);
		}
		YamlConfiguration config = new YamlConfiguration();
		config.load(new File(Cores.instance().getDataFolder(), "map.yml"));
		l1 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("lone"));
		r1 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("rone"));
		l2 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("ltwo"));
		r2 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("rtwo"));
		spawn1 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("spawnone"));
		spawn2 = LocationUtils.parseLocation(Bukkit.getWorlds().get(0), config.getString("spawntwo"));
		l1.getBlock().setType(Material.OBSIDIAN);
		l2.getBlock().setType(Material.OBSIDIAN);
		r1.getBlock().setType(Material.OBSIDIAN);
		r2.getBlock().setType(Material.OBSIDIAN);
		/* Start a Thread that updates every second */
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) if (!isRunning) p.sendActionBar(Component.text("\u00A7cChoose your own kit."));
				l1.getWorld().spawnParticle(Particle.SPELL_WITCH, l1, 40);
				r1.getWorld().spawnParticle(Particle.SPELL_WITCH, r1, 40);
				l2.getWorld().spawnParticle(Particle.SPELL_WITCH, l2, 40);
				r2.getWorld().spawnParticle(Particle.SPELL_WITCH, r2, 40);
			}
		}.runTaskTimer(Cores.instance(), 0L, 20L);
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
		Objective o = s.registerNewObjective(p.getName(), "dummy", Component.text("Cores"), RenderType.INTEGER);
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
		// prepare the kit selector item
		ItemStack kit = new ItemStack(Material.CHEST);
		kit.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aSelect a kit"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will open a menu where the player can vote for a kit."),
				Component.text("\u00A75The kit with the most votes will be selected for the game."),
				Component.text().build()
			));
		});
		p.getInventory().setItem(0, kit);
		// prepare the tickrate changer item
		ItemStack tickrate = new ItemStack(Material.CLOCK);
		tickrate.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aSelect a tickrate"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will open a menu where the player can vote for a tickrate."),
				Component.text("\u00A75The tickrate with the most votes will be selected for the game."),
				Component.text().build()
			));
		});
		p.getInventory().setItem(1, tickrate);
		p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
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
			switch (item.getType()) {
				case CHEST:
					Inventory inv = Bukkit.createInventory(null, 18, Component.text("\u00A7aKit Selector"));
					for (String map : availableKits.keySet()) {
						Material mat = Material.getMaterial(Serialization.getIcon(availableKits.get(map)).replaceAll("\r", "").replaceAll("\n", ""));
						if (mat == null) mat = Material.RED_STAINED_GLASS_PANE;
						ItemStack kit = new ItemStack(mat);
						kit.editMeta(c -> {
							c.displayName(Component.text("\u00A7f" + map));
						});
						inv.addItem(kit); 
					}
					p.openInventory(inv);
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_CHEST_OPEN, Source.MASTER, 1.0f, 2.0f));
					break;
				case CLOCK:
					Inventory tinv = Bukkit.createInventory(null, 9, Component.text("\u00A7aTickrate Changer"));
					ItemStack kit = new ItemStack(Material.LIME_WOOL);
					kit.editMeta(c -> {
						c.displayName(Component.text("\u00A7aTickrate 2"));
					});
					tinv.setItem(0, kit);
					kit = new ItemStack(Material.CLOCK);
					kit.editMeta(c -> {
						c.displayName(Component.text("\u00A7eTickrate 4"));
					});
					tinv.setItem(4, kit);
					kit = new ItemStack(Material.RED_WOOL);
					kit.editMeta(c -> {
						c.displayName(Component.text("\u00A7cTickrate 10"));
					});
					tinv.setItem(8, kit);
					p.openInventory(tinv);
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_CHEST_OPEN, Source.MASTER, 1.0f, 2.0f));
					break;
				case RED_STAINED_GLASS_PANE:
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
					break;
				case LIME_STAINED_GLASS_PANE:
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
					break;
				default:
					break;
			}
		}
	}
	

	/**
	 * Runs on inventory interaction
	 * @param whoClicked Player that clicked
	 * @param action Action that occured
	 * @param currentItem Item clicked
	 * @param cursor Item below cursor
	 * @param inventory Inventory opened
	 * @param clickedInventory Inventory of click
	 * @param slot Slot of click
	 */
	public static void onInventory(Player p, InventoryAction action, ItemStack currentItem, ItemStack cursor, Inventory inventory, Inventory clickedInventory, int slot) {
		if (isRunning || startingTask != null || currentItem == null) return;
		if (currentItem.getItemMeta().hasDisplayName()) {
			String name = PlainTextComponentSerializer.plainText().serialize(currentItem.getItemMeta().displayName()).replaceAll("\u00A7f", "");
			if (availableKits.containsKey(name) && !cooldownplayers.contains(p)) try {
				byte[][] inventorySave = Serialization.serializeInventory(p.getInventory());
				Serialization.deserializeInventory(p, availableKits.get(name), true);
				cooldownplayers.add(p);
				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							if (isRunning) return;
							Serialization.deserializeInventory(p, inventorySave, true); // Revert back Inventory
							cooldownplayers.remove(p);
							p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
					}
				}.runTaskLater(Cores.instance(), 20L * 6L);
				p.sendMessage(Component.text("\u00A7b\u00bb \u00A77You selected kit " + name));
				if (kits.containsKey(p.getUniqueId())) kits.remove(p.getUniqueId());
				kits.put(p.getUniqueId(), name);
				p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_VILLAGER_WORK_FLETCHER, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
			} catch (IllegalStateException | IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}
			if (name.toLowerCase().contains("tickrate ")) {
				Integer tickrate = Integer.parseInt(name.toLowerCase().split("tickrate ")[1]);
				if (tickrates.containsKey(p.getUniqueId())) tickrates.remove(p.getUniqueId());
				tickrates.put(p.getUniqueId(), tickrate);
				p.sendMessage(Component.text("\u00A7b\u00bb \u00A77You selected tickrate " + tickrate));
				p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
			}
		}
	}

	/**
	 * Runs on player disconnect
	 * @param p Player to disconnect
	 */
	public static void onQuit(Player p) {
		if (kits.containsKey(p.getUniqueId())) kits.remove(p.getUniqueId());
		if (tickrates.containsKey(p.getUniqueId())) tickrates.remove(p.getUniqueId());
		if (readyplayers.contains(p)) readyplayers.remove(p);
		if (cooldownplayers.contains(p)) cooldownplayers.remove(p);
		
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
			checkTickrate();
			Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.DO_TILE_DROPS, false));
			Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.KEEP_INVENTORY, true));
			Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false));
			startGame();
		}
	}


	private static void checkTickrate() {
		int mostVotes = -1;
		List<Integer> votedTickrates = new ArrayList<>();
		for (Entry<UUID, Integer> entry : tickrates.entrySet()) {
			int votes = Collections.frequency(tickrates.values(), entry.getValue());
			if (votes > mostVotes) {
				mostVotes = votes;
				votedTickrates.clear();
				votedTickrates.add(entry.getValue());
			} else if (votes == mostVotes) {
				votedTickrates.add(entry.getValue());
			}
		}
		int finalKitIndex = votedTickrates.size() == 0 ? 4 : new Random().nextInt(votedTickrates.size());
		tickrate = votedTickrates.size() == 0 ? 4 : votedTickrates.get(finalKitIndex);
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Tickrate \uu00A7a" + tickrate + "\u00A77 was selected"));
	}
	
	/**
	 * Whenever a player dies or quits the server
	 * @param player The Player that died
	 */
	public static void onPlayerOut(Player player) {
		if (team1.contains(player))
			team1.remove(player);
		if (team2.contains(player))
			team2.remove(player);
		alivePlayers.remove(player);
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
				killer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
			}
		}
		Player winner = null;
		ArrayList<Player> winners = null;
		if (team1.size() == 0) {
			winner = team2.get(0);
			winners = team2;
		} else if (team2.size() == 0) {
			winner = team1.get(0);
			winners = team1;			
		} if (winner != null) {
			try {
				Cores.updateTickrate(1.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			winners.forEach(c -> {
				PlayerStats.addWin(c);
			});
			for (Player player2 : Bukkit.getOnlinePlayers()) {
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
			}
			if (winner != null) {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + winner.getName() + "\u00A77's Team won the game!"));
				winner.showTitle(Title.title(Component.text("\u00A7cYour team won!"), Component.empty()));
				PlayerStats.addWin(winner);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Cores.updateTickrate(20.0f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(Cores.instance(), 16L);
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(Cores.instance(), 6L+16L);
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
						Cores.updateTickrate(tickrate);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
						isRunning = true;
						// Split the teams
						ArrayList<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
						Collections.shuffle(players);
						boolean shouldGoToTeam1 = true;
						for (Player player : players) {
							if (shouldGoToTeam1) {
								team1.add(player);
								player.setFallDistance(0.0f);
								player.teleport(spawn1);
							} else {
								team2.add(player);
								player.setFallDistance(0.0f);
								player.teleport(spawn2);
							}
							shouldGoToTeam1 = !shouldGoToTeam1;
						}
						for (Player p : Bukkit.getOnlinePlayers()) {
							dyeTeam(p);
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
							p.getWorld().setDifficulty(Difficulty.HARD);
							p.setExp(0.0f);
							alivePlayers.add(p);
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(Cores.instance(), 10*20L);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Equips a player with dyed equipment
	 * @param p Player to equip
	 */
	public static void dyeTeam(Player p) {
		try {
			Serialization.deserializeInventory(p, kits.containsKey(p.getUniqueId()) ? availableKits.get(kits.get(p.getUniqueId())) : availableKits.values().iterator().next(), team2.contains(p)); // load kit
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs on player death
	 * @param p Player that died
	 */
	public static void onDeath(Player p) {
		p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_SCREAM, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
		PlayerStats.addDeath(p);
		if (p.getKiller() != null)
			PlayerStats.addKill(p.getKiller());
		dyeTeam(p);
		if (team1.contains(p)) {
			p.teleport(spawn1);
			if (l1.getBlock().getType() != Material.OBSIDIAN && r1.getBlock().getType() != Material.OBSIDIAN && isRunning && alivePlayers.contains(p)) {
				team1.remove(p);
				onPlayerOut(p);
			}
		} else if (team2.contains(p)) {
			p.teleport(spawn2);
			if (l2.getBlock().getType() != Material.OBSIDIAN && r2.getBlock().getType() != Material.OBSIDIAN && isRunning && alivePlayers.contains(p)) {
				team2.remove(p);
				onPlayerOut(p);
			}
		}
	}

	public static Location respawn(Player p) {
		if (team1.contains(p))
			return spawn1;
		if (team2.contains(p))
			return spawn2;
		return p.getWorld().getSpawnLocation();
	}

	public static void onCore(Player player, Location location) {
		if (location.equals(l1))
			Bukkit.broadcast(Component.text("Team Blue's left core was destroyed."));
		else if (location.equals(r1))
			Bukkit.broadcast(Component.text("Team Blue's right core was destroyed."));
		else if (location.equals(l2))
			Bukkit.broadcast(Component.text("Team Reds's left core was destroyed."));
		else if (location.equals(r2))
			Bukkit.broadcast(Component.text("Team Reds's right core was destroyed."));
		else
			return;
		for (Player player2 : alivePlayers)
			player2.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.MASTER, 1.0f, 1.0f));
	}

}
