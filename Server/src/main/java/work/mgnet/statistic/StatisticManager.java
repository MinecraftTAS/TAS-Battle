package work.mgnet.statistic;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import work.mgnet.utils.BlockUtils;

public class StatisticManager {

	public static HashMap<String, Integer> scores = new HashMap<String, Integer>();
	
	public static int addPoints(String p, int points) {
		if (!scores.containsKey(p)) scores.put(p, 0);
		
		int currentScore = scores.get(p);
		currentScore += points;
		
		scores.remove(p);
		scores.put(p, currentScore);
		
		updateScoreboard();
		return currentScore;
	}
	
	public static void updateScoreboard() {
		List<Entry<String, Integer>> sortedScores = getSortedScores();
		
		for (int i = 0; i < 5; i++) {
			sortedScores.add(new AbstractMap.SimpleEntry<String, Integer>("X", 0));
			Entry<String, Integer> user = sortedScores.get(0);
			BlockUtils.editHead(new Location(Bukkit.getWorlds().get(0), 10002 - i, 103, 10011).getBlock(), user.getKey());
			BlockUtils.changeSign(new Location(Bukkit.getWorlds().get(0), 10002 - i, 104, 10011).getBlock(), "Place " + (i + 1), "Points", user.getValue() + "", user.getKey());
			sortedScores.remove(0);
		}
		
	}
	
	public static List<Entry<String, Integer>> getSortedScores() {
		List<Entry<String, Integer>> sortedScores = new LinkedList<Entry<String, Integer>>(scores.entrySet());
		
		Collections.sort(sortedScores, new Comparator<Entry<String, Integer> >() { 
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) { 
                return o2.getValue() - o1.getValue();
            } 
        });
		return sortedScores;
	}
	
}
