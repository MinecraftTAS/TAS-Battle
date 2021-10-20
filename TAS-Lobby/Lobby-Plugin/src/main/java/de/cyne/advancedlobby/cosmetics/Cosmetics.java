package de.cyne.advancedlobby.cosmetics;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.crossversion.VMaterial;
import de.cyne.advancedlobby.crossversion.VParticle;
import de.cyne.advancedlobby.itembuilder.ItemBuilder;
import de.cyne.advancedlobby.locale.Locale;
import de.cyne.advancedlobby.misc.Balloon;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class Cosmetics {

    public static HashMap<Player, HatType> hats = new HashMap<>();
    public static HashMap<Player, ParticleType> particles = new HashMap<>();
    public static HashMap<Player, Balloon> balloons = new HashMap<>();
    public static HashMap<Player, GadgetType> gadgets = new HashMap<>();
    public static ArrayList<Player> gadgetReloading = new ArrayList<>();

    public enum HatType {
        MELON_BLOCK, MELON, TNT, GLASS, SPONGE, PUMPKIN, CACTUS
    }

    public enum ParticleType {
        HEART, MUSIC, FLAMES, VILLAGER, RAINBOW
    }

    public enum BalloonType {
        YELLOW, RED, GREEN, BLUE, HAY_BLOCK, SEA_LANTERN, BOOKSHELF, NOTE_BLOCK
    }

    public enum GadgetType {
        GRAPPLING_HOOK, ROCKET_JUMP
    }

    public static void equipHat(Player player, HatType type) {
        if (hats.containsKey(player)) {
            hats.remove(player);
        }
        ItemStack hat = null;
        String message = null;
        switch (type) {
            case MELON_BLOCK:
            case MELON:
                if (player.hasPermission("advancedlobby.cosmetics.hats.melon")) {
                    hat = new ItemBuilder(VMaterial.MELON.getType()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"));
                    hats.put(player, HatType.MELON);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"));
                break;
            case TNT:
                if (player.hasPermission("advancedlobby.cosmetics.hats.tnt")) {
                    hat = new ItemBuilder(Material.TNT).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"));
                    hats.put(player, HatType.TNT);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"));
                break;
            case GLASS:
                if (player.hasPermission("advancedlobby.cosmetics.hats.glass")) {
                    hat = new ItemBuilder(Material.GLASS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"));
                    hats.put(player, HatType.GLASS);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"));
                break;
            case SPONGE:
                if (player.hasPermission("advancedlobby.cosmetics.hats.sponge")) {
                    hat = new ItemBuilder(Material.SPONGE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"));
                    hats.put(player, HatType.SPONGE);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"));
                break;
            case PUMPKIN:
                if (player.hasPermission("advancedlobby.cosmetics.hats.pumpkin")) {
                    hat = new ItemBuilder(Material.PUMPKIN).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"));
                    hats.put(player, HatType.PUMPKIN);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"));
                break;
            case CACTUS:
                if (player.hasPermission("advancedlobby.cosmetics.hats.cactus")) {
                    hat = new ItemBuilder(Material.CACTUS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"));
                    hats.put(player, HatType.CACTUS);
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"));
                    break;
                }
                message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player).replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"));
                break;
        }
        player.getInventory().setHelmet(hat);
        player.sendMessage(message);
    }

    public static void equipBalloon(Player player, BalloonType type) {
        Balloon balloon = null;
        switch (type) {
            case YELLOW:
                balloon = new Balloon(player, VMaterial.YELLOW_TERRACOTTA.toItemStack());
                break;
            case RED:
                balloon = new Balloon(player, VMaterial.RED_TERRACOTTA.toItemStack());
                break;
            case GREEN:
                balloon = new Balloon(player, VMaterial.LIME_TERRACOTTA.toItemStack());
                break;
            case BLUE:
                balloon = new Balloon(player, VMaterial.LIGHT_BLUE_TERRACOTTA.toItemStack());
                break;
            case HAY_BLOCK:
                balloon = new Balloon(player, Material.HAY_BLOCK);
                break;
            case SEA_LANTERN:
                balloon = new Balloon(player, Material.SEA_LANTERN);
                break;
            case BOOKSHELF:
                balloon = new Balloon(player, Material.BOOKSHELF);
                break;
            case NOTE_BLOCK:
                balloon = new Balloon(player, Material.NOTE_BLOCK);
                break;
        }
        if (type != null) {
            if (!AdvancedLobby.silentLobby.contains(player)) {
                balloon.create();
            }
            balloons.put(player, balloon);
        }
    }

    public static void equipGadget(Player player, GadgetType type) {
        ItemStack gadget = null;
        switch (type) {
            case GRAPPLING_HOOK:
                gadget = new ItemBuilder(Material.FISHING_ROD).setDisplayName(AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname"))).setLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.equipped.lore"));
                break;
            case ROCKET_JUMP:
                gadget = new ItemBuilder(Material.FEATHER).setDisplayName(AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname"))).setLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.equipped.lore"));
                break;
        }
        player.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.gadget.slot"), gadget);
        gadgets.put(player, type);
    }

    public static void startBalloonTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (balloons.containsKey(players)) {
                    if (!AdvancedLobby.multiWorld_mode | players.getWorld() == AdvancedLobby.lobbyWorlds) {
                        if (!AdvancedLobby.silentLobby.contains(players)) {
                            if (balloons.get(players).getFallingBlock() == null) {
                                balloons.get(players).create();
                            }
                            if (balloons.get(players).getFallingBlock().isDead() | balloons.get(players).getBat().isDead()) {
                                balloons.get(players).remove();
                                balloons.get(players).create();
                            }
                            Bat localBat = balloons.get(players).getBat();

                            Location location = players.getLocation();
                            location.setYaw(location.getYaw() + 90.0F);
                            location.setPitch(-45.0F);

                            Vector direction = location.getDirection().normalize();
                            location.add(direction.getX() * 1.5D, direction.getY() * 1.5D + 0.5D, direction.getZ() * 1.5D);

                            Vector locationVector = location.toVector();
                            Vector batVector = balloons.get(players).getBat().getLocation().toVector();

                            localBat.setVelocity(locationVector.clone().subtract(batVector).multiply(0.5D));
                        }
                    }
                }
            }
        }, 0L, 3L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                for (FallingBlock fallingBlocks : Balloon.fallingBlocks.values()) {
                    if (!AdvancedLobby.multiWorld_mode | players.getWorld() == AdvancedLobby.lobbyWorlds) {
                        if (!AdvancedLobby.silentLobby.contains(players)) {
                            VParticle.spawnParticle(players, "SPELL", fallingBlocks.getLocation(), 4, 0.0f, 0.0f, 0.0f, 0.1f);
                        }
                    }
                }
            }
        }, 90L, 90L);
    }

    public static void reloadGadget(Player player) {
        Cosmetics.gadgetReloading.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), () -> {
            if (player.isOnline()) {
                Cosmetics.gadgetReloading.remove(player);
            }
        }, 100L);
    }

}
