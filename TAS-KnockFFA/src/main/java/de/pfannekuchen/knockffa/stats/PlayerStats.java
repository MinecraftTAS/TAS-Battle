package de.pfannekuchen.knockffa.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import net.kyori.adventure.text.Component;

/**
 * Holds all players stats
 * @author Pancake
 *
 */
public class PlayerStats {

	public static final File stats = new File("/home/tasbattle/knockffa/stats.dat");
	
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
	
	public static void save() {
		try {
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(stats));
			s.writeObject(data);
			s.close();
		} catch (Exception e) {
			System.err.println("Save error");
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
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
