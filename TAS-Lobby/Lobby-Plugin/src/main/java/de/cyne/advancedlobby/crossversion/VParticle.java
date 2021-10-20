package de.cyne.advancedlobby.crossversion;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.misc.ReflectionHelper;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VParticle {

    private static Constructor<?> PACKET_PARTICLE;
    private static Class<?> ENUM_PARTICLE;

    private static Method WORLD_GET_HANDLE;
    private static Method WORLD_SEND_PARTICLE;

    private static Method PLAYER_GET_HANDLE;
    private static Field PLAYER_CONNECTION;
    private static Method SEND_PACKET;

    public static void sendParticle(Object receiver, String particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        try {
            Class<?> packetParticleClass = ReflectionHelper.getNMSClass("PacketPlayOutWorldParticles");
            Class<?> playerClass = ReflectionHelper.getNMSClass("EntityPlayer");
            Class<?> playerConnectionClass = ReflectionHelper.getNMSClass("PlayerConnection");
            Class<?> worldClass = ReflectionHelper.getNMSClass("WorldServer");
            Class<?> entityPlayerClass = ReflectionHelper.getNMSClass("EntityPlayer");

            Class<?> craftPlayerClass = ReflectionHelper.getOBCClass("entity.CraftPlayer");
            Class<?> craftWorldClass = ReflectionHelper.getOBCClass("CraftWorld");

            ENUM_PARTICLE = ReflectionHelper.getNMSClass("EnumParticle");

            PACKET_PARTICLE = packetParticleClass.getConstructor(ENUM_PARTICLE, boolean.class, float.class,
                    float.class, float.class, float.class, float.class, float.class, float.class, int.class,
                    int[].class);
            WORLD_SEND_PARTICLE = worldClass.getDeclaredMethod("sendParticles", entityPlayerClass, ENUM_PARTICLE,
                    boolean.class, double.class, double.class, double.class, int.class, double.class, double.class,
                    double.class, double.class, int[].class);


            WORLD_GET_HANDLE = craftWorldClass.getDeclaredMethod("getHandle");
            PLAYER_GET_HANDLE = craftPlayerClass.getDeclaredMethod("getHandle");
            PLAYER_CONNECTION = playerClass.getField("playerConnection");
            SEND_PACKET = playerConnectionClass.getMethod("sendPacket", ReflectionHelper.getNMSClass("Packet"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        try {
            if (receiver instanceof World) {
                Object worldServer = WORLD_GET_HANDLE.invoke(receiver);
                WORLD_SEND_PARTICLE.invoke(worldServer, null, getEnumParticle(particle), true, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
            } else if (receiver instanceof Player) {
                Object packet = PACKET_PARTICLE.newInstance(getEnumParticle(particle), true, (float) x, (float) y,
                        (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, null);
                Object entityPlayer = PLAYER_GET_HANDLE.invoke(receiver);
                Object playerConnection = PLAYER_CONNECTION.get(entityPlayer);
                SEND_PACKET.invoke(playerConnection, packet);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static Object getEnumParticle(String particleType) {
        return ReflectionHelper.enumValueOf(ENUM_PARTICLE, particleType);
    }

    public static void spawnParticle(Player player, String particle, Location location, int count) {
        if (AdvancedLobby.isOneEightVersion()) {
            VParticle.sendParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, 0.0D, 0.0D, 0.0D, 0.0D);
        } else {
            player.spawnParticle(Particle.valueOf(particle), location, count, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void spawnParticle(Player player, String particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        if (AdvancedLobby.isOneEightVersion()) {
            VParticle.sendParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
        } else {
            player.spawnParticle(Particle.valueOf(particle), location, count, offsetX, offsetY, offsetZ, extra);
        }
    }

}
