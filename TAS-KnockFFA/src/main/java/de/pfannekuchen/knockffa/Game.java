package de.pfannekuchen.knockffa;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import de.pfannekuchen.knockffa.stats.PlayerStats;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

/**
 * Class that controls the Game
 * @author Pancake
 */
public class Game {

	public static HashMap<Location, Integer> blocks = new HashMap<>();
	
	/**
	 * Runs on player connect
	 * @param p Player to connect
	 */
	public static void onJoin(Player p) {
		p.setFallDistance(0.0f);
		p.getWorld().setTime(6000);
		p.teleport(p.getWorld().getSpawnLocation());
		// prepare scoreboard
		Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = s.registerNewObjective(p.getName(), "dummy", Component.text("FFA"), RenderType.INTEGER);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.getScore("discord.gg/hUcYSFnJsW").setScore(0);
		o.getScore(" ").setScore(1);
		o.getScore("\u00A7aKDR: \u00A7f" + String.format("%.2f", PlayerStats.getKills(p) / ((double) PlayerStats.getDeaths(p)))).setScore(2);
		o.getScore("\u00A7bDeaths: \u00A7f" + PlayerStats.getDeaths(p)).setScore(3);
		o.getScore("\u00A7bKills: \u00A7f" + PlayerStats.getKills(p)).setScore(4);
		o.getScore("  ").setScore(5);
		o.getScore("\u00A7bYour Stats").setScore(6);
		p.setScoreboard(s);
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, Source.BLOCK, .4f, 2f), Sound.Emitter.self());
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	/**
	 * Runs on player disconnect
	 * @param p Player to disconnect
	 */
	public static void onQuit(Player p) {
		Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has left the game."));
	}

	/**
	 * Runs on player death
	 * @param p Player that died
	 */
	public static void onDeath(Player p) {
		p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_SCREAM, Source.BLOCK, .6f, 1f), Sound.Emitter.self());
		PlayerStats.addDeath(p);
		if (p.getKiller() != null) PlayerStats.addKill(p.getKiller());
	}
	
	/**
	 * Runs on player spawn or join
	 * @param p Player that joined
	 */
	public static void onSpawn(Player p) {
		p.getInventory().clear();
		try {
			KnockFFA.updateTickrate(4.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ItemStack stick = new ItemStack(Material.STICK);
		stick.editMeta(e -> {
			e.displayName(Component.text("\u00A7fKnockback Stick"));
			e.setUnbreakable(true);
			e.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("speed", 1000, Operation.ADD_NUMBER));
		});
		stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		p.getInventory().setItem(0, stick);
		ItemStack bow = new ItemStack(Material.BOW);
		bow.editMeta(e -> {
			e.displayName(Component.text("\u00A7fPunch Bow"));
			e.setUnbreakable(true);
		});
		bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		p.getInventory().setItem(1, bow);
		p.getInventory().setItem(7, new ItemStack(Material.ENDER_PEARL, 16));
		p.getInventory().setItemInOffHand(new ItemStack(Material.SANDSTONE, 64));
		p.getInventory().setItem(9, new ItemStack(Material.SANDSTONE, 64));
		p.getInventory().setItem(18, new ItemStack(Material.SANDSTONE, 64));
		p.getInventory().setItem(27, new ItemStack(Material.SANDSTONE, 64));
		p.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
	}
	
	/**
	 * Runs on player block place
	 * @param player Player that placed the block
	 * @param blockLoc Position of the placed block
	 */
	public static void onBlockPlace(Player player, Location blockLoc) {
		if (blockLoc.getY() < 94) {
			blocks.put(blockLoc, 100);
		}
	}

}
