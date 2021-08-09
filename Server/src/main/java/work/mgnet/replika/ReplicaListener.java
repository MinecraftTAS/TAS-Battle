package work.mgnet.replika;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class ReplicaListener implements Listener {

	public static ArrayList<Player> done = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		int x = e.getBlock().getLocation().getBlockX();
		int z = e.getBlock().getLocation().getBlockZ();
		if (z >= 1000 && z <= 1007 && x <= 0 && x >= -8 && e.getBlock().getLocation().getBlockY() == 101) {
			IBlockData state = CraftBlockData.newData(e.getBlock().getType(), "").getState();
			e.setCancelled(true);
			
			if (ReplicaRun.playerBuild.get(e.getPlayer()).containsKey(e.getBlock().getLocation())) ReplicaRun.playerBuild.get(e.getPlayer()).remove(e.getBlock().getLocation());
			ReplicaRun.playerBuild.get(e.getPlayer()).put(e.getBlock().getLocation(), e.getBlock().getType());
			
			int points = 0;
			
			try {
				for (Entry<Location, Material> map : ReplicaRun.demo.entrySet()) {
					if (ReplicaRun.playerBuild.get(e.getPlayer()).get(map.getKey()).equals(map.getValue())) {
						points++;
					}
				}
			} catch (Exception e2) {
				
			}
			
			if (points == 64) {
				
				Bukkit.broadcastMessage((Prefix.REPLICA + "§6" + e.getPlayer().getName() + " replikated in " + ((System.currentTimeMillis() - ReplicaRun.timeStart) / 1000) + " seconds!"));
				
				done.add(e.getPlayer());
				
				if (done.size() == 1) {
					StatisticManager.addPoints(e.getPlayer().getName(), 5);
					Bukkit.broadcastMessage(Prefix.REPLICA + e.getPlayer().getName() + " was first and earned 5 points!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 2) {
					StatisticManager.addPoints(e.getPlayer().getName(), 2);
					Bukkit.broadcastMessage(Prefix.REPLICA + e.getPlayer().getName() + " was second and earned 2 points!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 3) {
					StatisticManager.addPoints(e.getPlayer().getName(), 1);
					Bukkit.broadcastMessage(Prefix.REPLICA + e.getPlayer().getName() + " was third and earned 1 point!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				}
				if (done.size() == Bukkit.getOnlinePlayers().size() || done.size() == 3) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							p.showPlayer(p2);
							p2.showPlayer(p);
							
						}
						Configuration.restrictInventory = true;
						Configuration.restrictInteract = true;
						p.setAllowFlight(false);
						p.getInventory().clear();
						try {
							UtilListener.updateTickrate(20f);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						Tournament.CURRENTGAME = Games.NONE;
						p.teleport(p.getWorld().getSpawnLocation());
						p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
						p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
						p.resetMaxHealth();
						p.setHealth(20.0);
						done.clear();
						p.setGameMode(GameMode.ADVENTURE);
					}
				}
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					((CraftPlayer) e.getPlayer()).getHandle().b.sendPacket(new PacketPlayOutBlockChange(new BlockPosition(x, 100, z),
							state));
				}
			}, 2L);
			}
	}
	
}
