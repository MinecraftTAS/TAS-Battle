package de.cyne.advancedlobby.misc;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationManager {

    public static void saveLocation(Location location, String name) {
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float pitch = location.getPitch();
        float yaw = location.getYaw();

        String compact = world + ":" + x + ";" + y + ";" + z + ";" + pitch + ";" + yaw;

        AdvancedLobby.cfgL.set(name, compact);

        try {
            AdvancedLobby.cfgL.save(AdvancedLobby.fileLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLocation(String name) {
        if (AdvancedLobby.cfgL.isSet(name)) {
            AdvancedLobby.cfgL.set(name, null);
            try {
                AdvancedLobby.cfgL.save(AdvancedLobby.fileLocations);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Location getLocation(String name) {
        if (AdvancedLobby.cfgL.isSet(name)) {
            String compact = AdvancedLobby.cfgL.getString(name);

            String world = compact.split(":")[0];
            double x = Double.parseDouble(compact.split(":")[1].split(";")[0]);
            double y = Double.parseDouble(compact.split(";")[1]);
            double z = Double.parseDouble(compact.split(";")[2]);
            float pitch = Float.parseFloat(compact.split(";")[3]);
            float yaw = Float.parseFloat(compact.split(";")[4]);

            Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            return location;
        }
        return null;
    }

}