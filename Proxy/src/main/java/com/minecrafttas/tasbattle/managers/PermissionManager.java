package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

import java.util.Arrays;
import java.util.UUID;

/**
 * Simple permission manager
 * @author Pancake
 */
public class PermissionManager {

    private PermissionProvider permissionProvider;

    /**
     * Initialize Permissions Manager
     * @param plugin Plugin
     */
    public PermissionManager(TASBattleProxy plugin) {
        var server = plugin.getServer();
        var config = plugin.getProperties();

        // register events
        server.getEventManager().register(plugin, this);

        // create permission function
        var admins = Arrays.stream(config.getProperty("admin_uuids").split("\\,")).map(s -> UUID.fromString(s)).toList();
        this.permissionProvider = subject -> {
            if ((subject instanceof Player player && admins.contains(player.getUniqueId())) || subject instanceof ConsoleCommandSource) // grant all permissions to admins and the console
                return PermissionFunction.ALWAYS_TRUE;

            return PermissionFunction.ALWAYS_FALSE;
        };
    }

    /**
     * Update client permissions on permissions setup
     * @param e Permissions setup event
     */
    @Subscribe
    public void onPermissionSetup(PermissionsSetupEvent e) {
        e.setProvider(this.permissionProvider);
    }

}
