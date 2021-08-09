package work.mgnet.ffa;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.GameStarter;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class FFAListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		e.setDeathSound(Sound.ENTITY_ENDERMAN_DEATH);
		e.setShouldPlayDeathSound(true);
		if (Tournament.CURRENTGAME == Games.FFA) {
			
			if(e.getEntity().getKiller() != null) {
				StatisticManager.addPoints(e.getEntity().getKiller().getName(), 2);
				Bukkit.broadcastMessage(Prefix.FFA + e.getEntity().getName() + " was killed by " + e.getEntity().getKiller().getName());
				e.getEntity().getKiller().setHealth(20);
			}
			int c = 0;
			for (Player lol : Bukkit.getOnlinePlayers()) {
				if (lol.getGameMode() == GameMode.ADVENTURE) c++;
			}
			if (c == 2) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
					
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.teleport(p.getWorld().getSpawnLocation());
							p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
							p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
							UtilListener.enablePVP = false;
							p.resetMaxHealth();
							p.setHealth(20.0);
							p.getInventory().clear();
							p.setGameMode(GameMode.SPECTATOR);
							
							Tournament.CURRENTGAME = Games.NONE;
							
							GameStarter.setAttackSpeed(p, 4);

							p.resetMaxHealth();
							p.setHealth(20.0);

							p.setGameMode(GameMode.ADVENTURE);
						}
					}
				}, 20L);

				
			}
		}
	}
	
}
