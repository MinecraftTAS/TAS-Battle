package work.mgnet.utils;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.destroystokyo.paper.Title;

import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.blockrun.BlockrunConfiguration;
import work.mgnet.blockrun.BlockrunRun;
import work.mgnet.craftmania.CraftmaniaConfiguration;
import work.mgnet.craftmania.CraftmaniaListener;
import work.mgnet.craftmania.CraftmaniaRun;
import work.mgnet.dropper.DropperConfiguration;
import work.mgnet.dropper.DropperRun;
import work.mgnet.duel.DuelRun;
import work.mgnet.ffa.FFAConfiguration;
import work.mgnet.iciclerun.IcicleConfiguration;
import work.mgnet.iciclerun.IcicleListener;
import work.mgnet.oldschoolffa.OldSchoolFFAConfiguration;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.parkour.ParkourConfiguration;
import work.mgnet.replika.ReplicaConfiguration;
import work.mgnet.replika.ReplicaRun;
import work.mgnet.speedbuild.SpeedbuildRun;
import work.mgnet.spleef.SpleefConfiguration;

@SuppressWarnings("deprecation")
public class GameStarter {
	
	public static void resetAllPlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().clear();
			for (PotionEffectType p2 : PotionEffectType.values()) {
				p.removePotionEffect(p2);
			}
			p.resetMaxHealth();
			p.setHealth(20);
		}
	}
	
	public static void startDuel() {
		Tournament.CURRENTGAME = Games.DUEL;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§7Duel", "§cGet ready!"));
			p.setTitleTimes(5, 5, 5);
			p.getInventory().clear();
			p.setGameMode(GameMode.ADVENTURE);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		try {
			UtilListener.updateTickrate(4f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DuelRun.getFights();
		DuelRun.startFight();
	}
	
	public static void startBlockrun() {
		Tournament.CURRENTGAME = Games.BLOCKRUN;
		Location selectedMap = BlockrunConfiguration.mapLocation[new Random().nextInt(BlockrunConfiguration.mapLocation.length)];
		BlockrunRun.map = selectedMap.clone();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§4Blockrun", "§cGet ready!"));
			p.teleport(selectedMap);
			p.getInventory().clear();
			p.setGameMode(GameMode.SURVIVAL);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendTitle(new Title("§aGo"));
					p.setTitleTimes(10, 10, 10);
					try {
						UtilListener.updateTickrate(4f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					p.setGameMode(GameMode.ADVENTURE);
					ItemStack kn = new ItemStack(Material.ENCHANTED_BOOK);
					ItemMeta mt = kn.getItemMeta();
					mt.setDisplayName("§5§lTome of Yeet");
					kn.setItemMeta(mt);
					kn.setAmount(3);
					p.getInventory().addItem(kn.clone());
					p.sendMessage(Prefix.BLOCKRUN + "RUN, FLOOR IS COLLAPSING " + p.getName() + "!");
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
				}
			}
		}, 100L);
	}
	
	public static void startSpleef() {
		Tournament.CURRENTGAME = Games.SPLEEF;
		IcicleListener.map = SpleefConfiguration.spleefLocation[new Random().nextInt(SpleefConfiguration.spleefLocation.length)];
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§bSpleef", "§cGet ready!"));
			p.teleport(IcicleListener.map);
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(20);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Configuration.restrictBreakAndPlace = false;
		Configuration.restrictInteract = false;
		Configuration.restrictInventory = false;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendTitle(new Title("§aGo"));
					p.setTitleTimes(10, 10, 10);
					try {
						UtilListener.updateTickrate(4f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ItemStack is = new ItemStack(Material.DIAMOND_SHOVEL);
					ItemMeta im = is.getItemMeta();
					im.setUnbreakable(true);
					is.setItemMeta(im);
					is.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
					
					p.getInventory().clear();
					p.getInventory().setHeldItemSlot(0);
					p.getInventory().setItemInMainHand(is);
					
					p.sendMessage(Prefix.SPLEEF + "Spleef em! " + p.getName() + "!");
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
				}
			}
		}, 200L);
	}
	
	public static void startReplica() {
		Tournament.CURRENTGAME = Games.REPLICA;
		ReplicaRun.playerBuild.clear();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§cRe§4plica", "§cPrepare!"));
			ReplicaRun.playerBuild.put(p, new HashMap<Location, Material>());
			Configuration.restrictInventory = false;
			Configuration.restrictInteract = false;

			p.teleport(new Location(p.getWorld(), -4, 101, 1003));
			p.setGameMode(GameMode.SURVIVAL);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (Player p2 : Bukkit.getOnlinePlayers()) {
						p.hidePlayer(p2);
						p2.hidePlayer(p);
					}
					try {
						UtilListener.updateTickrate(4f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					p.sendTitle(new Title("§aGo"));
					p.setTitleTimes(10, 10, 10);
					p.setAllowFlight(true);
					p.setFlying(true);
					p.sendMessage("§9[§cRe§4plika§9]§6 Show me your cps, " + p.getName() + "!");
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
				}
				ReplicaRun.loadBuild(new Random().nextInt(ReplicaConfiguration.numberOfBuilds));
			}
		}, 40L);
	}
	
	public static void startParkour() {
		Tournament.CURRENTGAME = Games.PARKOUR;
		try {
			UtilListener.updateTickrate(4f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§6Parkour", "§aGo!"));
			p.setTitleTimes(10, 10, 10);
			p.teleport(ParkourConfiguration.parkourLocation);
			p.setGameMode(GameMode.ADVENTURE);
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		}
	}
	
	public static void startDropper() {
		Tournament.CURRENTGAME = Games.DROPPER;
		DropperRun.loadDropper();
		try {
			UtilListener.updateTickrate(4f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§eDropper", "§aGo!"));
			p.setTitleTimes(10, 10, 10);
			p.getWorld().setGameRuleValue("fallDamage", "true");
			p.teleport(DropperConfiguration.playLoc);
			p.setGameMode(GameMode.ADVENTURE);
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		}
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
	
	public static void startIcicleRun() {
		Tournament.CURRENTGAME = Games.ICICLERUN;
		Location selectedMap = IcicleConfiguration.teleportLocation[new Random().nextInt(IcicleConfiguration.teleportLocation.length)];
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(selectedMap);
			p.setGameMode(GameMode.ADVENTURE);
			p.sendTitle(new Title("§bIcicle§9Run", "§cDanger!"));
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendTitle(new Title("§aGo"));
					try {
						UtilListener.updateTickrate(4f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					p.setTitleTimes(10, 10, 10);
					p.sendMessage(Prefix.ICICLERUN + "§6Watch your step " + p.getName() + "!");
					p.setMaxHealth(0.5);
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
				}
			}
		}, 100L);
	}
	
	public static void startOldSchoolFFA() {
		Tournament.CURRENTGAME = Games.OLDSCHOOLFFA;
		UtilListener.enablePVP = true;
		
		
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(OldSchoolFFAConfiguration.pvpLocation);
			p.setGameMode(GameMode.ADVENTURE);
			p.sendTitle(new Title("§bFFA", "§cGet Ready!"));
			p.setNoDamageTicks(280);
			p.getInventory().clear();
			p.setHealth(20);
			
			setAttackSpeed(p, 16);
			

			//Material[] items = new Material[] {Material.IRON_SWORD, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET , Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.GOLDEN_APPLE};
			
			PlayerUtils.diamondArmor(p);
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
			
			/*for (Material itm : items) {
				p.getInventory().addItem(new ItemStack(itm));
			}*/
			
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Prefix.FFA + "The Fight begins!");
			}
		}, 280L);
	}

	public static void startFFA() {
		Tournament.CURRENTGAME = Games.FFA;
		UtilListener.enablePVP = true;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(FFAConfiguration.pvpLocation);
			p.setGameMode(GameMode.ADVENTURE);
			p.sendTitle(new Title("§bFFA", "§cGet Ready!"));
			p.setNoDamageTicks(280);
			p.getInventory().clear();
			p.setHealth(20);
			
			setAttackSpeed(p, 4);
			
			/*Material[] items = new Material[] {Material.IRON_AXE, Material.IRON_SWORD, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET , Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.SHIELD, Material.GOLDEN_APPLE};
			
			
			
			for (Material itm : items) {
				p.getInventory().addItem(new ItemStack(itm));
			}*/
			
			PlayerUtils.diamondArmor(p);
			p.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
			p.getInventory().addItem(new ItemStack(Material.IRON_AXE));
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
			p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
			
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Prefix.FFA + "The Fight begins!");
				try {
					UtilListener.updateTickrate(4f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 280L);
	}

	public static void startCraftmania() {
		Tournament.CURRENTGAME = Games.CRAFTMANIA;
		CraftmaniaListener.rounds = 1;
		Configuration.restrictInventory = false;
		Configuration.restrictCrafting = false;
		Configuration.restrictInteract = false;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
			p.sendTitle(new Title("§6Craftmania", "§cGet Ready!"));
			p.getInventory().clear();
			p.teleport(CraftmaniaConfiguration.pLoc);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Prefix.CRAFTMANIA + "Go craft fast!");
				CraftmaniaRun.setCrafting();
				try {
					UtilListener.updateTickrate(4f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 100L);
	}

	public static void startReplica3d() {
		Tournament.CURRENTGAME = Games.SPEEDBUILD;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(new Title("§bSpeedbuild", "§cPrepare!"));
			Configuration.restrictInventory = false;
			Configuration.restrictInteract = false;
			Configuration.restrictBreakAndPlace = false;
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendTitle(new Title("§aGo"));
					p.setTitleTimes(10, 10, 10);
					p.sendMessage("§9[§bSpeedbuild§9]§6 Show me your cps, " + p.getName() + "!");
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
				}
				try {
					UtilListener.updateTickrate(4f);
				} catch (Exception e) {
					e.printStackTrace();
				}
				SpeedbuildRun.loadBuild();
			}
		}, 40L);
	}
}
