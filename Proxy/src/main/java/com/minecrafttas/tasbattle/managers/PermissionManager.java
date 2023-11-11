package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.UUID;

/**
 * Simple permission manager
 * @author Pancake
 */
public class PermissionManager {

    /**
     * List of all admins
     */
    private static List<UUID> ADMINS = List.of(
            UUID.fromString("f3112feb-00c1-4de8-9829-53b940342996"),
            UUID.fromString("b8abdafc-5002-40df-ab68-63206ea4c7e8"),
            UUID.fromString("faed0946-bb7f-4fcc-bff1-5fb9ef75a066"),
            UUID.fromString("455798c9-eed4-47f2-aeae-12d071e0ce69"),
            UUID.fromString("06e8982e-608e-4633-bc2b-6ea1af91b052")
    );

    /**
     * Initialize Permissions Manager
     * @param plugin Plugin
     */
    public PermissionManager(TASBattleProxy plugin) {
        // create permission function
        PermissionProvider permissionProvider = subject -> {
            if ((subject instanceof Player player && ADMINS.contains(player.getUniqueId())) || subject instanceof ConsoleCommandSource) // grant all permissions to admins and the console
                return PermissionFunction.ALWAYS_TRUE;

            return PermissionFunction.ALWAYS_FALSE;
        };

        // register event for updating client permission on setup
        plugin.getServer().getEventManager().register(plugin, PermissionsSetupEvent.class, e -> e.setProvider(permissionProvider));
    }

}
