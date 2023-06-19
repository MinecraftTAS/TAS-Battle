package com.minecrafttas.tasbattle.bedwars.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

/**
 * Resource spawner
 * @author Pancake
 */
public class ResourceSpawner implements Listener {

	// TODO: no stack, no more than 4
	
	@RequiredArgsConstructor
	public class Spawner {
		
		@NonNull private Location loc;
		@NonNull private Integer[] tiers;
		@NonNull private ItemStack item;
		
		private int currentTier = 0;
		private int lastSpawnAt = -1;
		
		/**
		 * Tick spawner
		 * @param tick Current tick
		 */
		public void tick(int tick) {
			if (this.lastSpawnAt + this.tiers[this.currentTier] <= tick) {
				this.lastSpawnAt = tick;
				this.loc.getWorld().spawnEntity(this.loc.clone().add(.5, 0, .5), EntityType.DROPPED_ITEM, SpawnReason.CUSTOM, e -> {
					((Item) e).setItemStack(this.item.clone());
					e.setVelocity(new Vector(0, 0, 0));
					e.customName(Component.text(Math.random() + ""));
					e.setCustomNameVisible(false);
				});		
			}
		}
		
	}
	
	private World world;
	private List<Runnable> armorStandUpdates;
	private int tick;
	
	private Spawner[][] teamSpawners;
	private Spawner[] diamondSpawners;
	private Spawner[] emeraldSpawners;
	
	/**
	 * Initialize resource spawner
	 * @param plugin Plugin
	 * @param world World
	 */
	public ResourceSpawner(TASBattleGameserver plugin, World world) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.world = world;
		this.armorStandUpdates = new ArrayList<>();
		
		// load config
		var config = new YamlConfiguration();
		try {
			config.load(new File(this.world.getWorldFolder(), "locations.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load resource spawners");
			e.printStackTrace();
		}
		
		// load timings
		var ironTicks = config.getIntegerList("ironTiers").toArray(Integer[]::new);
		var goldTicks = config.getIntegerList("goldTiers").toArray(Integer[]::new);
		var diamondTicks = config.getIntegerList("diamondTiers").toArray(Integer[]::new);
		var emeraldTicks = config.getIntegerList("emeraldTiers").toArray(Integer[]::new);
		var baseEmeraldTicks = config.getIntegerList("baseEmeraldTiers").toArray(Integer[]::new);
		
		// create items
		var ironItem = new ItemStack(Material.IRON_INGOT);
		var goldItem = new ItemStack(Material.GOLD_INGOT);
		var diamondItem = new ItemStack(Material.DIAMOND);
		var emeraldItem = new ItemStack(Material.EMERALD);
		
		// create team spawners
		var teamLocations = config.getStringList("teamSpawners").stream().map(s -> s.split(" ")).map(s -> new Location(this.world, Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]))).collect(Collectors.toList());
		this.teamSpawners = new Spawner[teamLocations.size()][3];
		for (int team = 0; team < this.teamSpawners.length; team++) {
			var loc = teamLocations.get(team);
			this.teamSpawners[team] = new Spawner[] {
				new Spawner(loc, ironTicks, ironItem),
				new Spawner(loc, goldTicks, goldItem),
				new Spawner(loc, baseEmeraldTicks, emeraldItem),
			};
		}
		
		// create special spawners
		this.diamondSpawners = config.getStringList("diamondSpawners").stream().map(s -> s.split(" ")).map(s -> new Location(this.world, Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]))).map(loc -> new Spawner(loc, diamondTicks, diamondItem)).collect(Collectors.toList()).toArray(Spawner[]::new);
		this.emeraldSpawners = config.getStringList("emeraldSpawners").stream().map(s -> s.split(" ")).map(s -> new Location(this.world, Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]))).map(loc -> new Spawner(loc, emeraldTicks, emeraldItem)).collect(Collectors.toList()).toArray(Spawner[]::new);
		
		// create emerald spawner armor stands
		for (var spawner : this.emeraldSpawners) {
			var loc = spawner.loc.clone();
			this.createArmorStand(loc.add(0, .7, 0), "§2Emerald", null, null);
			this.createArmorStand(loc.add(0, .3, 0), null, e -> e.customName(Component.text("§eTier §c" + spawner.currentTier)), null);
			this.createArmorStand(loc.add(0, -.6, 0), null, e -> {
				e.customName(Component.text("§eSpawns in §c" + ((int) ((spawner.lastSpawnAt + spawner.tiers[spawner.currentTier] - this.tick) / 20)) + " §eseconds"));
				e.setRotation(e.getLocation().getYaw() + 1, e.getLocation().getPitch());
			}, Material.EMERALD_BLOCK);
		}
		
		// create diamond spawner armor stands
		for (var spawner : this.diamondSpawners) {
			var loc = spawner.loc.clone();
			this.createArmorStand(loc.add(0, .7, 0), "§bDiamond", null, null);
			this.createArmorStand(loc.add(0, .3, 0), null, e -> e.customName(Component.text("§eTier §c" + spawner.currentTier)), null);
			this.createArmorStand(loc.add(0, -.6, 0), null, e -> {
				e.customName(Component.text("§eSpawns in §c" + ((int) ((spawner.lastSpawnAt + spawner.tiers[spawner.currentTier] - this.tick) / 20)) + " §eseconds"));
				e.setRotation(e.getLocation().getYaw() + 1, e.getLocation().getPitch());
			}, Material.DIAMOND_BLOCK);
		}
	}
	
	/**
	 * Tick resource spawner
	 */
	@EventHandler
	public void onTick(ServerTickStartEvent e) {
		this.tick++;
		
		for (Spawner[] spawners : this.teamSpawners)
			for (Spawner spawner : spawners)
				spawner.tick(this.tick);
		
		for (Spawner spawner : this.diamondSpawners)
			spawner.tick(this.tick);
		
		for (Spawner spawner : this.emeraldSpawners)
			spawner.tick(this.tick);
	
		// update armor stands
		for (var update : this.armorStandUpdates)
			update.run();
	}
	
	/**
	 * Create (automatically updating) custom armor stand
	 * @param loc Location
	 * @param text Custom name
	 * @param textUpdate Custom name updater
	 * @param head Head item
	 */
	private void createArmorStand(Location loc, String text, Consumer<ArmorStand> textUpdate, Material head) {
		// create armor stand and set basic properties
		var stand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(.5, 0, .5), EntityType.ARMOR_STAND);
		stand.setInvisible(true);
		stand.setInvulnerable(true);
		stand.setGravity(false);
		stand.setAI(false);
		stand.setCustomNameVisible(true);
		
		// update head item
		if (head != null)
			stand.getEquipment().setHelmet(new ItemStack(head), true);
		
		// update text
		if (text != null)
			stand.customName(Component.text(text));
		
		// update text updater
		if (textUpdate != null) {
			textUpdate.accept(stand);
			this.armorStandUpdates.add(() -> textUpdate.accept(stand));
		}
	}
	
}
