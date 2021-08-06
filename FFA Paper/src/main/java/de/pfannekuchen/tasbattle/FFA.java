package de.pfannekuchen.tasbattle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.pfannekuchen.tasbattle.Configuration.Arena;
import de.pfannekuchen.tasbattle.Configuration.Combat;
import de.pfannekuchen.tasbattle.Configuration.Kit;
import de.pfannekuchen.tasbattle.util.BukkitSerialization;
import net.kyori.adventure.text.Component;

public class FFA extends JavaPlugin implements Listener, PluginMessageListener {

	/* Disable Block Placing and Damage in the Lobby World */
	@EventHandler public void onBlockBreak(BlockBreakEvent e) { if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby") && !e.getPlayer().isOp()) e.setCancelled(true); }
	@EventHandler public void onBlockPlace(BlockPlaceEvent e) { if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby") && !e.getPlayer().isOp()) e.setCancelled(true); }
	@EventHandler public void onDamage(EntityDamageEvent e) { if (e.getEntity().getWorld().getName().equalsIgnoreCase("lobby")) e.setCancelled(true); }
	/* Teleport Players that connect to the Lobby, or the spectating Lobby */
	@EventHandler public void onConnect(PlayerJoinEvent e) {
		// TODO: Spectator
		e.joinMessage(null);
		e.getPlayer().teleport(new Location(Bukkit.getWorld("lobby"), 0, 100, 0));
	}
	
	public static FFA PLUGIN;
	
	@Override
	public void onEnable() {
		PLUGIN = this;
		getDataFolder().mkdirs();
		Configuration.configFile = new File(getDataFolder(), "config.dat");
		try {
			Configuration.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "ffa:data");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "ffa:data", this);
		Bukkit.getPluginManager().registerEvents(this, this);
		super.onEnable();
	}
	
	/**
	 * Tab Completing for the FFA Command
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return Arrays.asList("help", "kit", "arena", "game");
		} else if (args.length == 2) {
			switch (args[0]) {
				case "kit": 
					return Arrays.asList("select", "list", "create", "delete", "loaditems");
				case "arena": 
					return Arrays.asList("select", "list", "create", "delete");
				case "game": 
					return Arrays.asList("info", "start", "stop", "spectate", "togglecommunity", "togglecombat");
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("kit") && (args[1].equalsIgnoreCase("select") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("loaditems"))) return Configuration.getInstance().kits.stream().map(Kit::toString).toList();
			if (args[0].equalsIgnoreCase("arena") && (args[1].equalsIgnoreCase("select") || args[1].equalsIgnoreCase("delete"))) return Configuration.getInstance().arenas.stream().map(Arena::toString).toList();
			if (args[0].equalsIgnoreCase("arena") && args[1].equalsIgnoreCase("togglecombat")) return Arrays.asList("OLD", "NEW", "DEFAULT");
		}
		return super.onTabComplete(sender, command, alias, args);
	}
	
	/**
	 * The Command for FFA Managing
	 */
	@Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) return false;
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(Component.text("§6» §7Displaying Help for /ffa\n"
					+ "§6» §7/ffa game help - §bDisplays help about all Game Commands\n"
					+ "§6» §7/ffa kit help - §bDisplays help about all Kit Commands\n"
					+ "§6» §7/ffa arena help - §bDisplays help about all Arena Commands"));
		} else if (args[0].equalsIgnoreCase("game") && args.length >= 2) {
			switch (args[1]) {
			case "help":
				sender.sendMessage(Component.text("§6» §7Displaying Help for /ffa\n"
						+ "§6» §7/ffa game info - §bDisplays Game Info\n"
						+ "§6» §7/ffa game start - §bStarts the game now.\n"
						+ "§6» §7/ffa game stop - §bStops the game now.\n"
						+ "§6» §7/ffa game spectate - §bSpectates the current game, instead of playing.\n"
						+ "§6» §7/ffa game togglecommunity - §bDisables or enables community voting\n"
						+ "§6» §7/ffa game togglecombat <combat> - §bForces a specific combat system for the next game"));
				break;
			case "togglecombat": 
				if (args.length == 3) {
					try {
						Combat c = Combat.valueOf(args[2].toUpperCase());
						// TODO: Update Combat Mode
						Configuration.getInstance().combatmode = c;
						break;
					} catch (Exception e) {}
				}
				sender.sendMessage(Component.text("§6» §7What Combat Mode? NEW, OLD, DEFAULT."));
				break;
			case "togglecommunity":
				Configuration.getInstance().shouldAskCommunity = !Configuration.getInstance().shouldAskCommunity;
				if (Configuration.getInstance().shouldAskCommunity) sender.sendMessage(Component.text("§6» §7The Players can now decide what settings to play with."));	
				else sender.sendMessage(Component.text("§6» §7The Players are forced to play the selected settings."));
				break;
			case "start": 
				// TODO: Start the Game
				break;
			case "stop": 
				// TODO: End the Game
				break;
			case "info": 
				if (Configuration.getInstance().shouldAskCommunity) sender.sendMessage(Component.text("§6» §7There are no settings, because the Community can decide which Map/Kit to play with."));
				else {
					sender.sendMessage(Component.text("§6» §7Current Settings:"));
					sender.sendMessage(Component.text("§6» §7Map: " + Configuration.getInstance().currentArena));
					sender.sendMessage(Component.text("§6» §7Kit: " + Configuration.getInstance().currentKit));
				}
				break;
			case "spectate": 
				// TODO: Spectate a game
				break;
			}
		} else if (args[0].equalsIgnoreCase("kit") && args.length >= 2) {
			switch (args[1]) {
			case "help":
				sender.sendMessage(Component.text("§6» §7Displaying Help for /ffa\n"
						+ "§6» §7/ffa kit create <name> - §bAdds your current inventory as a kit\n"
						+ "§6» §7/ffa kit delete <name> - §bRemoves a kit registration\n"
						+ "§6» §7/ffa kit loaditems <name> - §bLoads the kit into your inventory\n"
						+ "§6» §7/ffa kit select <name> - §bSelects a kit for the next game\n"
						+ "§6» §7/ffa kit list - §bList all kits\n"));
				break;
			case "create": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7Enter a kit name"));
					break;
				}
				Kit kt = new Kit();
				kt.name = args[2];
				kt.data = BukkitSerialization.playerInventoryToBase64(((Player) sender).getInventory());
				Configuration.getInstance().kits.add(kt);
				sender.sendMessage(Component.text("§6» §7Kit created."));
				break;
			case "delete": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7What Kit should be deleted?"));
					break;
				}
				for (Kit kit : new ArrayList<>(Configuration.getInstance().kits)) {
					if (kit.name.equalsIgnoreCase(args[2])) {
						Configuration.getInstance().kits.remove(kit);
						sender.sendMessage(Component.text("§6» §7The Kit has been deleted."));
						try { Configuration.save(); } catch (Exception e) {}
						return true;
					}
				}
				sender.sendMessage(Component.text("§6» §7Kit not found."));
				break;
			case "loaditems": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7What Kit should be loaded into your Inventory?"));
					break;
				}
				for (Kit kit : Configuration.getInstance().kits) {
					if (kit.name.equalsIgnoreCase(args[2])) {
						sender.sendMessage(Component.text("§6» §7Kit was loaded into your inventory."));
						try {
							BukkitSerialization.playerInventoryToBase64((Player) sender, kit.data);
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
							sender.sendMessage(Component.text("§6» §7Kit is corrupted. :("));
						}
						try { Configuration.save(); } catch (Exception e) {}
						return true;
					}
				}
				sender.sendMessage(Component.text("§6» §7Kit not found!"));
				break;
			case "select": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7What Kit should be selected?"));
					break;
				}
				for (Kit kit : Configuration.getInstance().kits) {
					if (kit.name.equalsIgnoreCase(args[2])) {
						sender.sendMessage(Component.text("§6» §7Kit selected."));
						Configuration.getInstance().currentKit = kit;
						try { Configuration.save(); } catch (Exception e) {}
						return true;
					}
				}
				sender.sendMessage(Component.text("§6» §7Kit not found!"));
				break;
			case "list": 
				sender.sendMessage(Component.text("§6» §7There are " + Configuration.getInstance().kits.size()  + " kits registered."));
				for (Kit kit : Configuration.getInstance().kits) sender.sendMessage(Component.text("§6» §7" + kit.name));
				break;
			}
		} else if (args[0].equalsIgnoreCase("arena") && args.length >= 2) {
			switch (args[1]) {
			case "help":
				sender.sendMessage(Component.text("§6» §7Displaying Help for /ffa\n"
						+ "§6» §7/ffa arena list - §bShows all arenas\n"
						+ "§6» §7/ffa arena create <name> - §bAdds an arena with <name> as worldname\n"
						+ "§6» §7/ffa arena select <name> - §bSets an arena with <name> as active\n"
						+ "§6» §7/ffa arena delete <name> - §bRemoves an arena from the list\n"));
				break;
			case "list":
				sender.sendMessage(Component.text("§6» §7There are " + Configuration.getInstance().arenas.size()  + " arenas registered."));
				for (Arena arena : Configuration.getInstance().arenas) sender.sendMessage(Component.text("§6» §7" + arena.name));
				break;
			case "create":
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7Enter a world name"));
					break;
				}
				File world_dir = new File(getDataFolder().getParentFile().getParentFile(), args[2]);
				if (!world_dir.exists()) {
					sender.sendMessage(Component.text("§6» §7Enter a world name."));
					break;
				}
				Arena ar = new Arena();
				ar.name = args[2];
				Configuration.getInstance().arenas.add(ar);
				sender.sendMessage(Component.text("§6» §7Arena successfully created."));
				break;
			case "select": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7Expected Arena as parameter"));
					break;
				}
				for (Arena arena : Configuration.getInstance().arenas) {
					if (arena.name.equalsIgnoreCase(args[2])) {
						sender.sendMessage(Component.text("§6» §7Arena selected."));
						Configuration.getInstance().currentArena = arena;
						try { Configuration.save(); } catch (Exception e) {}
						return true;
					}
				}
				sender.sendMessage(Component.text("§6» §7Expected Arena as parameter"));
				break;
			case "delete": 
				if (args.length != 3) {
					sender.sendMessage(Component.text("§6» §7Expected Arena as parameter"));
					break;
				}
				for (Arena arena : new ArrayList<>(Configuration.getInstance().arenas)) {
					if (arena.name.equalsIgnoreCase(args[2])) {
						Configuration.getInstance().arenas.remove(arena);
						sender.sendMessage(Component.text("§6» §7The Arena has been deleted."));
						try { Configuration.save(); } catch (Exception e) {}
						return true;
					}
				}
				sender.sendMessage(Component.text("§6» §7Arena not found!"));
				break;
			}
		}
		try { Configuration.save(); } catch (Exception e) {}
		return true;
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		if (!channel.equalsIgnoreCase("ffa:data")) return;
	}
	
}
