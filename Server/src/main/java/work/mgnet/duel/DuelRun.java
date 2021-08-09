package work.mgnet.duel;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class DuelRun {
	
	public static String pvp1;
	public static String pvp2;
	
	public static ArrayList<String> fights = new ArrayList<String>();
	public static ArrayList<String> doneFights = new ArrayList<String>();
	
	public static void getFights() {
		fights.clear();
		doneFights.clear();
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p.getName().equalsIgnoreCase(p2.getName())) continue;
				fights.add(p.getName() + ":" + p2.getName());
			}
		}
	}
	
	public static void startFight() {
		if (fights.size() == 0) {
			Bukkit.broadcast(Component.text(Prefix.DUEL + "The Game is over!"));
			Tournament.CURRENTGAME = Games.NONE;
			Configuration.restrictInventory = true;
			Configuration.restrictOffhand = true;
			Configuration.restrictInteract = true;
			try {
				UtilListener.updateTickrate(20f);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			return;
		}
		pvp1 = fights.get(0).split(":")[0];
		pvp2 = fights.get(0).split(":")[1];
		fights.remove(0);
		Player p = Bukkit.getPlayer(pvp1);
		Player p2 = Bukkit.getPlayer(pvp2);
		
		p.teleport(DuelConfiguration.arenaLocation1);
		p2.teleport(DuelConfiguration.arenaLocation2);
		
		Bukkit.broadcast(Component.text(Prefix.DUEL + "A new Game has begun!"));
		
		p.sendMessage(Prefix.DUEL + "You are fighting against " + p2.getName());
		p2.sendMessage(Prefix.DUEL + "You are fighting against " + p.getName());
		
		p.setNoDamageTicks(80);
		p2.setNoDamageTicks(80);
		
		p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		p2.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		p2.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		p2.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		p2.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		
		p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
		
		if (!doneFights.contains(pvp1 + ":" + pvp2)) p.getInventory().addItem(new ItemStack(Material.SHIELD));
		if (!doneFights.contains(pvp1 + ":" + pvp2)) p2.getInventory().addItem(new ItemStack(Material.SHIELD));
		
		if (doneFights.contains(pvp1 + ":" + pvp2)) p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
		
		p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
		p2.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
		
		if (doneFights.contains(pvp1 + ":" + pvp2)) p2.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
		
		p2.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
		
		p.setGameMode(GameMode.ADVENTURE);
		p2.setGameMode(GameMode.ADVENTURE);
		
		if (doneFights.contains(pvp1 + ":" + pvp2)) {
			setAttackSpeed(p, 16);
			setAttackSpeed(p2, 16);
			Bukkit.broadcast(Component.text(Prefix.DUEL + "This Duel will be in 1.8 Combat"));
		} else {
			setAttackSpeed(p, 4);
			setAttackSpeed(p2, 4);
			Bukkit.broadcast(Component.text(Prefix.DUEL + "This Duel will be in 1.16 Combat"));
		}
		
		doneFights.add(pvp2 + ":" + pvp1);
	}
	
    public static void setAttackSpeed(Player player, double attackSpeed){
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if(attribute == null){
            return;
        }

        double baseValue = attribute.getBaseValue();

        if(baseValue != attackSpeed){
            attribute.setBaseValue(attackSpeed);
            player.saveData();
        }
    }
    
}
