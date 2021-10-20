package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.cosmetics.Cosmetics.ParticleType;
import de.cyne.advancedlobby.crossversion.VMaterial;
import de.cyne.advancedlobby.crossversion.VParticle;
import de.cyne.advancedlobby.misc.HiderType;
import de.cyne.advancedlobby.misc.LocationManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
            /*
             * PARTICLES >
             */
            if (Cosmetics.particles.containsKey(p)) {
                if (Cosmetics.particles.get(p) == ParticleType.HEART) {
                    showParticles(p, "HEART", 2, 0.3F, 0.3F, 0.3F, 1.0F);
                }
                if (Cosmetics.particles.get(p) == ParticleType.MUSIC) {
                    showParticles(p, "NOTE", 3, 0.3F, 0.3F, 0.3F, 1.0F);
                }
                if (Cosmetics.particles.get(p) == ParticleType.FLAMES) {
                    showParticles(p, "FLAME", 4, 0.0F, 0.0F, 0.0F, 0.1F);
                }
                if (Cosmetics.particles.get(p) == ParticleType.VILLAGER) {
                    showParticles(p, "VILLAGER_HAPPY", 4, 0.5F, 0.5F, 0.5F, 1.0F);
                }
                if (Cosmetics.particles.get(p) == ParticleType.RAINBOW) {
                    showParticles(p, "SPELL_MOB", 8, 0.5F, 0.5F, 0.5F, 1.0F);
                }
            }
            /*
             * PARTICLES <
             */

            /*
             * VOID TELEPORT >
             */
            if (AdvancedLobby.cfg.getBoolean("void_teleport.enabled")) {
                if (p.getLocation().getY() < AdvancedLobby.cfg.getDouble("void_teleport.height")) {
                    Location location = LocationManager.getLocation(
                            AdvancedLobby.cfg.getString("spawn_location"));
                    if (location != null) {
                        p.teleport(location);
                    }
                }
            }
            /*
             * VOID TELEPORT <
             */

            /*
             * JUMPPADS >
             */
            if (p.getLocation().getBlock().getType() == VMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.DARK_OAK_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.BIRCH_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.ACACIA_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.JUNGLE_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.OAK_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.SPRUCE_PRESSURE_PLATE.getType() | p.getLocation().getBlock().getType() == VMaterial.STONE_PRESSURE_PLATE.getType() && p.getLocation().subtract(0.0D, 2.0D, 0.0D).getBlock().getType() == Material.REDSTONE_BLOCK) {
                if (AdvancedLobby.cfg.getBoolean("jumppads.enabled")) {
                    Vector vector = p.getLocation().getDirection().multiply(AdvancedLobby.cfg.getDouble("jumppads.lenght")).setY(AdvancedLobby.cfg.getDouble("jumppads.height"));
                    p.setVelocity(vector);

                    AdvancedLobby.playSound(p, p.getLocation(), "jumppads");
                    p.playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);

                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (p != players) {
                            if (!AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players) && !AdvancedLobby.playerHider.containsKey(players)) {
                                AdvancedLobby.playSound(players, p.getLocation(), "jumppads");
                                players.playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                            }
                        }
                    }

                }
            }
            /*
             * JUMPPADS <
             */

            /*
             * SHIELD (PLAYER MOVES) >
             */
            if (AdvancedLobby.shield.contains(p)) {
                for (Entity entities : p.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
                    if (entities instanceof Player) {
                        Player nearbyPlayers = (Player) entities;
                        if (!nearbyPlayers.hasMetadata("NPC") && !AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(nearbyPlayers)) {
                            if (!nearbyPlayers.hasPermission("advancedlobby.shield.bypass")) {

                                Vector nPV = nearbyPlayers.getLocation().toVector();
                                Vector pV = p.getLocation().toVector();
                                Vector v = nPV.clone().subtract(pV).normalize().multiply(0.5D).setY(0.25D);
                                nearbyPlayers.setVelocity(v);

                                p.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);

                                for (Player players : Bukkit.getOnlinePlayers()) {
                                    if (p != players) {
                                        if (!AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players)) {
                                            if (!nearbyPlayers.hasPermission("advancedlobby.shield.bypass")) {
                                                players.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
            /*
             * SHIELD (PLAYER MOVES) <
             */

            /*
             * SHIELD (NEARBY PLAYER MOVES) >
             */
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (p != players) {
                    if (AdvancedLobby.shield.contains(players)) {
                        if (!p.hasMetadata("NPC") && !AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players)) {
                            if (!p.hasPermission("advancedlobby.shield.bypass")) {
                                if (p.getLocation().distance(players.getLocation()) <= 2.5) {

                                    Vector pV = p.getLocation().toVector();
                                    Vector pSV = players.getLocation().toVector();
                                    Vector v = pV.clone().subtract(pSV).normalize().multiply(0.5D).setY(0.25D);

                                    p.setVelocity(v);

                                    p.playEffect(players.getLocation(), Effect.ENDER_SIGNAL, 1);
                                    players.playEffect(players.getLocation(), Effect.ENDER_SIGNAL, 1);

                                    for (Player players1 : Bukkit.getOnlinePlayers()) {
                                        if (p != players1) {
                                            if (!AdvancedLobby.silentLobby.contains(p) && !AdvancedLobby.silentLobby.contains(players)) {
                                                if (!p.hasPermission("advancedlobby.shield.bypass")) {
                                                    players1.playEffect(players.getLocation(), Effect.ENDER_SIGNAL, 1);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
            /*
             * SHIELD (NEARBY PLAYER MOVES) <
             */

            /*
             * WORLDBORDER >
             */
            if (AdvancedLobby.cfg.getBoolean("worldborder.enabled")) {
                Location center_location = LocationManager.getLocation(
                        AdvancedLobby.cfg.getString("worldborder.center_location"));
                if (center_location != null) {
                    if (playerAtWorldBorder(p, center_location)) {
                        if (!AdvancedLobby.build.contains(p)) {

                            Vector lV = center_location.toVector();
                            Vector pV = p.getLocation().toVector();
                            Vector v = lV.clone().subtract(pV).normalize().multiply(0.5D).setY(0.25D);

                            p.setVelocity(v);
                            p.playEffect(p.getLocation(), Effect.SMOKE, 1);
                            AdvancedLobby.playSound(p, p.getLocation(), "worldborder_push_back");

                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (p != players) {
                                    if (!AdvancedLobby.silentLobby.contains(players) && !AdvancedLobby.silentLobby.contains(p)) {
                                        if (!AdvancedLobby.playerHider.containsKey(players)) {
                                            players.playEffect(p.getLocation(), Effect.SMOKE, 1);
                                            AdvancedLobby.playSound(players, p.getLocation(), "worldborder_push_back");
                                        }
                                    }
                                }
                            }

                            Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), () -> {
                                if (playerAtWorldBorder(p, center_location)) {
                                    if (!AdvancedLobby.build.contains(p)) {
                                        Location location = LocationManager.getLocation(
                                                AdvancedLobby.cfg.getString("spawn_location"));
                                        if (location != null) {
                                            p.teleport(location);
                                        }
                                    }
                                }
                            }, 100L);

                        }
                    }
                }
            }
            /*
             * WORLDBORDER <
             */
        }

    }

    private void showParticles(Player player, String particle, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        VParticle.spawnParticle(player, particle, player.getLocation(), count, offsetX, offsetY, offsetZ, extra);
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players != player && !AdvancedLobby.silentLobby.contains(players) && !AdvancedLobby.silentLobby.contains(player) && !(AdvancedLobby.playerHider.get(players) == HiderType.NONE)) {
                VParticle.spawnParticle(players, particle, player.getLocation(), count, offsetX, offsetY, offsetZ, extra);
            }
        }
    }


    private boolean playerAtWorldBorder(Player player, Location location) {
        double radius = AdvancedLobby.cfg.getDouble("worldborder.radius");
        if (player.getLocation().getX() > location.getX() + radius | player.getLocation().getX() < location.getX() - radius | player.getLocation().getZ() > location.getZ() + radius | player.getLocation().getZ() < location.getZ() - radius) {
            return true;
        }
        return false;
    }

}
