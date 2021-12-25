package de.pfannekuchen.juggernaut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import de.pfannekuchen.juggernaut.stats.PlayerStats;
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

	/** Map that holds all players kit votes */
	public static HashMap<UUID, String> kits = new HashMap<>();
	/** Map that holds all players tickrate votes */
	public static HashMap<UUID, Integer> tickrates = new HashMap<>();
	/** List of ready players */
	public static ArrayList<Player> readyplayers = new ArrayList<>();
	/** List of players on interact cooldown */
	public static ArrayList<Player> cooldownplayers = new ArrayList<>();
	/** List of players that want player drops */
	public static ArrayList<Player> pdrops = new ArrayList<>();
	/** List of players that want block drops */
	public static ArrayList<Player> bdrops = new ArrayList<>();
	/** List of players that vote for shown hp */
	public static ArrayList<Player> hpshow = new ArrayList<>();
	/** List of votes for time limits */
	public static ArrayList<Player> timelimit = new ArrayList<>();
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	public static boolean isRunning;
	/** Juggernaut of the game */
	public static Player juggernaut;
	/** Allow voting */
	public static boolean shouldAllowVoting = true;
	/** Alive players of the game */
	public static List<Player> alivePlayers = new LinkedList<>();
	/** The currently selected kit */
	public static byte[][] serializedSelectedKit;
	public static byte[][] serializedSelectedJkit;
	/** The selected tickrate */
	public static int tickrate = 4;
	/** All available kits */
	public static HashMap<String, byte[][]> availableKits = new HashMap<>();
	public static HashMap<String, byte[][]> availableJKits = new HashMap<>();
	/** The currently selected kit name */
	public static String selectedKitName;
	private static boolean isTimelimitOn;
	
	/**
	 * Whether Interactions such as block breaking, damage and interactions should be cancelled
	 * @param entity Player to interact
	 * @return Whether the event should be cancalled or not
	 */
	public static boolean shouldAllowInteraction(Entity entity) {
		return !entity.isOp() && !Game.isRunning;
	}

	/**
	 * Runs on the Server Start
	 * @throws Exception
	 */
	public static void onStartup() throws Exception {
		/* Read available Kits */
		for (File folder : Juggernaut.instance().getDataFolder().listFiles()) {
			if (!folder.isDirectory()) continue;
			File kit = new File(Juggernaut.instance().getDataFolder(), folder.getName());
			byte[][] items = new byte[4][];
			items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
			items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
			items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
			items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
			availableKits.put(folder.getName(), items);
			byte[][] jitems = new byte[4][];
			jitems[0] = Files.readAllBytes(new File(kit, "inv-jug.dat").toPath());
			jitems[1] = Files.readAllBytes(new File(kit, "extra-jug.dat").toPath());
			jitems[2] = Files.readAllBytes(new File(kit, "armor-jug.dat").toPath());
			jitems[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
			availableJKits.put(folder.getName(), jitems);
		}
		/* Start a Thread that updates every second */
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) if (!isRunning) p.sendActionBar(Component.text("\u00A7cLC to view a kit. RC to vote a kit."));
			}
		}.runTaskTimer(Juggernaut.instance(), 0L, 20L);
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
		Objective o = s.registerNewObjective(p.getName(), "dummy", Component.text("Juggernaut"), RenderType.INTEGER);
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
		o.getScore("    ").setScore(9);
		o.getScore("\u00A7bJuggernaut Wins: \u00A7f" + PlayerStats.getJWins(p)).setScore(10);
		o.getScore("\u00A7bJuggernaut Kills: \u00A7f" + PlayerStats.getJKills(p)).setScore(11);
		o.getScore("     ").setScore(12);
		o.getScore("\u00A7bTeam Wins: \u00A7f" + PlayerStats.getNJWins(p)).setScore(13);
		o.getScore("\u00A7bTeam Kills: \u00A7f" + PlayerStats.getNJKills(p)).setScore(14);
		o.getScore("      ").setScore(15);
		o.getScore("\u00A7bYour Stats").setScore(16);
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
		// prepare the gamerule items
		ItemStack hp = new ItemStack(Material.REDSTONE_BLOCK);
		hp.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aShow Health of other players: off"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will decide whether the health of other people should be"),
				Component.text("\u00A75shown during the battle."),
				Component.text().build()
			));
		});
		p.getInventory().setItem(2, hp);

		ItemStack drops = new ItemStack(Material.STICK);
		drops.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aDrop Items from Players: off"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will decide whether players should drop their items on death"),
				Component.text().build()
			));
		});
		p.getInventory().setItem(3, drops);
		
		ItemStack bdrops = new ItemStack(Material.GRASS_BLOCK);
		bdrops.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aDrop Items from Blocks: off"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will decide whether blocks should drop their items"),
				Component.text().build()
			));
		});
		p.getInventory().setItem(4, bdrops);
		p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
		
		ItemStack limit = new ItemStack(Material.COMPASS);
		limit.editMeta(meta -> {
			meta.displayName(Component.text("\u00A7aTime Limit: off"));
			meta.lore(Arrays.asList(
				Component.text().build(),
				Component.text("\u00A75This item will decide whether a worldborder will close after 45 minutes"),
				Component.text().build()
			));
		});
		p.getInventory().setItem(5, limit);
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
			String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName());
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
				case REDSTONE_BLOCK:
					boolean isOn = !name.contains(" off");
					if (isOn) {
						// turn off
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" on", " off"))));
						if (hpshow.contains(p)) hpshow.remove(p);
					} else {
						// turn on
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" off", " on"))));
						if (!hpshow.contains(p)) hpshow.add(p);
					}
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BASALT_BREAK, Source.MASTER, 1.0f, 2.0f));
					break;
				case STICK:
					isOn = !name.contains(" off");
					if (isOn) {
						// turn off
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" on", " off"))));
						if (pdrops.contains(p)) pdrops.remove(p);
					} else {
						// turn on
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" off", " on"))));
						if (!pdrops.contains(p)) pdrops.add(p);
					}
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BASALT_BREAK, Source.MASTER, 1.0f, 2.0f));
					break;
				case GRASS_BLOCK:
					isOn = !name.contains(" off");
					if (isOn) {
						// turn off
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" on", " off"))));
						if (bdrops.contains(p)) bdrops.remove(p);
					} else {
						// turn on
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" off", " on"))));
						if (!bdrops.contains(p)) bdrops.add(p);
					}
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BASALT_BREAK, Source.MASTER, 1.0f, 2.0f));
					break;
				case COMPASS:
					isOn = !name.contains(" off");
					if (isOn) {
						// turn off
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" on", " off"))));
						if (timelimit.contains(p)) timelimit.remove(p);
					} else {
						// turn on
						item.editMeta(m -> m.displayName(Component.text("\u00A7a" + name.replaceFirst(" off", " on"))));
						if (!timelimit.contains(p)) timelimit.add(p);
					}
					p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BASALT_BREAK, Source.MASTER, 1.0f, 2.0f));
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
				Serialization.deserializeInventory(p, availableKits.get(name));
				cooldownplayers.add(p);
				new BukkitRunnable() {

					@Override
					public void run() {
						try {
							if (isRunning) return;
							Serialization.deserializeInventory(p, inventorySave); // Revert back Inventory
							cooldownplayers.remove(p);
							p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
					}
				}.runTaskLater(Juggernaut.instance(), 20L * 6L);
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
		if (bdrops.contains(p)) bdrops.remove(p);
		if (pdrops.contains(p)) pdrops.remove(p);
		if (hpshow.contains(p)) hpshow.remove(p);
		if (timelimit.contains(p)) timelimit.remove(p);
		
		if (startingTask != null) {
			int playersLeft = Bukkit.getOnlinePlayers().size() - 1;
			if (playersLeft < 3) {
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
		if (readyplayers.size() >= Bukkit.getOnlinePlayers().size() && Bukkit.getOnlinePlayers().size() >= 3) {
			/* Find most voted kit */
			checkKit();
			checkTickrate();
			checkMoreStuff();
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
	
	private static void checkMoreStuff() {
		boolean isHpOn = hpshow.size() >= (Bukkit.getOnlinePlayers().size()/2.0);
		isTimelimitOn = timelimit.size() >= (Bukkit.getOnlinePlayers().size()/2.0);
		boolean isPlayerDropsOn = pdrops.size() >= (Bukkit.getOnlinePlayers().size()/2.0);
		boolean isBlockDropsOn = bdrops.size() >= (Bukkit.getOnlinePlayers().size()/2.0);
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Health will \uu00A7a" + (isHpOn ? "" : "not ") + "\u00A77be shown"));
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The Game will \uu00A7a" + (isTimelimitOn ? "" : "not ") + "\u00A77end after 45 minutes"));
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Players will \uu00A7a" + (isPlayerDropsOn ? "" : "not ") + "\u00A77drop items after death"));
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Blocks will \uu00A7a" + (isBlockDropsOn ? "" : "not ") + "\u00A77drop"));
		// Health Scoreboard
		if (isHpOn) for (Player p : Bukkit.getOnlinePlayers()) {
			Scoreboard s = p.getScoreboard();
			Objective o = s.registerNewObjective("hp", "health", Component.text("HP"), RenderType.HEARTS);
			o.setDisplaySlot(DisplaySlot.BELOW_NAME);
			for (Player player : alivePlayers) {
				o.getScore(player.getName()).setScore((int) player.getHealth());
			}
			o = s.registerNewObjective("hp2", "health", Component.text("HP"), RenderType.HEARTS);
			o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			for (Player player : alivePlayers) {
				o.getScore(player.getName()).setScore((int) player.getHealth());
			}
		}
		isHpOn = false; // Avoid above piece to be ran twice
		// Block Drops
		if (isBlockDropsOn) Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.DO_TILE_DROPS, true));
		else Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.DO_TILE_DROPS, false));
		if (isPlayerDropsOn) Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.KEEP_INVENTORY, false));
		else Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.KEEP_INVENTORY, true));
	}
	
	private static void checkKit() throws IOException {
		int mostVotes = -1;
		List<String> votedKits = new ArrayList<>();
		for (Map.Entry<UUID, String> entry : kits.entrySet()) {
			int votes = Collections.frequency(kits.values(), entry.getValue());
			if (votes > mostVotes) {
				mostVotes = votes;
				votedKits.clear();
				votedKits.add(entry.getValue());
			} else if (votes == mostVotes) {
				votedKits.add(entry.getValue());
			}
		}
		File kit;
		if (votedKits.size() == 0) {
			ArrayList<String> kits = new ArrayList<String>(availableKits.keySet());
			Collections.shuffle(kits);
			kit = new File(Juggernaut.instance().getDataFolder(), kits.iterator().next());
		} else {
			kit = new File(Juggernaut.instance().getDataFolder(), votedKits.get(new Random().nextInt(votedKits.size())));
		}
		byte[][] items = new byte[4][];
		items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
		items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
		items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
		items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
		serializedSelectedKit = items;
		byte[][] items2 = new byte[4][];
		items2[0] = Files.readAllBytes(new File(kit, "inv-jug.dat").toPath());
		items2[1] = Files.readAllBytes(new File(kit, "extra-jug.dat").toPath());
		items2[2] = Files.readAllBytes(new File(kit, "armor-jug.dat").toPath());
		items2[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
		serializedSelectedJkit = items2;
		selectedKitName = kit.getName();
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77Kit \uu00A7a" + kit.getName() + "\u00A77 was selected"));
	}

	/**
	 * Whenever a player dies or quits the server
	 * @param player The Player that died
	 */
	public static void onPlayerOut(Player player) {
		alivePlayers.remove(player);
		player.setGameMode(GameMode.SPECTATOR);
		PlayerStats.addDeath(player);
		Player killer = player.getKiller();
		if (killer == null)
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
		else {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 was slain by \u00A7a" + killer.getName()));
			killer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
			PlayerStats.addKill(killer);
			if (killer == juggernaut)
				PlayerStats.addJKill(killer);
			else
				PlayerStats.addNJKill(killer);
		}
		boolean isJuggerAlive = juggernaut == null ? false : alivePlayers.contains(juggernaut);
		if (!isJuggerAlive || alivePlayers.size() == 1) {
			try {
				Juggernaut.updateTickrate(1.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Player player2 : Bukkit.getOnlinePlayers()) {
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
				player2.playSound(Sound.sound(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, Source.BLOCK, 1f, (float) (Math.random() * 0.5f + 1f)), Sound.Emitter.self());
			}
			if (!isJuggerAlive) {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7aThe Juggernaut\u00A77 has been defeated!"));
				if (juggernaut != null)
					PlayerStats.addLoss(juggernaut);
				for (Player p : alivePlayers) {
					if (!p.equals(juggernaut)) {
						p.showTitle(Title.title(Component.text("\u00A7cYou won!"), Component.empty()));
						PlayerStats.addWin(p);
						PlayerStats.addNJWin(p);
					}
				}
			} else if (alivePlayers.size() == 1) {
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7aThe Juggernaut\u00A77 won the game!"));
				juggernaut.showTitle(Title.title(Component.text("\u00A7cYou won!"), Component.empty()));
				PlayerStats.addWin(juggernaut);
				PlayerStats.addJWin(juggernaut);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Juggernaut.updateTickrate(20.0f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(Juggernaut.instance(), 16L);
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(Juggernaut.instance(), 6L+16L);
		}
	}

	/**
	 * Whenever a kit gets selected, a countdown of 10 seconds will start, after which the players will be spread across the map.
	 */
	public static boolean startGame() {
		if (Bukkit.getOnlinePlayers().size() >= 3 /* Check if the game is startable */)  {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will start in 10 seconds."));
			for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.BLOCK, .6f, 1.2f), Sound.Emitter.self());
			startingTask = new BukkitRunnable() {
				@Override public void run() {
					/* Start the game */
					try {
						Juggernaut.updateTickrate(tickrate);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						Random rng = new Random();
						for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
						isRunning = true;
						int index = rng.nextInt(Bukkit.getOnlinePlayers().size());
						int ci = 0;
						for (Player p : Bukkit.getOnlinePlayers()) {
							// load kit
							if (ci == index)
								Serialization.deserializeInventory(juggernaut = p, serializedSelectedJkit);
							else 
								Serialization.deserializeInventory(p, serializedSelectedKit);
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
							p.getWorld().setDifficulty(Difficulty.EASY);
							p.setExp(0.0f);
							alivePlayers.add(p);
							/* Teleport the player */
							while (true) {
								double size = Math.min(200, p.getWorld().getWorldBorder().getSize());
								int x = (int) ((rng.nextInt((int) size)-(size/2.0f)) + p.getWorld().getWorldBorder().getCenter().getBlockX());
								int z = (int) ((rng.nextInt((int) size)-(size/2.0f)) + p.getWorld().getWorldBorder().getCenter().getBlockZ());
//								int x = rng.nextInt(301) - 150;
//								int z = rng.nextInt(301) - 150;
								Block b = p.getWorld().getHighestBlockAt(x, z);
								if (b != null && b.getType() != Material.WATER && b.getType() != Material.AIR && b.getY() > 1) {
									p.setFallDistance(0.0f);
									p.teleport(new Location(p.getWorld(), x, b.getY() + 1, z));
									break;
								}
							}
							ci++;
						}
						if (isTimelimitOn) {
							new BukkitRunnable() {
								@Override
								public void run() {
									for (Player p : alivePlayers) {
										p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 300, 5));
									}
								}
							}.runTaskTimer(Juggernaut.instance(), (45*60*20)/tickrate, 2L);
						}
						int health = Bukkit.getOnlinePlayers().size()*25;
						juggernaut.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health*2);
						juggernaut.setHealth(health*2);
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The juggernaut is \u00A7a" + juggernaut.getName() + "\u00A77. The Juggernaut has \u00A7a" + health + "\u00A77 hearts."));
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(Juggernaut.instance(), 10*20L);
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

}
