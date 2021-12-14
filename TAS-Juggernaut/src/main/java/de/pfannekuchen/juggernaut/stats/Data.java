package de.pfannekuchen.juggernaut.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * All Stats
 * @author Pancake
 */
public class Data implements Serializable {

	public HashMap<UUID, Integer> kills = new HashMap<>();
	public HashMap<UUID, Integer> deaths = new HashMap<>();
	public HashMap<UUID, Integer> wins = new HashMap<>();
	public HashMap<UUID, Integer> losses = new HashMap<>();
	
	public HashMap<UUID, Integer> jkills = new HashMap<>();
	public HashMap<UUID, Integer> jwins = new HashMap<>();
	public HashMap<UUID, Integer> njkills = new HashMap<>();
	public HashMap<UUID, Integer> njwins = new HashMap<>();
	
}
