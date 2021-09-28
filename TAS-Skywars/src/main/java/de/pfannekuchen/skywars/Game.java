package de.pfannekuchen.skywars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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
		return !entity.isOp() && !Game.isRunning;
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
			byte[][] items = new byte[3][];
			items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
			items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
			items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
			availableKits.put(folder.getName(), items);
		}
		/* Read available spawns */
		for (String line : Files.readAllLines(new File(Skywars.instance().getDataFolder(), "map.yml").toPath())) spawns.add(new Location(Bukkit.getWorlds().get(0), Double.parseDouble(line.split(" ")[0]), Double.parseDouble(line.split(" ")[1]), Double.parseDouble(line.split(" ")[2])));
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
		p.teleport(p.getWorld().getSpawnLocation());
		if (isRunning) {
			p.sendMessage(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
			p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
			p.setGameMode(GameMode.SPECTATOR);
			return;
		}
		p.sendMessage(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		for (String map : availableKits.keySet()) {
			ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			item.editMeta(c -> {
				c.displayName(Component.text("\u00A7f" + map));
			});
			p.getInventory().addItem(item);
		}
		p.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
		if (Bukkit.getOnlinePlayers().size() >= 2) {
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
		if (isRunning || startingTask != null || item == null) return;
		if (item.getType() == Material.RED_STAINED_GLASS_PANE && item.getItemMeta().hasDisplayName()) {
			String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName()).replaceAll("\u00A7f", "");
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				byte[][] inventorySave = Serialization.serializeInventory(p.getInventory());
				Serialization.deserializeInventory(p, availableKits.get(name));
				new BukkitRunnable() {

					@Override
					public void run() {
						try {
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
		Player killer = player.getKiller();
		if (killer == null)
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 died"));
		else {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + player.getName() + "\u00A77 was slain by \u00A7a" + killer.getName()));
			killer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
		}
		if (alivePlayers.size() <= 1) {
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
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						for (Player player : Bukkit.getOnlinePlayers()) player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.BLOCK, 1f, 1f), Sound.Emitter.self());
						isRunning = true;
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (selectedKits.containsKey(p.getUniqueId())) Serialization.deserializeInventory(p, selectedKits.get(p.getUniqueId()));
							p.setGameMode(GameMode.SURVIVAL);
							p.setLevel(0);
							p.getWorld().setDifficulty(Difficulty.HARD);
							p.setExp(0.0f);
							alivePlayers.add(p);
							/* Teleport the player */
							p.teleport(spawns.poll());
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(Skywars.instance(), 10*20L);
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
	public static final ItemStack[] ITEMS = new ItemStack[] {
		new ItemStack(Material.DIAMOND, 3),
		new ItemStack(Material.GOLDEN_APPLE, 1),
		Utils.enchant(new ItemStack(Material.IRON_CHESTPLATE, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 2),
		Utils.enchant(new ItemStack(Material.IRON_LEGGINGS, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 2),
		Utils.enchant(new ItemStack(Material.IRON_HELMET, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 2),
		Utils.enchant(new ItemStack(Material.IRON_BOOTS, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 2),
		Utils.enchant(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
		Utils.enchant(new ItemStack(Material.DIAMOND_LEGGINGS, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
		Utils.enchant(new ItemStack(Material.DIAMOND_HELMET, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
		Utils.enchant(new ItemStack(Material.DIAMOND_BOOTS, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
		new ItemStack(Material.BRICKS, 64),
		new ItemStack(Material.STONE, 64),
		new ItemStack(Material.OAK_PLANKS, 64),
		new ItemStack(Material.COBBLESTONE, 64),
		new ItemStack(Material.ANDESITE, 64),
		new ItemStack(Material.DIORITE, 64),
		new ItemStack(Material.EXPERIENCE_BOTTLE, new Random().nextInt(16) + 1),
		new ItemStack(Material.IRON_INGOT, 7),
		new ItemStack(Material.WATER_BUCKET, 1),
		new ItemStack(Material.EGG, 16),
		new ItemStack(Material.COBWEB, 16),
		Utils.enchant(new ItemStack(Material.DIAMOND_PICKAXE, 1), Enchantment.DIG_SPEED, 4),
		Utils.enchant(new ItemStack(Material.IRON_AXE, 1), Enchantment.DIG_SPEED, 4),
		new ItemStack(Material.TNT, 64)
	};
	
	/**
	 * Refills a chest
	 * @return List of items for chest
	 */
	public static ItemStack[] refillChest() {
		Random rng = new Random();
		int itemCount = rng.nextInt(5) + 4;
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) list.add(ITEMS[rng.nextInt(ITEMS.length)]);
		if (rng.nextBoolean()) list.add(Utils.enchant(new ItemStack(Material.WOODEN_SWORD, 1), Enchantment.DAMAGE_ALL, 2));
		if (rng.nextBoolean()) list.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		if (rng.nextBoolean()) list.add(new ItemStack(Material.STONE, 64));
		return list.toArray(ItemStack[]::new);
	}

}
