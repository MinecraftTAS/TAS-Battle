package com.minecrafttas.tasbattle.ffa;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractGameMode;

import net.kyori.adventure.text.Component;

/**
 * FFA plugin
 * @author Pancake
 */
public class FFA extends AbstractGameMode {
	public FFA(TASBattle plugin) { super(plugin); }

	@Override
	public void startGameMode(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
	}
	
}
