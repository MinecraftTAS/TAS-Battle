package com.minecrafttas.tasbattle.bedwars;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractGameMode;
import com.minecrafttas.tasbattle.bedwars.components.ExplosivePhysics;
import com.minecrafttas.tasbattle.bedwars.components.ItemPhysics;
import com.minecrafttas.tasbattle.bedwars.components.PlacementRules;
import com.minecrafttas.tasbattle.bedwars.components.ResourceSpawner;
import com.minecrafttas.tasbattle.gamemode.GameMode;
import com.minecrafttas.tasbattle.loading.WorldUtils;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

/**
 * Bedwars gamemode
 * @author Pancake
 */
public class Bedwars extends AbstractGameMode implements GameMode {
	
	private World world;
	private ResourceSpawner spawner;
	private ItemPhysics physics;
	private PlacementRules placementRules;
	private ExplosivePhysics explosivePhysics;
	
	/**
	 * Initialize bedwars gamemode
	 * @param plugin Plugin
	 */
	public Bedwars(TASBattle plugin) {
		super(plugin);
		
		// find available worlds
		File serverDir = new File(".");
		File[] availableWorlds = serverDir.listFiles((dir, name) -> name.startsWith("bedwars-"));
		// pick random world
		String worldName = availableWorlds[(int) (Math.random() * availableWorlds.length)].getName();
		this.world = WorldUtils.loadWorld(worldName);
		
		// register mode management
		plugin.getModeManagement().registerGameMode("BEDWARS", this);
		
		// load components
		try {
			this.spawner = new ResourceSpawner(this.world);
			this.physics = new ItemPhysics(this.world);
			this.placementRules = new PlacementRules(plugin);
			this.explosivePhysics = new ExplosivePhysics(plugin, this.placementRules);
		} catch (Exception e) {
			System.err.println("Exception occured while intializing bedwars");
			e.printStackTrace();
		}
	}

	/**
	 * Start bedwars gamemode
	 */
	@Override
	public void startGameMode(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
		this.plugin.getModeManagement().setGameMode("BEDWARS");
		
		// launch game components
		this.spawner.startGame();
		this.physics.startGame(players);
		
		for (Player p : players)
			p.teleport(this.world.getSpawnLocation());
	}
	
	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList();
	}

	/**
	 * Tick bedwars
	 */
	@Override
	@Deprecated
	public void serverTick() {
		this.spawner.tick();
		this.physics.tick();
	}
	
	@Override
	@Deprecated
	public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) {
		this.physics.onDeath(player);
		return Arrays.asList();
	}
	
	@Override
	@Deprecated
	public boolean playerDrop(Player player, Item item) { 
		return this.physics.onDrop(item.getItemStack());
	}
	
	@Override
	@Deprecated
	public boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) {
		if (clickedItem == null)
			return true;
		
		return this.physics.onClick(clickedItem);
	}
	
	
	@Override public double entityDamage(Entity entity, double damage, DamageCause cause) { return cause == DamageCause.FALL ? 0 : damage; } // FIXME: implement proper damage
	@Deprecated(forRemoval = true) @Override public boolean entityPickup(LivingEntity entity, Item item) { return false; }
	@Deprecated(forRemoval = true) @Override public boolean playerConsume(Player player, ItemStack item) { return false; }
	@Deprecated(forRemoval = true) @Override public void playerJoin(Player player) {}
	@Deprecated(forRemoval = true) @Override public void playerLeave(Player player) {}
	@Deprecated(forRemoval = true) @Override public boolean playerBreak(Player player, Block block) { return false; }
	@Deprecated(forRemoval = true) @Override public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) { return false; }
	@Deprecated(forRemoval = true) @Override public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) { return false; }
	@Deprecated(forRemoval = true) @Override public boolean entityExplosion(Entity entity, Location loc, List<Block> blocklist) { return false; }
	
}
