package de.pfannekuchen.bedwars.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.bedwars.Bedwars;
import de.pfannekuchen.bedwars.exceptions.ParseException;
import de.pfannekuchen.bedwars.utils.LocationUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Summons shops and handles their GUIs
 * @author Pancake
 */
public class Shop implements Listener {

	/**
	 * Shopping in the Shop Guis
	 * @param e
	 */
	@EventHandler
	public void onShopInvInteract(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv == null || e.getClickedInventory() == null) return;
		if (PlainTextComponentSerializer.plainText().serialize(e.getView().title()).contains("Item Shop") && e.getClickedInventory().equals(inv)) {
			e.setCancelled(true);
			int slot = e.getSlot();
			if (slot < 8) {
				selectItemPage((Player) e.getWhoClicked(), inv, slot);
			} else if (slot == 8) {
				e.getWhoClicked().closeInventory();
			} else if (e.getCurrentItem() != null) {
				// try to figure out price of Item
				ItemStack i = e.getCurrentItem();
				List<Component> lore = i.lore();
				if (lore != null) {
					for (Component component : lore) {
						String lore1 = PlainTextComponentSerializer.plainText().serialize(component);
						if (lore1.contains("Cost: ")) {
							String[] parts = lore1.split(" ");
							String type = parts[2];
							int cost = Integer.parseInt(parts[1].substring(2));
							Material mat = null;
							if ("Iron".equals(type)) 
								mat = Material.IRON_INGOT;
							else if ("Gold".equals(type)) 
								mat = Material.GOLD_INGOT;
							else if (type.contains("Emerald")) 
								mat = Material.EMERALD;
							if (mat != null) {
								HashMap<Integer, ? extends ItemStack> map = e.getWhoClicked().getInventory().all(mat);
								int count = 0;
								for (Entry<Integer, ? extends ItemStack> component2 : map.entrySet()) {
									count += component2.getValue().getAmount();
								}
								List<ItemStack> stacks = new ArrayList<>(map.values());
								Collections.sort(stacks, new Comparator<ItemStack>() {
									@Override
									public int compare(ItemStack o1, ItemStack o2) {
										return o2.getAmount() - o1.getAmount();
									}
								});
								Collections.reverse(stacks);
								if (count >= cost) {
									for (ItemStack stack : stacks) {
										if (cost >= stack.getAmount()) {
											cost -= stack.getAmount();
											e.getWhoClicked().getInventory().removeItem(stack);
										} else {
											stack.setAmount(stack.getAmount() - cost);
											cost = 0;
										}
										if (cost <= 0) break;
									}
									e.getWhoClicked().getInventory().addItem(buyItem((Player) e.getWhoClicked(), e.getCurrentItem()));
									e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.BLOCK, 1f, 2f));
								} else {
									e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.BLOCK, 1f, 1f));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public ItemStack buyItem(Player p, ItemStack i) {
		ItemStack buying = i.clone();
		buying.lore(null);
		return buying;
	}
	
	/**
	 * Opens a shop gui when a player interacts with a shop
	 * @param e
	 */
	@EventHandler
	public void onShopInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Villager) {
			for (Villager villager : itemShopLocations) {
				if (e.getRightClicked().getLocation().distance(villager.getLocation()) <= 2) {
					openItemShop(e.getPlayer());
				}
			}
			for (Villager villager : upgradeShopLocations) {
				if (e.getRightClicked().getLocation().distance(villager.getLocation()) <= 2) {
					openUpgradeShop(e.getPlayer());
				}
			}
			e.setCancelled(true);
		}
	}
	
	/**
	 * Ticks all villagers and makes then face another player
	 */
	public static void tick() {
		faceAt(itemShopLocations);
		faceAt(upgradeShopLocations);
	}
	
	public static void faceAt(Villager[] v) {
		for (Villager villager : v) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (villager.getLocation().distance(p.getLocation()) < 10) {
					Location vec = villager.getLocation().subtract(p.getLocation());
					Location loc = villager.getEyeLocation().setDirection(vec.toVector());
					villager.setRotation(180.0f + loc.getYaw(), -loc.getPitch());
					break;
				}
			}
		}
	}
	
	/**
	 * Opens an Upgrade Shop for a player
	 * @param player Player to open for
	 */
	private void openUpgradeShop(Player player) {
		
	}

	/**
	 * Opens an Item Shop for a player
	 * @param player Player to open for
	 */
	private void openItemShop(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 54, Component.text("\u00A7bItem Shop"));
		inventory.addItem(getItemStack(Material.NETHER_STAR, "Quick Buy", 1));
		inventory.addItem(getItemStack(Material.TERRACOTTA, "Blocks", 1));
		inventory.addItem(getItemStack(Material.GOLDEN_SWORD, "Weapons", 1));
		inventory.addItem(getItemStack(Material.CHAINMAIL_BOOTS, "Armor", 1));
		inventory.addItem(getItemStack(Material.STONE_PICKAXE, "Tools", 1));
		inventory.addItem(getItemStack(Material.BOW, "Bow", 1));
		inventory.addItem(getItemStack(Material.BREWING_STAND, "Potions", 1));
		inventory.addItem(getItemStack(Material.TNT, "Utilities", 1));
		inventory.addItem(getItemStack(Material.BARRIER, "Close Shop", 1));
		selectItemPage(player, inventory, 0);
		player.openInventory(inventory);
	}
	
	/**
	 * Fills up an Inventory with the shop items
	 * @param inventory
	 * @param i
	 */
	private void selectItemPage(Player p, Inventory inventory, int i) {
		for (int j = 0; j < 9; j++) inventory.setItem(j + 9, getItemStack(i == j ? Material.GREEN_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "", 1));
		for (int j = 0; j < (4*9); j++) inventory.setItem(j+18, null);
		switch (i) {
			case 0:
				inventory.setItem(18+1, getItemStack(Material.WHITE_WOOL, "Wool", 16, "§7Cost: §f4 Iron"));
				inventory.setItem(18+2, getItemStack(Material.STONE_SWORD, "Stone Sword", 1, "§7Cost: §f10 Iron"));
				inventory.setItem(18+3, getItemStack(Material.CHAINMAIL_BOOTS, "Chainmail Armor", 1, "§7Cost: §f40 Iron"));
				inventory.setItem(18+4, getItemStack(Material.DIAMOND_BOOTS, "Diamond Armor", 1, "§7Cost: §26 Emeralds"));
				inventory.setItem(18+5, getItemStack(Material.BOW, "Bow", 1, "§7Cost: §612 Gold"));
				inventory.setItem(18+6, getItemStack(new PotionEffect(PotionEffectType.SPEED, 45*20, 4), Color.fromRGB(0xFFBF00), "Speed Potion V", 1, "§7Cost: §21 Emerald"));
				inventory.setItem(18+7, getItemStack(Material.TNT, "TNT", 1, "§7Cost: §64 Gold"));
				inventory.setItem(18+1+9, getItemStack(Material.OAK_PLANKS, "Wood", 16, "§7Cost: §64 Gold"));
				inventory.setItem(18+2+9, getItemStack(Material.IRON_SWORD, "Iron Sword", 1, "§7Cost: §67 Gold"));
				inventory.setItem(18+3+9, getItemStack(Material.IRON_BOOTS, "Iron Armor", 1, "§7Cost: §612 Gold"));
				inventory.setItem(18+4+9, getItemStack(Material.SHEARS, "Shears", 1, "§7Cost: §f20 Iron"));
				inventory.setItem(18+5+9, getItemStack(Material.ARROW, "Arrow", 8, "§7Cost: §62 Gold"));
				inventory.setItem(18+6+9, getItemStack(new PotionEffect(PotionEffectType.JUMP, 45*20, 4), Color.fromRGB(0xE6E6FA), "Jump Boost Potion V", 1, "§7Cost: §21 Emerald"));
				inventory.setItem(18+7+9, getItemStack(Material.WATER_BUCKET, "Water Bucket", 1, "§7Cost: §63 Gold"));
				return;
			case 1:
				inventory.setItem(18+1, getItemStack(Material.WHITE_WOOL, "Wool", 16, "§7Cost: §f4 Iron"));
				inventory.setItem(18+2, getItemStack(Material.TERRACOTTA, "Terracotta", 16, "§7Cost: §f8 Iron"));
				inventory.setItem(18+3, getItemStack(Material.GLASS, "Glass", 4, "§7Cost: §f12 Iron"));
				inventory.setItem(18+4, getItemStack(Material.END_STONE, "End Stone", 12, "§7Cost: §f24 Iron"));
				inventory.setItem(18+5, getItemStack(Material.LADDER, "Ladder", 8, "§7Cost: §f3 Iron"));
				inventory.setItem(18+6, getItemStack(Material.OAK_PLANKS, "Wood", 16, "§7Cost: §64 Gold"));
				inventory.setItem(18+7, getItemStack(Material.OBSIDIAN, "Obsidian", 4, "§7Cost: §24 Emeralds"));
				return;
			case 2:
				inventory.setItem(18+1, getItemStack(Material.STONE_SWORD, "Stone Sword", 1, "§7Cost: §f10 Iron"));
				inventory.setItem(18+2, getItemStack(Material.IRON_SWORD, "Iron Sword", 1, "§7Cost: §67 Gold"));
				inventory.setItem(18+3, getItemStack(Material.DIAMOND_SWORD, "Diamond Sword", 1, "§7Cost: §24 Emeralds"));
				inventory.setItem(18+4, getItemStack(Material.STICK, "Knockback Stick", 1, "§7Cost: §65 Gold", "§7Knockback II"));
				return;
			case 3:
				inventory.setItem(18+1, getItemStack(Material.CHAINMAIL_BOOTS, "Chainmail Armor", 1, "§7Cost: §f40 Iron"));
				inventory.setItem(18+2, getItemStack(Material.IRON_BOOTS, "Iron Armor", 1, "§7Cost: §612 Gold"));
				inventory.setItem(18+3, getItemStack(Material.DIAMOND_BOOTS, "Diamond Armor", 1, "§7Cost: §26 Emeralds"));
				return;
			case 4:
				inventory.setItem(18+1, getItemStack(Material.SHEARS, "Shears", 1, "§7Cost: §f20 Iron"));
				inventory.setItem(18+2, getItemStack(Material.WOODEN_PICKAXE, "Pickaxe I", 1, "§7Cost: §f10 Iron", "§7Tier: §6I"));
				inventory.setItem(18+3, getItemStack(Material.WOODEN_AXE, "Axe I", 1, "§7Cost: §f10 Iron", "§7Tier: §6I"));
				return;
			case 5:
				inventory.setItem(18+1, getItemStack(Material.ARROW, "Arrow", 8, "§7Cost: §62 Gold"));
				inventory.setItem(18+2, getItemStack(Material.BOW, "Bow", 1, "§7Cost: §612 Gold"));
				inventory.setItem(18+3, getItemStack(Material.BOW, "Bow II", 1, "§7Cost: §624 Gold", "§7Power I"));
				inventory.setItem(18+4, getItemStack(Material.BOW, "Bow III", 1, "§7Cost: §26 Emeralds", "§7Power I", "§7Punch I"));
				return;
			case 6:
				inventory.setItem(18+1, getItemStack(new PotionEffect(PotionEffectType.SPEED, 45*20, 4), Color.fromRGB(0xFFBF00), "Speed V Potion", 1, "§7Cost: §21 Emerald"));
				inventory.setItem(18+2, getItemStack(new PotionEffect(PotionEffectType.JUMP, 45*20, 4), Color.fromRGB(0xE6E6FA), "Jump Boost V Potion", 1, "§7Cost: §21 Emerald"));
				inventory.setItem(18+3, getItemStack(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 0), Color.fromRGB(0x9c9d97), "Invisibility Potion", 1, "§7Cost: §22 Emerald"));
				return;
			case 7:
				inventory.setItem(18+1, getItemStack(Material.GOLDEN_APPLE, "Golden Apple", 1, "§7Cost: §63 Gold"));
				inventory.setItem(18+2, getItemStack(Material.FIRE_CHARGE, "Fireball", 1, "§7Cost: §f40 Iron"));
				inventory.setItem(18+3, getItemStack(Material.TNT, "TNT", 1, "§7Cost: §64 Gold"));
				inventory.setItem(18+4, getItemStack(Material.ENDER_PEARL, "Ender Pearl", 1, "§7Cost: §24 Emerald"));
				inventory.setItem(18+5, getItemStack(Material.WATER_BUCKET, "Water Bucket", 1, "§7Cost: §63 Gold"));
				return;
		}
	}

	/**
	 * Creates a Potion
	 * @param mat Material of Item Stack
	 * @param name Name of Item Stack
	 * @param amount Item Stack count
	 * @return Item Stack
	 */
	private ItemStack getItemStack(PotionEffect potion, Color color, String name, int amount, String... lore) {
		ItemStack item = new ItemStack(Material.POTION, amount);
		PotionMeta meta = ((PotionMeta) item.getItemMeta());
		meta.addCustomEffect(potion, true);
		meta.setColor(color);
		item.setItemMeta(meta);
		item.editMeta(e -> {
			e.displayName(Component.text("\u00A7f" + name.replace('§', '\u00A7')));
			if (lore != null) {
				List<Component> l = new ArrayList<>();
				for (String string : lore) l.add(Component.text("\u00A7f" + string.replace('§', '\u00A7')));
				e.lore(l);
			}
		});
		return item;
	}
	
	/**
	 * Creates an Item Stack with a name, material and count
	 * @param mat Material of Item Stack
	 * @param name Name of Item Stack
	 * @param amount Item Stack count
	 * @return Item Stack
	 */
	private ItemStack getItemStack(Material mat, String name, int amount, String... lore) {
		ItemStack item = new ItemStack(mat, amount);
		item.editMeta(e -> {
			e.displayName(Component.text("\u00A7f" + name.replace('§', '\u00A7')));
			if (lore != null) {
				List<Component> l = new ArrayList<>();
				for (String string : lore) l.add(Component.text("\u00A7f" + string.replace('§', '\u00A7')));
				e.lore(l);
			}
		});
		return item;
	}

	private static Villager[] upgradeShopLocations;
	private static Villager[] itemShopLocations;
	
	/**
	 * Loads a configuration file, or creates one if it is empty
	 * @param configFile Configuration File
	 * @throws InvalidConfigurationException Throws whenever the configuration is incorrect
	 * @throws IOException Throws whenever the file can't be read
	 * @throws FileNotFoundException Throws whenever the file doesn't exist
	 */
	public static final void loadConfig(File configFolder) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(new File(configFolder, "shops.yml"));
		
		try {
			@NotNull List<String> configUpgradeShopLocations = config.getStringList("upgradeshops");
			World w = Bukkit.getWorlds().get(0);
			upgradeShopLocations = new Villager[configUpgradeShopLocations.size()];
			for (int i = 0; i < upgradeShopLocations.length; i++) {
				upgradeShopLocations[i] = summonUpgradeShop(LocationUtils.parseLocation(w, configUpgradeShopLocations.get(i)));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read and summon upgrade shops from configuration", e);
		}
		
		try {
			@NotNull List<String> configItemShopLocations = config.getStringList("itemshops");
			World w = Bukkit.getWorlds().get(0);
			itemShopLocations = new Villager[configItemShopLocations.size()];
			for (int i = 0; i < itemShopLocations.length; i++) {
				itemShopLocations[i] = summonItemShop(LocationUtils.parseLocation(w, configItemShopLocations.get(i)));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read and summon item shops from configuration", e);
		}
		
	}
	
	/**
	 * Spawns an item shop
	 * @param loc Position to spawn at
	 * @return Returns the Villager that has been summoned
	 */
	private static Villager summonItemShop(Location loc) {
		Villager villager = summonVillager(loc);
		villager.customName(Component.text("\u00A7b\u00A7lItem Shop"));
		return villager;
	}
	
	/**
	 * Spawns an upgrade shop
	 * @param loc Position to spawn at
	 * @return Returns the Villager that has been summoned
	 */
	private static Villager summonUpgradeShop(Location loc) {
		Villager villager = summonVillager(loc);
		villager.customName(Component.text("\u00A7b\u00A7lTeam Upgrades"));
		return villager;
	}

	/**
	 * Summons a dumb Villager
	 * @return
	 */
	private static Villager summonVillager(Location loc) {
		Villager villager = (Villager) Bedwars.PRIMARYWORLD.spawnEntity(loc.clone().add(.5, 0, .5), EntityType.VILLAGER);
		villager.setInvulnerable(true);
		villager.setGravity(false);
		villager.setAI(false);
		villager.setCustomNameVisible(true);
		return villager;
	}

	/**
	 * @return the upgradeShopLocations
	 */
	public static synchronized final Villager[] getUpgradeShopLocations() {
		return upgradeShopLocations;
	}

	/**
	 * @return the itemShopLocations
	 */
	public static synchronized final Villager[] getItemShopLocations() {
		return itemShopLocations;
	}
	
}
