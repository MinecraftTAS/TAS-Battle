package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains( e.getWorld())) {
            if(AdvancedLobby.cfg.getBoolean("weather.lock_weather")) {
                e.setCancelled(true);
            }
        }
    }

}
