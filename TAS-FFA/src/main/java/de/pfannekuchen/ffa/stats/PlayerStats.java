package de.pfannekuchen.ffa.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.entity.Player;

/**
 * Holds all players stats
 * @author Pancake
 *
 */
public class PlayerStats {

	public static final File stats = new File("/home/tasbattle/ffa/stats.dat");
	
	private static Data data;
	
	public static int getDeaths(Player p) {
		int deaths = 0;
		if (data.deaths.containsKey(p.getUniqueId())) deaths = data.deaths.get(p.getUniqueId());
		return deaths;
	}
	
	public static int getKills(Player p) {
		int kills = 0;
		if (data.kills.containsKey(p.getUniqueId())) kills = data.kills.get(p.getUniqueId());
		return kills;
	}
	
	public static int getWins(Player p) {
		int wins = 0;
		if (data.wins.containsKey(p.getUniqueId())) wins = data.wins.get(p.getUniqueId());
		return wins;
	}
	
	public static int getLosses(Player p) {
		int losses = 0;
		if (data.losses.containsKey(p.getUniqueId())) losses = data.losses.get(p.getUniqueId());
		return losses;
	}
	
	public static void addDeath(Player p) {
		int c = 1;
		if (data.deaths.containsKey(p.getUniqueId())) {
			c = data.deaths.get(p.getUniqueId())+1;
			data.deaths.remove(p.getUniqueId());
		}
		data.deaths.put(p.getUniqueId(), c);
		save();
	}
	
	public static void addKill(Player p) {
		int c = 1;
		if (data.kills.containsKey(p.getUniqueId())) {
			c = data.kills.get(p.getUniqueId())+1;
			data.kills.remove(p.getUniqueId());
		}
		data.kills.put(p.getUniqueId(), c);
		save();
	}
	
	public static void addLoss(Player p) {
		int c = 1;
		if (data.losses.containsKey(p.getUniqueId())) {
			c = data.losses.get(p.getUniqueId())+1;
			data.losses.remove(p.getUniqueId());
		}
		data.losses.put(p.getUniqueId(), c);
		save();
	}
	
	public static void addWin(Player p) {
		int c = 1;
		if (data.wins.containsKey(p.getUniqueId())) {
			c = data.wins.get(p.getUniqueId())+1;
			data.wins.remove(p.getUniqueId());
		}
		data.wins.put(p.getUniqueId(), c);
		save();
	}
	
	public static void save() {
		try {
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(stats));
			s.writeObject(data);
			s.close();
		} catch (Exception e) {
			System.err.println("Save error");
			e.printStackTrace();
		}
	}
	
	public static void load() throws IOException {
		if (stats.exists()) {
			try {
				ObjectInputStream s = new ObjectInputStream(new FileInputStream(stats));
				data = (Data) s.readObject();
				s.close();
			} catch (Exception e) {
				// no save data
				System.err.println("Load error");
				e.printStackTrace();
				data = new Data();
			}
		} else {
			stats.createNewFile();
			data = new Data();
		}
	}
	
}
