package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.managers.DimensionChanger;
import com.minecrafttas.tasbattle.managers.TickrateChanger;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class TASBattleLobby extends JavaPlugin implements Listener {

	@Getter
	private TickrateChanger tickrateChanger;

	@Getter
	private DimensionChanger dimensionChanger;

	@Getter
	private Slime actionSlime;

	/**
	 * Enable tasbattle lobby mod
	 */
	@Override
	public void onEnable() {
		this.tickrateChanger = new TickrateChanger(this);
		this.dimensionChanger = new DimensionChanger(this);

		var world = Bukkit.getWorlds().get(0);
		var loc = new Location(world, 0.5, 100.5, -7.5);

		// get rid of action slimes
		world.getNearbyEntities(loc, 1, 1, 1, null).forEach(e -> e.remove());

		// spawn action slime
		this.actionSlime = (Slime) world.spawnEntity(loc, EntityType.SLIME);
		this.actionSlime.customName(Component.text("Action Slime"));
		this.actionSlime.setAI(false);
		this.actionSlime.setInvulnerable(true);
		this.actionSlime.setSize(5);
		this.actionSlime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false));

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		var raytrace = e.getPlayer().rayTraceEntities(2);
		if (raytrace == null || raytrace.getHitEntity() != this.actionSlime)
			return;

		Bukkit.broadcast(Component.text("i win!"));
	}

	/**
	 * Remove join message on player join
	 * @param e Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setHealth(0.0); // immediately kill player for end override to become active
		e.joinMessage(null);
	}

	/**
	 * Remove quit message on player quit
	 * @param e Player quit event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.quitMessage(null);
	}

}
