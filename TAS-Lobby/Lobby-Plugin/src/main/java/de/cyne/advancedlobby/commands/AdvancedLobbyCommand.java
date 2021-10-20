package de.cyne.advancedlobby.commands;

import java.io.IOException;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import de.cyne.advancedlobby.misc.LocationManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AdvancedLobbyCommand implements CommandExecutor {

    private String prefix = "§8┃ §bAdvancedLobby §8┃ ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender.hasPermission("advancedlobby.admin")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload") | args[0].equalsIgnoreCase("rl") && sender.hasPermission("advancedlobby.reload")) {
                        reloadServer(sender);
                    }
                    if (args[0].equalsIgnoreCase("errors")) {
                        this.sendErrors(sender);
                        return true;
                    }
                    return true;
                }
            }
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.admin")) {
            if (args.length == 0) {
                sendPluginHelp(p);
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload") | args[0].equalsIgnoreCase("rl") && sender.hasPermission("advancedlobby.reload")) {
                    reloadServer(sender);
                    return true;
                }
                /**if (args[0].equalsIgnoreCase("settings")) {
                    Inventories.openAdvacedLobbySettings(p);
                    return true;
                }**/
                if (args[0].equalsIgnoreCase("errors")) {
                    this.sendErrors(p);
                    return true;
                }
                if (args[0].equalsIgnoreCase("location") | args[0].equalsIgnoreCase("loc")) {
                    p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]");
                    return true;
                }
                sendPluginHelp(p);
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("location") | args[0].equalsIgnoreCase("loc")) {
                    if (args[1].equalsIgnoreCase("list")) {
                        Set<String> locations = AdvancedLobby.cfgL.getKeys(false);
                        p.sendMessage("");
                        p.sendMessage("§8┃ §b● §8┃ §bLocations §8× §7Total§8: §f" + locations.size());
                        p.sendMessage("§8┃ §b● §8┃ ");

                        if (locations.isEmpty()) {
                            p.sendMessage("§8┃ §b● §8┃ §8- §cThere are no saved locations§8.");
                            p.sendMessage("§8┃ §b● §8┃ §8- §7Use §8/§eadvancedlobby location set §8<§elocation-name§8> §7");
                            p.sendMessage("§8┃ §b● §8┃ §7to add a new location.");
                        }

                        for (String location : locations) {
                            TextComponent component = new TextComponent("§8┃ §b● §8┃ §8- §f" + location);
                            TextComponent spacer = new TextComponent(" §8┃ ");

                            TextComponent teleport = new TextComponent("§aTeleport");
                            teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§8► §7Click to teleport to §8'§b" + location + "§8'")));
                            teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/advancedlobby location teleport " + location));

                            TextComponent remove = new TextComponent("§cRemove");
                            remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§8► §7Click to remove §8'§b" + location + "§8'")));
                            remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/advancedlobby location remove " + location));

                            component.addExtra(spacer);
                            component.addExtra(teleport);
                            component.addExtra(spacer);
                            component.addExtra(remove);

                            p.spigot().sendMessage(component);
                        }
                        p.sendMessage("");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("set")) {
                        p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location set §8<§clocation-name§8>");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location remove §8<§clocation-name§8>");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("teleport") | args[1].equalsIgnoreCase("tp")) {
                        p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location teleport §8<§clocation-name§8>");
                        return true;
                    }
                    p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]");
                    return true;
                }
                sendPluginHelp(p);
                return true;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("location") | args[0].equalsIgnoreCase("loc")) {
                    if (args[1].equalsIgnoreCase("set")) {
                        LocationManager.saveLocation(p.getLocation(), args[2]);
                        p.sendMessage(this.prefix + "§7The location §b" + args[2] + " §7has been set §asuccessfully§8.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        if (LocationManager.getLocation(args[2]) != null) {
                            LocationManager.deleteLocation(args[2]);
                            p.sendMessage(this.prefix + "§7The location §b" + args[2] + " §7has been §cremoved§8.");
                            return true;
                        }
                        p.sendMessage(this.prefix + "§cThe location §b" + args[2] + " §cwas not found§8.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("teleport") | args[1].equalsIgnoreCase("tp")) {
                        if (LocationManager.getLocation(args[2]) != null) {
                            p.teleport(LocationManager.getLocation(args[2]));
                            p.sendMessage(this.prefix + "§7You have been teleported to §b" + args[2] + "§8.");
                            return true;
                        }
                        p.sendMessage(this.prefix + "§cThe location §b" + args[2] + " §cwas not found§8.");
                        return true;
                    }
                    p.sendMessage(this.prefix + "§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]");
                    return true;
                }
            }
            sendPluginHelp(p);
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

    private void reloadServer(CommandSender sender) {
        long start = System.currentTimeMillis();
        sender.sendMessage("");
        sender.sendMessage(this.prefix + "§cReloading§8..");
        try {
            if (!AdvancedLobby.file.exists()) {
                AdvancedLobby.getInstance().saveDefaultConfig();
            }
            if (!AdvancedLobby.fileLocations.exists() | !AdvancedLobby.fileMessages.exists() | !AdvancedLobby.fileSounds.exists()) {
                AdvancedLobby.getInstance().getLogger().info("One or more files were not found. Creating..");
                if (!AdvancedLobby.fileLocations.exists()) {
                    AdvancedLobby.fileLocations.getParentFile().mkdirs();
                    try {
                        AdvancedLobby.fileLocations.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!AdvancedLobby.fileMessages.exists()) {
                    AdvancedLobby.fileMessages.getParentFile().mkdirs();
                    AdvancedLobby.getInstance().saveResource("messages.yml", false);
                }
                if (!AdvancedLobby.fileSounds.exists()) {
                    AdvancedLobby.fileMessages.getParentFile().mkdirs();
                    AdvancedLobby.getInstance().saveResource("sounds.yml", false);
                }
            }

            AdvancedLobby.cfg.load(AdvancedLobby.file);
            AdvancedLobby.cfgM.load(AdvancedLobby.fileMessages);
            AdvancedLobby.cfgL.load(AdvancedLobby.fileLocations);
            AdvancedLobby.cfgS.load(AdvancedLobby.fileSounds);

            AdvancedLobby.actionbarMessages.clear();
            if (AdvancedLobby.cfg.getBoolean("actionbar.enabled")) {
                AdvancedLobby.scheduler.stop();
                AdvancedLobby.actionbarMessages.addAll(AdvancedLobby.cfg.getStringList("actionbar.messages"));
                AdvancedLobby.scheduler.start();
            }


        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        long duration = System.currentTimeMillis() - start;
        sender.sendMessage(this.prefix + "§aReload finished, took §e" + duration + "ms§8.");
        sender.sendMessage("");
    }

    private void sendErrors(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§8┃ §4● §8┃ §cErrors §8× §7Total§8: §f" + AdvancedLobby.errors.size());
        sender.sendMessage("§8┃ §4● §8┃ ");

        if (AdvancedLobby.errors.isEmpty()) {
            sender.sendMessage("§8┃ §4● §8┃ §8- §aThere are no errors§8.");
        }

        for (String error : AdvancedLobby.errors.keySet()) {
            sender.sendMessage("§8┃ §4● §8┃ §8- §7Couldn't find §f" + AdvancedLobby.errors.get(error) + "§8: §c" + error);
        }
        sender.sendMessage("");
    }

    private void sendPluginHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§8┃ §b● §8┃ §bAdvancedLobby §8× §av"
                + AdvancedLobby.getInstance().getDescription().getVersion() + " §7by cyne");
        sender.sendMessage("§8┃ §b● §8┃ ");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby reload §8- §7Reload the configuration files");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby location §8- §7Manage the locations");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby errors §8- §7List all errors");
        sender.sendMessage("");
    }

}