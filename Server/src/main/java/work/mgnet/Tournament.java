package work.mgnet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import net.kyori.adventure.text.Component;
import work.mgnet.blockrun.BlockrunListener;
import work.mgnet.blockrun.BlockrunRun;
import work.mgnet.craftmania.CraftmaniaListener;
import work.mgnet.dropper.DropperListener;
import work.mgnet.duel.DuelListener;
import work.mgnet.ffa.FFAListener;
import work.mgnet.iciclerun.IcicleListener;
import work.mgnet.iciclerun.IcicleRun;
import work.mgnet.oldschoolffa.OldSchoolFFAListener;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.paperextensions.listener.ExtensionListener;
import work.mgnet.paperextensions.listener.RestrictionListener;
import work.mgnet.parcour.ParcourListener;
import work.mgnet.replika.ReplicaListener;
import work.mgnet.speedbuild.SpeedbuildListener;
import work.mgnet.speedbuild.SpeedbuildRun;
import work.mgnet.spleef.SpleefListener;
import work.mgnet.spleef.SpleefRun;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.GameStarter;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class Tournament extends JavaPlugin {
	
	public static Games CURRENTGAME = Games.NONE;
	public static Tournament self;
	
	public static boolean isTournament = false;
	
	public int currentIndex = 0;
	public static String[] existingGames = new String[] {"IcicleRun", "Replica", "Blockrun", "Spleef", "Dropper", "FFA", "OldSchoolFFA", "Craftmania", "Parcour", "Replica3D"};
	public static ArrayList<String> games = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		saveResource("config.yml", false);
		UtilListener.saved_tickrate = new File(getDataFolder(), "tickrate.dat");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "tickratechanger:data", new UtilListener());
		Bukkit.getPluginManager().registerEvents(new UtilListener(), this);
		if (!UtilListener.saved_tickrate.exists()) {
			try {
				UtilListener.saved_tickrate.getParentFile().mkdirs();
				UtilListener.saved_tickrate.createNewFile();
				Files.write(UtilListener.saved_tickrate.toPath(), ByteBuffer.allocate(4).putFloat(20).array(), StandardOpenOption.WRITE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				UtilListener.updateTickrate(ByteBuffer.wrap(Files.readAllBytes(UtilListener.saved_tickrate.toPath())).getFloat());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Configuration.loadConfiguration(this);
		Bukkit.getPluginManager().registerEvents(new RestrictionListener(), this);
		Bukkit.getPluginManager().registerEvents(new ExtensionListener(), this);
		Bukkit.getPluginManager().registerEvents(new UtilListener(), this);
		Bukkit.getPluginManager().registerEvents(new IcicleListener(), this);
		Bukkit.getPluginManager().registerEvents(new ReplicaListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpleefListener(), this);
		Bukkit.getPluginManager().registerEvents(new DuelListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockrunListener(), this);
		Bukkit.getPluginManager().registerEvents(new DropperListener(), this);
		Bukkit.getPluginManager().registerEvents(new FFAListener(), this);
		Bukkit.getPluginManager().registerEvents(new OldSchoolFFAListener(), this);
		Bukkit.getPluginManager().registerEvents(new CraftmaniaListener(), this);
		Bukkit.getPluginManager().registerEvents(new ParcourListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpeedbuildListener(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, IcicleRun.fallingBlocksThread(), 5L, 5L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, BlockrunRun.fallingBlocksThread(), 3L, 3L);
		getLogger().info(Prefix.PLUGIN + "enabled!");
		
	}
	
	@Override
	public void onDisable() {
		IcicleRun.clearFallingBlocks();
		SpleefRun.replaceBlocks();
		BlockrunRun.resetBlocks();
		SpeedbuildRun.reset();
		getLogger().info(Prefix.PLUGIN + "disabled!");
	}
	
	@Override
	public void onLoad() {
		self = this;
		for (String game : existingGames) {
			games.add(game);
			games.add(game);
		}
		games.remove("FFA");
		games.remove("OldSchoolFFA");
		getLogger().info(Prefix.PLUGIN + "loaded!");
	}
	
	public void shuffleGames() {
		Collections.shuffle(games);
	}
	
	public void updateScoreboard() {
		Objective obj = null;
		try {obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("tournament");
		obj.unregister();} catch (Exception e) {}
		if (games.size() == 0) return;
		obj = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("tournament", "dummy", Component.text("§6§lTournament"));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		if (currentIndex < 15 && games.size() > 15) {
			for (int i = 0; i < 14; i++) {
				if (i == (currentIndex - 1)) {
					if (!obj.getScore("§e" + games.get(i)).isScoreSet()) {
						obj.getScore("§e" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§e" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
					
				} else if (i < currentIndex) {
					if (!obj.getScore("§c" + games.get(i)).isScoreSet()) {
						obj.getScore("§c" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§c" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
					
				} else if (i == currentIndex) {
					if (!obj.getScore("§a" + games.get(i)).isScoreSet()) {
						obj.getScore("§a" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§a" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
				} else {
					if (!obj.getScore("§7" + games.get(i)).isScoreSet()) {
						obj.getScore("§7" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§7" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
				}
			}
			obj.getScore("...").setScore(15);
		} else {
			for (int i = 14; i < games.size(); i++) {
				if (i == (currentIndex - 1)) {
					if (!obj.getScore("§e" + games.get(i)).isScoreSet()) {
						obj.getScore("§e" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§e" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
					
				} else if (i < currentIndex) {
					if (!obj.getScore("§c" + games.get(i)).isScoreSet()) {
						obj.getScore("§c" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§c" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
					
				} else if (i == currentIndex) {
					if (!obj.getScore("§a" + games.get(i)).isScoreSet()) {
						obj.getScore("§a" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§a" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
				} else {
					if (!obj.getScore("§7" + games.get(i)).isScoreSet()) {
						obj.getScore("§7" + games.get(i)).setScore(i + 1);
					} else {
						obj.getScore("§7" + games.get(i) + new String(new char[i]).replace("\0", " ")).setScore(i + 1);
					}
				}
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(obj.getScoreboard());
		}
		
	}
	
	@SuppressWarnings({ "deprecation" })
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp()) return true;
		if (command.getName().equalsIgnoreCase("ticks")) {
			if (args.length == 1) {
				try {
					UtilListener.updateTickrate(Float.parseFloat(args[0]));
					sender.sendMessage("Tickrate changed to " + args[0]);
					return true;
				} catch (Exception e) {
					sender.sendMessage("Invalid Tickrate");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("default")) {
					try {
						Files.write(UtilListener.saved_tickrate.toPath(), ByteBuffer.allocate(4).putFloat(Float.parseFloat(args[1])).array(), StandardOpenOption.WRITE);
						UtilListener.updateTickrate(Float.parseFloat(args[1]));
						sender.sendMessage("Default Tickrate changed to " + args[1]);
						return true;
					} catch (Exception e) {
						sender.sendMessage("Invalid Tickrate");
					}
				}
			}
		}
		if (command.getName().equalsIgnoreCase("generateTournament")) {
			shuffleGames();
			currentIndex = 0;
			StatisticManager.scores = new HashMap<String, Integer>();
			StatisticManager.updateScoreboard();
			
			Bukkit.broadcastMessage(Prefix.PLUGIN + "A new Tournament has started.");

			
			updateScoreboard();
			
		} else if (command.getName().equalsIgnoreCase("endTournament")) {
			games = new ArrayList<>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
				p.sendTitle("§c" + StatisticManager.getSortedScores().get(0).getKey() + " won!", "Thanks for playing <3");
			}
			Bukkit.broadcastMessage(Prefix.PLUGIN + "The Tournament has ended, thanks for playing!");
			isTournament = false;
			updateScoreboard();
			
		} else if (command.getName().equalsIgnoreCase("nextGame") && CURRENTGAME == Games.NONE) {
			isTournament = true;
			Bukkit.broadcastMessage(Prefix.PLUGIN + "The next Game is " + games.get(currentIndex) + "! Prepare, it'll start soon!");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				
				@Override
				public void run() {
					
					switch (games.get(currentIndex)) {
					case "IcicleRun":
						GameStarter.startIcicleRun();
						break;
					case "Replica":
						GameStarter.startReplica();
						break;
					case "Blockrun":
						GameStarter.startBlockrun();
						break;
					case "Spleef":
						GameStarter.startSpleef();
						break;
					case "Duel":
						GameStarter.startDuel();
						break;
					case "Dropper":
						GameStarter.startDropper();
						break;
					case "FFA":
						GameStarter.startFFA();
						break;
					case "OldSchoolFFA":
						GameStarter.startOldSchoolFFA();
						break;
					case "Craftmania":
						GameStarter.startCraftmania();
						break;
					case "Parcour":
						GameStarter.startParcour();
						break;
					case "Replica3D":
						GameStarter.startReplica3d();
						break;
					default:
						break;
					}
					
					currentIndex++;
					updateScoreboard();
				}
			}, 80L);
		} else if (command.getName().equalsIgnoreCase("start") && CURRENTGAME == Games.NONE) {
			if (args[0].equalsIgnoreCase("random")) {
				switch (existingGames[new Random().nextInt(existingGames.length)]) {
				case "IcicleRun":
					GameStarter.startIcicleRun();
					break;
				case "Replica":
					GameStarter.startReplica();
					break;
				case "Blockrun":
					GameStarter.startBlockrun();
					break;
				case "Spleef":
					GameStarter.startSpleef();
					break;
				case "Duel":
					GameStarter.startDuel();
					break;
				case "Dropper":
					GameStarter.startDropper();
					break;
				case "FFA":
					GameStarter.startFFA();
					break;
				case "OldSchoolFFA":
					GameStarter.startOldSchoolFFA();
					break;
				case "Craftmania":
					GameStarter.startCraftmania();
					break;
				case "Parcour":
					GameStarter.startParcour();
					break;
				case "Replica3D":
					GameStarter.startReplica3d();
					break;
				default:
					break;
				}
			} else {
				switch (args[0].toLowerCase()) {
				case "iciclerun":
					GameStarter.startIcicleRun();
					break;
				case "replica":
					GameStarter.startReplica();
					break;
				case "blockrun":
					GameStarter.startBlockrun();
					break;
				case "spleef":
					GameStarter.startSpleef();
					break;
				case "duel":
					GameStarter.startDuel();
					break;
				case "dropper":
					GameStarter.startDropper();
					break;
				case "ffa":
					GameStarter.startFFA();
					break;
				case "oldschoolffa":
					GameStarter.startOldSchoolFFA();
					break;
				case "craftmania":
					GameStarter.startCraftmania();
					break;
				case "parcour":
					GameStarter.startParcour();
					break;
				case "replica3d":
					GameStarter.startReplica3d();
					break;
				default:
					break;
				}
			}
		}
		return true;
	}
	
}
