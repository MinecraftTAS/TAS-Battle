package de.pfannekuchen.skywars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

	/** All available kits */
	public static HashMap<UUID, byte[][]> selectedKits = new HashMap<>();
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	public static boolean isRunning;
	/** Alive players of the game */
	private static List<Player> alivePlayers = new LinkedList<>();
	/** All available kits */
	public static HashMap<String, byte[][]> availableKits = new HashMap<>();
	/** Possible Spawns and max players */
	public static Queue<Location> spawns = new LinkedList<>();
	
	/**
	 * Whether Interactions such as block breaking, damage and interactions should be cancelled
	 * @param entity Player to interact
	 * @return Whether the event should be cancalled or not
	 */
	public static boolean shouldAllowInteraction(Entity entity) {
		return !Game.isRunning;
	}

	/**
	 * Runs on the Server Start
	 * @throws Exception
	 */
	public static void onStartup() throws Exception {
		/* Read available Kits */
		for (File folder : Skywars.instance().getDataFolder().listFiles()) {
			if (!folder.isDirectory()) continue;
			File kit = new File(Skywars.instance().getDataFolder(), folder.getName());
			byte[][] items = new byte[4][];
			items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
			items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
			items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
			items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
			availableKits.put(folder.getName(), items);
		}
		/* Read available spawns */
		ArrayList<Location> lspawns = new ArrayList<Location>(); 
		for (String line : Files.readAllLines(new File(Skywars.instance().getDataFolder(), "map.yml").toPath())) lspawns.add(new Location(Bukkit.getWorlds().get(0), Double.parseDouble(line.split(" ")[0]), Double.parseDouble(line.split(" ")[1]), Double.parseDouble(line.split(" ")[2])));
		Collections.shuffle(lspawns);
		spawns.addAll(lspawns);
		/* Start a Thread that updates every second */
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) if (!isRunning) p.sendActionBar(Component.text("\u00A7cLC to view a kit. RC to vote a kit."));
			}
		}.runTaskTimer(Skywars.instance(), 0L, 20L);
	}

	/**
	 * Runs on player connect
	 * @param p Player to connect
	 */
	public static void onJoin(Player p) {
		if (Bukkit.getOnlinePlayers().size() > spawns.size()) p.kick(Component.text("The game is already full!"));
		p.setFallDistance(0.0f);
		p.teleport(p.getWorld().getSpawnLocation());
		if (startingTask != null) {
			p.sendMessage(Component.text("\u00A7b\u00bb \u00A7aThe game will start soon!"));
		}
		if (isRunning) {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
			p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
			p.setGameMode(GameMode.SPECTATOR);
			return;
		}
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		int index = 0;
		for (String map : availableKits.keySet()) {
			Material mat = Material.getMaterial(Serialization.getIcon(availableKits.get(map)).replaceAll("\r", "").replaceAll("\n", ""));
			if (mat == null) mat = Material.RED_STAINED_GLASS_PANE;
			ItemStack item = new ItemStack(mat);
			item.editMeta(c -> {
				c.displayName(Component.text("\u00A7f" + map));
				List<Component> itemlist = new LinkedList<>();
				byte[][] items = availableKits.get(map);
				try {
					Serialization.getItemStacks(items).forEach(i -> { 
						if (i != null) itemlist.add(Component.text("\u00A7f" + i.getAmount() + "x" + i.getI18NDisplayName()));
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				c.lore(itemlist);
			});
			p.getInventory().setItem(index, item); 
			index++;
			if (index == 9) index = 27 + 9;
			if (index > 9) index -= 2;
		}
		p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
		if (Bukkit.getOnlinePlayers().size() >= 2 && startingTask == null) {
			startGame();
		}
	}

	/**
	 * Runs on player interaction
	 * @param p Player to interact
	 * @param item Item interacted with
	 * @param action Action used
	 * @throws Exception Happens..
	 */
	public static void onInteract(Player p, ItemStack item, Action action) throws Exception {
		if (isRunning || item == null) return;
		if (item.getItemMeta().hasDisplayName()) {
			String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName()).replaceAll("\u00A7f", "");
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				byte[][] inventorySave = Serialization.serializeInventory(p.getInventory());
				Serialization.deserializeInventory(p, availableKits.get(name));
				new BukkitRunnable() {

					@Override
					public void run() {
						try {
							if (isRunning) return;
							Serialization.deserializeInventory(p, inventorySave); // Revert back Inventory
							p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
					}
				}.runTaskLater(Skywars.instance(), 20L * 6L);
				p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_CHORUS_FLOWER_GROW, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
			} else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				if (selectedKits.containsKey(p.getUniqueId())) selectedKits.remove(p.getUniqueId());
				p.sendMessage(Component.text("\u00A7b\u00bb \u00A77You selected kit " + name));
				selectedKits.put(p.getUniqueId(), availableKits.get(name));
				p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_SCAFFOLDING_BREAK, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
			}
		}
	}

	/**
	 * Runs on player disconnect
	 * @param p Player to disconnect
	 */
	public static void onQuit(Player p) {
		if (selectedKits.containsKey(p.getUniqueId())) selectedKits.remove(p.getUniqueId());
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
	 * Whenever a player dies or quits the server
	 * @param player The Player that died
	 */
	public static void onPlayerOut(Player player) {
		alivePlayers.remove(player);
		player.setGameMode(GameMode.SPECTATOR);
		Player killer = player.getKiller();
		if (killer == null)
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
		else {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 was slain by \u00A7a" + killer.getName()));
			killer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
		}
		if (alivePlayers.size() <= 1) {
			try {
				Skywars.updateTickrate(20.0f);
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
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(Skywars.instance(), 20L * 5L);
		}
	}

	/**
	 * Whenever a kit gets selected, a countdown of 20 seconds will start, after which the players will be spread across the map.
	 */
	public static boolean startGame() {
		if (Bukkit.getOnlinePlayers().size() >= 2 /* Check if the game is startable */)  {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will start in 20 seconds."));
			for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.BLOCK, .6f, 1.2f), Sound.Emitter.self());
			startingTask = new BukkitRunnable() {
				@Override public void run() {
					/* Start the game */
					try {
						Skywars.updateTickrate(4f);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
						isRunning = true;
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (selectedKits.containsKey(p.getUniqueId())) Serialization.deserializeInventory(p, selectedKits.get(p.getUniqueId()));
							else Serialization.deserializeInventory(p, availableKits.values().iterator().next());
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getWorld().setDifficulty(Difficulty.HARD);
							p.setExp(0.0f);
							alivePlayers.add(p);
							/* Teleport the player */
							p.setFallDistance(0.0f);
							p.teleport(spawns.poll());
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(Skywars.instance(), 20*20L);
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

	/**
	 * List of Items to spawn in a chest
	 */
	public static final WeightedRandomBag<ItemStack> ITEMS = new WeightedRandomBag<>() {{
		addEntry(new ItemStack(Material.IRON_HELMET), 5.0);
		addEntry(new ItemStack(Material.IRON_CHESTPLATE), 5.0);
		addEntry(new ItemStack(Material.IRON_LEGGINGS), 5.0);
		addEntry(new ItemStack(Material.IRON_BOOTS), 5.0);
		addEntry(new ItemStack(Material.DIAMOND_HELMET), 5.0);
		addEntry(new ItemStack(Material.DIAMOND_CHESTPLATE), 5.0);
		addEntry(new ItemStack(Material.DIAMOND_LEGGINGS), 5.0);
		addEntry(new ItemStack(Material.DIAMOND_BOOTS), 5.0);
		
		addEntry(new ItemStack(Material.STONE, 64), 20.0);
		addEntry(new ItemStack(Material.OAK_LOG, 64), 20.0);
		
		addEntry(Utils.enchant(new ItemStack(Material.DIAMOND_SWORD), Enchantment.DAMAGE_ALL, 1), 4.0);
		addEntry(new ItemStack(Material.DIAMOND_SWORD), 5.0);
		
		addEntry(Utils.enchant(new ItemStack(Material.BOW), Enchantment.ARROW_DAMAGE, 3), 4.0);
		addEntry(new ItemStack(Material.EGG, 16), 10.0);
		addEntry(new ItemStack(Material.SNOWBALL, 16), 10.0);
		
		addEntry(Utils.enchant(new ItemStack(Material.BOW), Enchantment.ARROW_DAMAGE, 3), 4.0);
		addEntry(new ItemStack(Material.EGG, 16), 10.0);
		addEntry(new ItemStack(Material.SNOWBALL, 16), 10.0);
		
		addEntry(new ItemStack(Material.EXPERIENCE_BOTTLE, 32), 4.0);
		addEntry(new ItemStack(Material.DIAMOND_PICKAXE), 10.0);
		addEntry(new ItemStack(Material.DIAMOND_AXE), 10.0);
		
		addEntry(new ItemStack(Material.WATER_BUCKET), 7.0);
		addEntry(new ItemStack(Material.LAVA_BUCKET), 7.0);
		
		addEntry(new ItemStack(Material.GOLDEN_APPLE), 30.0);
		
		addEntry(new ItemStack(Material.ARROW, 32), 30.0);
	}};
	
	/**
	 * Refills a chest
	 * @return List of items for chest
	 */
	public static ItemStack[] refillChest() {
		Random rng = new Random();
		int itemCount = rng.nextInt(5) + 4;
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) list.add(ITEMS.getRandom());
		if (rng.nextBoolean()) list.add(Utils.enchant(new ItemStack(Material.WOODEN_SWORD, 1), Enchantment.DAMAGE_ALL, 2));
		if (rng.nextBoolean()) list.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		if (rng.nextBoolean()) list.add(new ItemStack(Material.STONE, 64));
		return list.toArray(ItemStack[]::new);
	}

}
