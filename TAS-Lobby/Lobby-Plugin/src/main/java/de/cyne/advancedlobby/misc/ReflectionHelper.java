package de.cyne.advancedlobby.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionHelper {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object enumValueOf(Class<?> enumClass, String enumName) {
        return Enum.valueOf((Class<Enum>) enumClass, enumName.toUpperCase());
    }

    public static Class<?> getOBCClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", new Class[]{ReflectionHelper.getNMSClass("Packet")})
                    .invoke(playerConnection, new Object[]{packet});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
