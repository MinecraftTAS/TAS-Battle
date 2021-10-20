package de.cyne.advancedlobby.locale;

import org.bukkit.entity.Player;

import de.cyne.advancedlobby.AdvancedLobby;
import net.md_5.bungee.api.ChatColor;

public enum Locale {

    PREFIX("prefix"),
    NO_PERMISSION("no_permission"),
    UNKNOWN_COMMAND("unknown_command"),
    PLAYER_NOT_FOUND("player_not_found"),

    JOIN_MESSAGE("join_message"),
    QUIT_MESSAGE("quit_message"),

    COMPASS_LOC_NOT_FOUND("teleporter.location_not_found"),
    COMPASS_LOC_NOT_FOUND_ADMIN("teleporter.location_not_found_admin"),

    HIDER_SHOW_ALL("player_hider.show_all"),
    HIDER_SHOW_VIP("player_hider.show_vip"),
    HIDER_SHOW_NONE("player_hider.show_none"),

    SILENTLOBBY_CHAT_BLOCKED("silentlobby.chat_blocked"),
    SILENTLOBBY_FUNCTION_BLOCKED("silentlobby.function_blocked"),
    SILENTLOBBY_JOIN("silentlobby.join"),
    SILENTLOBBY_LEAVE("silentlobby.leave"),

    SHIELD_ACTIVATE("shield.activate"),
    SHIELD_DEACTIVATE("shield.deactivate"),

    GLOBALMUTE_CHAT_BLOCKED("globalmute_chat_blocked"),

    COMMAND_BUILD_JOIN("commands.build.join"),
    COMMAND_BUILD_LEAVE("commands.build.leave"),

    COMMAND_CHATCLEAR_GLOBAL("commands.chatclear.global"),

    COMMAND_FLY_USAGE("commands.fly.usage"),
    COMMAND_FLY_ENABLE("commands.fly.enable"),
    COMMAND_FLY_DISABLE("commands.fly.disable"),
    COMMAND_FLY_ENABLE_OTHER("commands.fly.enable_other"),
    COMMAND_FLY_DISABLE_OTHER("commands.fly.disable_other"),

    COMMAND_GAMEMODE_USAGE("commands.gamemode.usage"),
    COMMAND_GAMEMODE_SWITCH("commands.gamemode.switch"),
    COMMAND_GAMEMODE_SWITCH_OTHER("commands.gamemode.switch_other"),

    COMMAND_GLOBALMUTE_ENABLE("commands.globalmute.enable"),
    COMMAND_GLOBALMUTE_DISABLE("commands.globalmute.disable"),

    COMMAND_LOBBY_ALREADY_IN_LOBBY("commands.lobby.already_in_lobby"),
    COMMAND_LOBBY_TELEPORT("commands.lobby.teleport"),

    COMMAND_TELEPORTALL_TELEPORT("commands.teleportall.teleport"),

    COSMETICS_HATS_EQUIP("cosmetics.hats.equip"),
    COSMETICS_HATS_NO_PERMISSION("cosmetics.hats.no_permission"),
    COSMETICS_HATS_DISABLE("cosmetics.hats.disable"),
    COSMETICS_HATS_DISABLE_ERROR("cosmetics.hats.disable_error"),

    COSMETICS_PARTICLES_EQUIP("cosmetics.particles.equip"),
    COSMETICS_PARTICLES_NO_PERMISSION("cosmetics.particles.no_permission"),
    COSMETICS_PARTICLES_DISABLE("cosmetics.particles.disable"),
    COSMETICS_PARTICLES_DISABLE_ERROR("cosmetics.particles.disable_error"),

    COSMETICS_BALLOONS_EQUIP("cosmetics.balloons.equip"),
    COSMETICS_BALLOONS_NO_PERMISSION("cosmetics.balloons.no_permission"),
    COSMETICS_BALLOONS_DISABLE("cosmetics.balloons.disable"),
    COSMETICS_BALLOONS_DISABLE_ERROR("cosmetics.balloons.disable_error"),

    COSMETICS_GADGETS_EQUIP("cosmetics.gadgets.equip"),
    COSMETICS_GADGETS_NO_PERMISSION("cosmetics.gadgets.no_permission"),
    COSMETICS_GADGETS_DISABLE("cosmetics.gadgets.disable"),
    COSMETICS_GADGETS_DISABLE_ERROR("cosmetics.gadgets.disable_error");


    private String path;

    private Locale(String path) {
        this.path = path;
    }

    public String getMessage(Player player) {
        String message = AdvancedLobby.cfgM.getString(path).replace("%prefix%", PREFIX.getDefaultMessage());
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getDefaultMessage() {
        return ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfgM.getString(path));
    }

}
