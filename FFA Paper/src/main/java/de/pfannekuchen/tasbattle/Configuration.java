package de.pfannekuchen.tasbattle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;

public class Configuration implements Serializable {
	
	private static final long serialVersionUID = 1056467959579818388L;
	public static File configFile;
	private static Configuration self = new Configuration();
	
	public static Configuration getInstance() {
		return self;
	}
	
	/**
	 * Saves the Configuration to a File
	 * @throws IOException IO Exception
	 */
	public static void save() throws IOException {
		Files.write(configFile.toPath(), SerializationUtils.serialize(self), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}
	
	/**
	 * Loads the Configuration from a File
	 * @throws IOException IO Exception
	 */
	public static void load() throws IOException {
		if (configFile.exists()) self = (Configuration) SerializationUtils.deserialize(Files.readAllBytes(configFile.toPath()));
	}
	
	/* ======== Configuration Starts Here ============= */
	public static class Kit implements Serializable {
		private static final long serialVersionUID = 7823455149842909884L;
		public String name;
		@Override public String toString() { return name; }
	}
	public static class Arena implements Serializable {
		private static final long serialVersionUID = -1429869295712651349L;
		public String name;
		@Override public String toString() { return name; }
	}
	public static enum Combat { OLD, DEFAULT, NEW }
	
	public ArrayList<Arena> arenas = new ArrayList<>();
	public ArrayList<Kit> kits = new ArrayList<>();
	public Combat combatmode = Combat.DEFAULT;
	public boolean shouldAskCommunity = false;
	public Arena currentArena;
	public Kit currentKit;
	
}
