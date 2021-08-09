package work.mgnet.duel;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;

public class DuelListener implements Listener {

	@EventHandler
	public void onDie(PlayerDeathEvent e) {
		if (Tournament.CURRENTGAME == Games.DUEL) {
			if (e.getEntity().getName().equalsIgnoreCase(DuelRun.pvp1)) {
				Bukkit.broadcast(Component.text(Prefix.DUEL + DuelRun.pvp2 + " has won the battle!"));
				StatisticManager.addPoints(DuelRun.pvp2, 1);
			} else if (e.getEntity().getName().equalsIgnoreCase(DuelRun.pvp2)) {
				Bukkit.broadcast(Component.text(Prefix.DUEL + DuelRun.pvp1 + " has won the battle!"));
				StatisticManager.addPoints(DuelRun.pvp1, 1);
			} else {
				return;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					Bukkit.getPlayer(DuelRun.pvp2).teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					Bukkit.getPlayer(DuelRun.pvp1).teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					Bukkit.getPlayer(DuelRun.pvp1).getInventory().clear();
					Bukkit.getPlayer(DuelRun.pvp2).getInventory().clear();
					Bukkit.getPlayer(DuelRun.pvp1).setHealth(20);
					Bukkit.getPlayer(DuelRun.pvp2).setHealth(20);
					Bukkit.getPlayer(DuelRun.pvp1).setGameMode(GameMode.ADVENTURE);
					Bukkit.getPlayer(DuelRun.pvp1).setGameMode(GameMode.ADVENTURE);
					DuelRun.setAttackSpeed(Bukkit.getPlayer(DuelRun.pvp1), 4);
					DuelRun.setAttackSpeed(Bukkit.getPlayer(DuelRun.pvp2), 4);
					DuelRun.pvp1 = "";
					DuelRun.pvp2 = "";
				}
			}, 5L);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					DuelRun.startFight();
				}
			},  80L);
		}
		
	}
	
}
