package de.pfannekuchen.tasbattle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

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
		if (!configFile.createNewFile());
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(configFile));
		stream.writeObject(self);
		stream.close();
	}
	
	/**
	 * Loads the Configuration from a File
	 * @throws Exception IO Exception
	 */
	public static void load() throws Exception {
		if (configFile.exists()) {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(configFile));
			self = (Configuration) stream.readObject();
			stream.close();
		}
	}
	
	/* ======== Configuration Starts Here ============= */
	public static class Kit implements Serializable {
		private static final long serialVersionUID = 7823455149842909884L;
		public String name;
		public String[] data;
		@Override public String toString() { return name; }
	}
	public static class Arena implements Serializable {
		private static final long serialVersionUID = -1429869295712651349L;
		public String name;
		@Override public String toString() { return name; }
	}
	public static enum Combat implements Serializable { OLD, DEFAULT, NEW }
	
	public ArrayList<Arena> arenas = new ArrayList<>();
	public ArrayList<Kit> kits = new ArrayList<>();
	public Combat combatmode = Combat.DEFAULT;
	public boolean shouldAskCommunity = false;
	public Arena currentArena;
	public Kit currentKit;
	
}
