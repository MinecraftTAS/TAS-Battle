package com.minecrafttas.tasbattle.ffa;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle.GameMode;
import com.minecrafttas.tasbattle.ffa.managers.KitManager;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

/**
 * FFA plugin
 * @author Pancake
 */
public class FFA implements GameMode {
	
	@Override
	public void startGameMode(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
	}

	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList(
			new KitManager(),
			new ScenarioManager()
		);
	}
	
}
