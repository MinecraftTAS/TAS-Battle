package com.minecrafttas.tasbattle.bedwars.components;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.bedwars.components.shop.ItemShop;

import net.kyori.adventure.text.Component;

/**
 * Shop villager
 */
public class TeamShop implements Listener {

	private World world;
	private List<Villager> itemShopLocations;
	
	/**
	 * Initialize team shop
	 * @param plugin Plugin
	 * @param world Game world
	 */
	public TeamShop(TASBattle plugin, World world) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.world = world;
		
		// load config
		var config = new YamlConfiguration();
		try {
			config.load(new File(this.world.getWorldFolder(), "shops.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load team shops");
			e.printStackTrace();
		}
		
		this.itemShopLocations = config.getStringList("itemshops").stream().map(s -> s.split(" ")).map(s -> {
			var loc = new Location(this.world, Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]));
			var villager = (Villager) world.spawnEntity(loc.clone().add(.5, 1.0, .5), EntityType.VILLAGER);
			villager.setInvulnerable(true);
			villager.setCustomNameVisible(true);
			villager.setGravity(true);
			villager.setAI(true);
			villager.customName(Component.text("§e§lRIGHT CLICK"));
			
			var stand = (ArmorStand) this.world.spawnEntity(loc.clone().add(.5, 0.3, .5), EntityType.ARMOR_STAND);
			stand.setInvisible(true);
			stand.setInvulnerable(true);
			stand.setGravity(false);
			stand.setAI(false);	
			stand.setCustomNameVisible(true);
			stand.customName(Component.text("§bITEM SHOP"));
			return villager;
		}).collect(Collectors.toList());
	}
	
	/**
	 * Update shop villagers to face player
	 * @param e Event
	 */
	@EventHandler
	public void onTick(ServerTickStartEvent e) {
		for (var v : this.world.getEntitiesByClass(Villager.class)) {
			for (var p : this.world.getNearbyEntitiesByType(Player.class, v.getLocation(), 10.0)) {
				var offsetVec = v.getLocation().subtract(p.getLocation());
				var eyeVec = v.getEyeLocation().setDirection(offsetVec.toVector());
				v.setRotation(180.0f + eyeVec.getYaw(), -eyeVec.getPitch());
			}
		}
	}
	
	/**
	 * Open shop gui when a player interacts with shop
	 * @param e Event
	 */
	@EventHandler
	public void onShopInteract(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Villager))
			return;
		
		if (!this.itemShopLocations.contains(e.getRightClicked()))
			return;

		e.getPlayer().openInventory(new ItemShop().inventory());
		
		e.setCancelled(true);
	}
	
}
