package com.minecrafttas.tasbattle.bedwars;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractGameMode;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

/**
 * Bedwars gamemode
 * @author Pancake
 */
public class Bedwars extends AbstractGameMode {
	@Override public List<LobbyManager> createManagers() { return Arrays.asList(); }
	public Bedwars(TASBattle plugin) { super(plugin); }

	@Override
	public void startGameMode(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
	}


}
