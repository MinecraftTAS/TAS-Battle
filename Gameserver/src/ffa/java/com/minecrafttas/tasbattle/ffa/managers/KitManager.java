package com.minecrafttas.tasbattle.ffa.managers;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

public class KitManager extends LobbyManager {

	public KitManager(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	protected Material getItem() {
		return Material.CHEST;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(""), Component.text("§5The most voted kit will be equipped to"), Component.text("§5all players at the beginning of the game"));
	}

	@Override
	public void interact(Player p) {
		
	}

	@Override
	protected String getName() {
		return "Vote for a kit";
	}

}
