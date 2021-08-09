package work.mgnet.paperextensions;

import java.util.List;

import org.bukkit.plugin.Plugin;

public class Configuration {
	
	public static List<String> blockedCMDs;
	public static List<String> forceblockedCMDs;
	public static boolean blockDD;
	public static boolean restrictInteract;
	public static boolean restrictBreakAndPlace;
	public static boolean restrictInventory;
	public static boolean restrictCrafting;
	public static boolean restrictDrop;
	public static boolean restrictOffhand;
	public static boolean hideJoinAndQuit;
	public static boolean editTab;
	
	public static void loadConfiguration(Plugin p) {
		blockedCMDs = p.getConfig().getStringList("commands");
		forceblockedCMDs = p.getConfig().getStringList("forceblock");
		blockDD = p.getConfig().getBoolean("blockdd");
		restrictBreakAndPlace = p.getConfig().getBoolean("restrictBreakAndPlace");
		restrictInteract = p.getConfig().getBoolean("restrictInteract");
		restrictInventory = p.getConfig().getBoolean("restrictInventory");
		restrictDrop = p.getConfig().getBoolean("restrictDrop");
		restrictCrafting = p.getConfig().getBoolean("restrictCrafting");
		restrictOffhand = p.getConfig().getBoolean("restrictOffhand");
		hideJoinAndQuit = p.getConfig().getBoolean("hideJoinAndQuit");
		editTab = p.getConfig().getBoolean("editTab");
	}
	
}
