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
import com.minecrafttas.tasbattle.bedwars.components.ItemPhysics;
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
	
	/**
	 * Initialize bedwars gamemode
	 * @param plugin Plugin
	 */
	public Bedwars(TASBattle plugin) {
		super(plugin);
		this.world = this.pickRandomWorld();
		
		// register mode management
		plugin.getModeManagement().registerGameMode("BEDWARS", this);
		
		// load components
		try {
			this.spawner = new ResourceSpawner(this.world);
			this.physics = new ItemPhysics(this.world);
		} catch (Exception e) {
			System.err.println("Exception occured while intializing bedwars");
			e.printStackTrace();
		}
	}

	/**
	 * Pick random world from available worlds
	 * @return Random world
	 */
	private World pickRandomWorld() {
		// find available worlds
		File serverDir = new File(".");
		File[] availableWorlds = serverDir.listFiles((dir, name) -> name.startsWith("bedwars-"));
		
		// pick random world
		String worldName = availableWorlds[(int) (Math.random() * availableWorlds.length)].getName();
		return WorldUtils.loadWorld(worldName);
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
	public void serverTick() {
		this.spawner.tick();
		this.physics.tick();
	}
	
	@Override
	public boolean playerBreak(Player player, Block block) {
		return this.physics.onBlockBreak(block.getLocation());
	}
	
	@Override
	public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) {
		this.physics.onBlockPlace(block);
		return false;
	}
	
	@Override
	public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) {
		this.physics.onDeath(player);
		return Arrays.asList();
	}
	
	@Override
	public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			return this.physics.onRightClickInteract(player, item);
		
		return false;
	}
	
	@Override
	public boolean entityExplosion(Entity entity, Location loc, List<Block> blocklist) {
		this.physics.onTntExplode(entity.getType(), blocklist);
		return false;
	}
	
	@Override
	public boolean playerDrop(Player player, Item item) { 
		return this.physics.onDrop(item.getItemStack());
	}
	
	@Override
	public boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) {
		if (clickedItem == null)
			return true;
		
		return this.physics.onClick(clickedItem);
	}
	
	@Override public void playerJoin(Player player) { }
	@Override public void playerLeave(Player player) {}
	@Override public double entityDamage(Entity entity, double damage, DamageCause cause) { return cause == DamageCause.FALL ? 0 : this.physics.onTntDamage(cause, damage); } // watch out
	@Override public boolean entityPickup(LivingEntity entity, Item item) { return false; }
	@Override public boolean playerConsume(Player player, ItemStack item) { return false; }


}
