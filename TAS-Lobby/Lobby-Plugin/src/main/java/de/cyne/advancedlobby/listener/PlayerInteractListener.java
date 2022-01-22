package de.cyne.advancedlobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.crossversion.VMaterial;
import de.cyne.advancedlobby.crossversion.VParticle;
import de.cyne.advancedlobby.inventories.Inventories;
import de.cyne.advancedlobby.itembuilder.ItemBuilder;
import de.cyne.advancedlobby.locale.Locale;
import de.cyne.advancedlobby.misc.HiderType;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        	e.setCancelled(true);
        
        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
            if (e.getAction() == Action.PHYSICAL) {
                if (AdvancedLobby.cfg.getBoolean("disable_physical_player_interaction")) {
                    if (!AdvancedLobby.build.contains(p)) e.setCancelled(true);
                }
            }

            if (e.getAction() == Action.LEFT_CLICK_BLOCK | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.CHEST | e.getClickedBlock().getType() == Material.ENDER_CHEST | e.getClickedBlock().getType() == Material.TRAPPED_CHEST | e.getClickedBlock().getType() == VMaterial.CRAFTING_TABLE.getType() | e.getClickedBlock().getType() == Material.FURNACE | e.getClickedBlock().getType() == Material.ENDER_CHEST | e.getClickedBlock().getType() == VMaterial.ENCHANTING_TABLE.getType() | e.getClickedBlock().getType() == Material.ANVIL | e.getClickedBlock().getType() == VMaterial.BLUE_BED.getType() | e.getClickedBlock().getType() == VMaterial.BLACK_BED.getType() | e.getClickedBlock().getType() == VMaterial.BROWN_BED.getType() | e.getClickedBlock().getType() == VMaterial.CYAN_BED.getType() | e.getClickedBlock().getType() == VMaterial.GRAY_BED.getType() | e.getClickedBlock().getType() == VMaterial.GREEN_BED.getType() | e.getClickedBlock().getType() == VMaterial.LIGHT_BLUE_BED.getType() | e.getClickedBlock().getType() == VMaterial.LIGHT_GRAY_BED.getType() | e.getClickedBlock().getType() == VMaterial.LIME_BED.getType() | e.getClickedBlock().getType() == VMaterial.MAGENTA_BED.getType() | e.getClickedBlock().getType() == VMaterial.ORANGE_BED.getType() | e.getClickedBlock().getType() == VMaterial.PINK_BED.getType() | e.getClickedBlock().getType() == VMaterial.PURPLE_BED.getType() | e.getClickedBlock().getType() == VMaterial.RED_BED.getType() | e.getClickedBlock().getType() == VMaterial.WHITE_BED.getType() | e.getClickedBlock().getType() == VMaterial.YELLOW_BED.getType() | e.getClickedBlock().getType() == Material.JUKEBOX | e.getClickedBlock().getType() == Material.BEACON | e.getClickedBlock().getType() == Material.DISPENSER | e.getClickedBlock().getType() == Material.LEVER | e.getClickedBlock().getType() == Material.STONE_BUTTON | e.getClickedBlock().getType() == VMaterial.ACACIA_BUTTON.getType() | e.getClickedBlock().getType() == VMaterial.BIRCH_BUTTON.getType() | e.getClickedBlock().getType() == VMaterial.DARK_OAK_BUTTON.getType() | e.getClickedBlock().getType() == VMaterial.JUNGLE_BUTTON.getType() | e.getClickedBlock().getType() == VMaterial.OAK_BUTTON.getType() | e.getClickedBlock().getType() == VMaterial.SPRUCE_BUTTON.getType() | e.getClickedBlock().getType() == Material.DAYLIGHT_DETECTOR | e.getClickedBlock().getType() == Material.HOPPER | e.getClickedBlock().getType() == Material.DROPPER | e.getClickedBlock().getType() == Material.BREWING_STAND | e.getClickedBlock().getType() == VMaterial.COMPARATOR.getType() | e.getClickedBlock().getType() == VMaterial.REPEATER.getType() | e.getClickedBlock().getType() == Material.DRAGON_EGG | e.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                    if (AdvancedLobby.cfg.getBoolean("disable_player_interaction")) {
                        if (!AdvancedLobby.build.contains(p)) e.setCancelled(true);
                    }
                }
                if (e.getClickedBlock().getType() == Material.FLOWER_POT) {
                    if (!AdvancedLobby.build.contains(p)) e.setCancelled(true);
                }
            }

            if (e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (item != null) {
                    /*
                     * COMPASS >
                     */
                    if (item.getType() == AdvancedLobby.getMaterial("hotbar_items.teleporter.material") && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(AdvancedLobby.getString("hotbar_items.teleporter.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.teleporter.enabled")) {
                            e.setCancelled(true);
                            AdvancedLobby.playSound(p, p.getLocation(), "teleporter.open_inventory");
                            Inventories.openCompassInventory(p);
                            return;
                        }
                    }
                    /*
                     * COMPASS <
                     */

                    /*
                     * COSMETICS >
                     */
                    if (item.getType() == AdvancedLobby.getMaterial("hotbar_items.cosmetics.material") && item.hasItemMeta() && item
                            .getItemMeta().getDisplayName().equals(AdvancedLobby.getString("hotbar_items.cosmetics.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.cosmetics.enabled")) {
                            e.setCancelled(true);
                            AdvancedLobby.playSound(p, p.getLocation(), "cosmetics.open_inventory");
                            Inventories.openCosmetics(p);
                            return;
                        }
                    }
                    /*
                     * COSMETICS <
                     */

                    /*
                     * GADGETS >
                     */
                    if (item.getType() == Material.FEATHER && item.hasItemMeta() && ChatColor.stripColor(item
                            .getItemMeta().getDisplayName()).equals(ChatColor.stripColor(AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname"))))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.gadget.enabled")) {
                            e.setCancelled(true);
                            if (Cosmetics.gadgetReloading.contains(p)) {
                                AdvancedLobby.playSound(p, p.getLocation(), "gadgets.reload_gadget");
                                return;
                            }
                            AdvancedLobby.playSound(p, p.getLocation(), "gadgets.rocket_jump");
                            //p.spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation().add(0.0, 1.0, 0.0), 1);

                            VParticle.spawnParticle(p, "EXPLOSION_LARGE", p.getLocation(), 1);

                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (p != players) {
                                    if (!AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players) && !AdvancedLobby.playerHider.containsKey(players)) {
                                        AdvancedLobby.playSound(players, p.getLocation(), "gadgets.rocket_jump");
                                        //players.spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation().add(0.0, 1.0, 0.0), 1);
                                        VParticle.spawnParticle(players, "EXPLOSION_LARGE", p.getLocation(), 1);
                                    }
                                }
                            }
                            Cosmetics.reloadGadget(p);
                            p.setVelocity(new Vector(0.0D, 1.0D, 0.0D));
                            return;
                        }
                    }
                    /*
                     * GADGETS <
                     */

                    /*
                     * HIDER >
                     */
                    if (item.getType() == AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material")
                            | item.getType() == Material
                            .getMaterial(AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_vip.material"))
                            | item.getType() == Material
                            .getMaterial(AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_none.material"))
                            && item.hasItemMeta() && item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
                            | item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.player_hider.show_vip.displayname"))
                            | item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled")) {
                            e.setCancelled(true);
                            if (AdvancedLobby.silentLobby.contains(p)) {
                                p.sendMessage(Locale.SILENTLOBBY_FUNCTION_BLOCKED.getMessage(p));
                                return;
                            }
                            if (AdvancedLobby.playerHider.containsKey(p)) {
                                if (AdvancedLobby.playerHider.get(p) == HiderType.NONE) {
                                    AdvancedLobby.playerHider.remove(p);
                                    p.sendMessage(Locale.HIDER_SHOW_ALL.getMessage(p));
                                    AdvancedLobby.playSound(p, p.getLocation(), "player_hider");
//                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                                    ItemStack hider_all = new ItemBuilder(
                                            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"), 1,
                                            (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_all.subid"))
                                            .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
                                            .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"));
                                    p.getInventory().setItemInMainHand(hider_all);
                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        if (p != players) {
                                            if (!AdvancedLobby.silentLobby.contains(players)) {
                                                p.showPlayer(AdvancedLobby.getInstance(), players);
                                            }
                                        }
                                    }
                                    return;
                                }
                                if (AdvancedLobby.playerHider.get(p) == HiderType.VIP) {
                                    AdvancedLobby.playerHider.put(p, HiderType.NONE);
                                    p.sendMessage(Locale.HIDER_SHOW_NONE.getMessage(p));
                                    AdvancedLobby.playSound(p, p.getLocation(), "player_hider");
//                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                                    ItemStack hider_none = new ItemBuilder(
                                            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_none.material"), 1,
                                            (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_none.subid"))
                                            .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname"))
                                            .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_none.lore"));
                                    p.getInventory().setItemInMainHand(hider_none);
                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        p.hidePlayer(AdvancedLobby.getInstance(), players);
                                    }
                                    return;
                                }
                                return;
                            }
                            AdvancedLobby.playerHider.put(p, HiderType.VIP);
                            p.sendMessage(Locale.HIDER_SHOW_VIP.getMessage(p));
                            AdvancedLobby.playSound(p, p.getLocation(), "player_hider");
//                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                            ItemStack hider_vip = new ItemBuilder(
                                    AdvancedLobby.getMaterial("hotbar_items.player_hider.show_vip.material"), 1,
                                    (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_vip.subid"))
                                    .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_vip.displayname"))
                                    .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_vip.lore"));
                            p.getInventory().setItemInMainHand(hider_vip);
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (!players.hasPermission("advancedlobby.player_hider.bypass")) {
                                    p.hidePlayer(AdvancedLobby.getInstance(), players);
                                }
                            }
                            return;
                        }
                    }
                    /*
                     * HIDER <
                     */

                    /*
                     * SILENTLOBBY >
                     */
                    if (item.getType() == AdvancedLobby
                            .getMaterial("hotbar_items.silentlobby.activated.material")
                            | item.getType() == AdvancedLobby
                            .getMaterial("hotbar_items.silentlobby.deactivated.material")
                            && item.hasItemMeta() && item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.silentlobby.activated.displayname"))
                            | item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.silentlobby.deactivated.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled")) {
                            e.setCancelled(true);
                            if (!AdvancedLobby.silentLobby.contains(p)) {
                                AdvancedLobby.silentLobby.add(p);
                                AdvancedLobby.playerHider.put(p, HiderType.NONE);
                                p.sendMessage(Locale.SILENTLOBBY_JOIN.getMessage(p));
                                AdvancedLobby.playSound(p, p.getLocation(), "silentlobby.enable_silentlobby");
                                VParticle.spawnParticle(p, "EXPLOSION_HUGE", p.getLocation(), 1);
//                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));

                                for (Player players : Bukkit.getOnlinePlayers()) {
                                    if (players != p) {
                                        players.hidePlayer(AdvancedLobby.getInstance(), p);
                                        p.hidePlayer(AdvancedLobby.getInstance(), players);
                                    }
                                }
                                ItemStack silentlobby_active = new ItemBuilder(
                                        AdvancedLobby.getMaterial("hotbar_items.silentlobby.activated.material"),
                                        1, (short) AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.activated.subid"))
                                        .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.activated.displayname"))
                                        .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.activated.lore"));
                                p.getInventory().setItemInMainHand(silentlobby_active);

                                ItemStack hider_none = new ItemBuilder(
                                        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_none.material"), 1,
                                        (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_none.subid"))
                                        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname"))
                                        .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_none.lore"));
                                if (AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled")) {
                                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), hider_none);
                                }

                                for (Player players : Bukkit.getOnlinePlayers()) {
                                    p.hidePlayer(AdvancedLobby.getInstance(), players);
                                    players.hidePlayer(AdvancedLobby.getInstance(), p);
                                }
                                if (Cosmetics.balloons.containsKey(p)) {
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), () -> Cosmetics.balloons.get(p).remove(), 5L);
                                }
                                return;
                            }
                            AdvancedLobby.silentLobby.remove(p);
                            AdvancedLobby.playerHider.remove(p);
                            p.sendMessage(Locale.SILENTLOBBY_LEAVE.getMessage(p));
                            AdvancedLobby.playSound(p, p.getLocation(), "silentlobby.disable_silentlobby");
//                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                            ItemStack silentlobby_deactive = new ItemBuilder(
                                    AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material"), 1,
                                    (short) AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.deactivated.subid"))
                                    .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.deactivated.displayname"))
                                    .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.deactivated.lore"));
                            p.getInventory().setItemInMainHand(silentlobby_deactive);

                            ItemStack hider_all = new ItemBuilder(
                                    AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"), 1,
                                    (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_all.subid"))
                                    .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
                                    .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"));
                            if (AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled")) {
                                p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), hider_all);
                            }

                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (p != players) {
                                    if (!AdvancedLobby.silentLobby.contains(players)) {
                                        if (AdvancedLobby.playerHider.containsKey(players)) {
                                            if (AdvancedLobby.playerHider.get(players) == HiderType.VIP && p.hasPermission("advancedlobby.player_hider.bypass")) {
                                                players.showPlayer(AdvancedLobby.getInstance(), p);
                                            }
                                            if (AdvancedLobby.playerHider.get(players) == HiderType.NONE) {
                                                players.hidePlayer(AdvancedLobby.getInstance(), p);
                                            }
                                            p.showPlayer(AdvancedLobby.getInstance(), players);
                                            return;
                                        } else {
                                            p.showPlayer(AdvancedLobby.getInstance(), players);
                                            players.showPlayer(AdvancedLobby.getInstance(), p);
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }
                    /*
                     * SILENTLOBBY <
                     */

                    /*
                     * SHIELD >
                     */
                    if (item.getType() == AdvancedLobby.getMaterial("hotbar_items.shield.activated.material")
                            | item.getType() == Material
                            .getMaterial(AdvancedLobby.cfg.getString("hotbar_items.shield.deactivated.material"))
                            && item.hasItemMeta() && item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.shield.activated.displayname"))
                            | item.getItemMeta().getDisplayName()
                            .equals(AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.shield.enabled")) {
                            e.setCancelled(true);
                            if (!AdvancedLobby.shield.contains(p)) {
                                AdvancedLobby.shield.add(p);
                                p.sendMessage(Locale.SHIELD_ACTIVATE.getMessage(p));
                                AdvancedLobby.playSound(p, p.getLocation(), "shield.enable_shield");
                                ItemStack shield_active = new ItemBuilder(
                                        AdvancedLobby.getMaterial("hotbar_items.shield.activated.material"), 1,
                                        (short) AdvancedLobby.cfg.getInt("hotbar_items.shield.activated.subid"))
                                        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.activated.displayname"))
                                        .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.activated.lore"));
                                p.getInventory().setItemInMainHand(shield_active);

                                for (Entity entities : p.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
                                    if (entities instanceof Player) {
                                        Player nearbyPlayers = (Player) entities;
                                        if (!nearbyPlayers.hasMetadata("NPC") && !AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(nearbyPlayers)) {
                                            if (!nearbyPlayers.hasPermission("advancedlobby.shield.bypass")) {

                                                Vector nPV = nearbyPlayers.getLocation().toVector();
                                                Vector pV = p.getLocation().toVector();
                                                Vector v = nPV.clone().subtract(pV).normalize().multiply(0.5D).setY(0.25D);
                                                nearbyPlayers.setVelocity(v);

                                                p.playEffect(EntityEffect.TELEPORT_ENDER);

                                                for (Player players : Bukkit.getOnlinePlayers()) {
                                                    if (p != players) {
                                                        if (!AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players)) {
                                                            if (!nearbyPlayers.hasPermission("advancedlobby.shield.bypass")) {
                                                                players.playEffect(EntityEffect.TELEPORT_ENDER);
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }

                                return;
                            }
                            AdvancedLobby.shield.remove(p);
                            p.sendMessage(Locale.SHIELD_DEACTIVATE.getMessage(p));
                            AdvancedLobby.playSound(p, p.getLocation(), "shield.disable_shield");
                            ItemStack shield_deactive = new ItemBuilder(
                                    AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material"), 1,
                                    (short) AdvancedLobby.cfg.getInt("hotbar_items.shield.deactivated.subid"))
                                    .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))
                                    .setLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.deactivated.lore"));
                            p.getInventory().setItemInMainHand(shield_deactive);
                            return;
                        }
                    }
                    /*
                     * SHIELD <
                     */

                    /*
                     * CUSTOM ITEM >
                     */
                    if (item.getType() == AdvancedLobby.getMaterial("hotbar_items.custom_item.material") && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(AdvancedLobby.getString("hotbar_items.custom_item.displayname"))) {
                        if (AdvancedLobby.cfg.getBoolean("hotbar_items.custom_item.enabled")) {
                            e.setCancelled(true);
                            if (AdvancedLobby.cfg.getString("hotbar_items.custom_item.command") != null) {
                                p.performCommand(AdvancedLobby.cfg.getString("hotbar_items.custom_item.command"));
                            }
                            return;
                        }
                    }
                    /*
                     * CUSTOM ITEM <
                     */
                }
            }

        }

    }


}
