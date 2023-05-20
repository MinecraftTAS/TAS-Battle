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
		// tick game components
		this.spawner.tick();
	}
	
	@Override public void playerJoin(Player player) { }
	@Override public void playerLeave(Player player) {}
	@Override public boolean playerBreak(Player player, Block block) { return false; }
	@Override public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) { return false; }
	@Override public boolean entityDamage(Entity entity, double damage, DamageCause cause) { return true; } // watch out
	@Override public boolean playerDrop(Player player, Item item) { return false; }
	@Override public boolean entityPickup(LivingEntity entity, Item item) { return false; }
	@Override public boolean playerConsume(Player player, ItemStack item) { return false; }
	@Override public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) { return false; }
	@Override public boolean entityExplosion(Entity entity, Location loc) { return false; }
	@Override public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) { return Arrays.asList(); }
	@Override public boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) { return false; }


}
