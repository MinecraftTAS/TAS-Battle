package com.minecrafttas.tasbattle.lobby;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

/**
 * Abstract lobby manager
 */
public abstract class LobbyManager implements Listener {
	
	@Getter(value = AccessLevel.PROTECTED) @Setter(value = AccessLevel.PACKAGE)
	private boolean active;
	
	/**
	 * Initialize lobby manager
	 * @param plugin Plugin
	 */
	public LobbyManager(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.active = true;
	}
	
	/**
	 * Interact with item in inventory
	 * @param p Player
	 */
	public abstract void interact(Player p);
	
	/**
	 * Material of item in hand
	 * @return Material of item
	 */
	protected abstract Material getItem();

	/**
	 * Lore of item in hand
	 * @return Lore of item
	 */
	protected abstract List<Component> getItemLore();

	/**
	 * Display name of item in hand
	 * @return Name of item
	 */
	protected abstract String getName();
	
}
