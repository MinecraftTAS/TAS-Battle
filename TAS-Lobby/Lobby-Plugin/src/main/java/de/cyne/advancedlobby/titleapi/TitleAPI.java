package de.cyne.advancedlobby.titleapi;

import org.bukkit.entity.Player;

public class TitleAPI {

    public static void sendActionBar(Player player, String message) {
//        if (AdvancedLobby.isOneEightVersion()) {
//            try {
//                Object action = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                        .getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
//                Constructor<?> actionConstructor = ReflectionHelper.getNMSClass("PacketPlayOutChat")
//                        .getConstructor(new Class[]{ReflectionHelper.getNMSClass("IChatBaseComponent"), Byte.TYPE});
//                Object packet = actionConstructor.newInstance(new Object[]{action, (byte) 2});
//                ReflectionHelper.sendPacket(player, packet);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            try {
//                Object packet;
//                Class<?> packetPlayOutChatClass = ReflectionHelper.getNMSClass("PacketPlayOutChat");
//
//                Class<?> chatComponentTextClass = ReflectionHelper.getNMSClass("ChatComponentText");
//                Class<?> iChatBaseComponentClass = ReflectionHelper.getNMSClass("IChatBaseComponent");
//                try {
//                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + AdvancedLobby.getVersion() + ".ChatMessageType");
//                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
//                    Object chatMessageType = null;
//                    for (Object obj : chatMessageTypes) {
//                        if (obj.toString().equals("GAME_INFO")) {
//                            chatMessageType = obj;
//                        }
//                    }
//                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
//                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
//                } catch (ClassNotFoundException cnfe) {
//                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
//                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
//                }
//                ReflectionHelper.sendPacket(player, packet);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

    }

    public static void sendTabTitle(Player player, String header, String footer) {
//        if (header == null)
//            header = "";
//        header = ChatColor.translateAlternateColorCodes('&', header);
//
//        if (footer == null)
//            footer = "";
//        footer = ChatColor.translateAlternateColorCodes('&', footer);
//        try {
//            Object tabHeader = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                    .getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
//            Object tabFooter = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                    .getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
//            Constructor<?> titleConstructor = ReflectionHelper.getNMSClass("PacketPlayOutPlayerListHeaderFooter")
//                    .getConstructor();
//            Object packet = titleConstructor.newInstance();
//            try {
//                Field aField = packet.getClass().getDeclaredField("a");
//                aField.setAccessible(true);
//                aField.set(packet, tabHeader);
//                Field bField = packet.getClass().getDeclaredField("b");
//                bField.setAccessible(true);
//                bField.set(packet, tabFooter);
//            } catch (Exception ex) {
//                Field aField = packet.getClass().getDeclaredField("header");
//                aField.setAccessible(true);
//                aField.set(packet, tabHeader);
//                Field bField = packet.getClass().getDeclaredField("footer");
//                bField.setAccessible(true);
//                bField.set(packet, tabFooter);
//            }
//            ReflectionHelper.sendPacket(player, packet);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title,
                                 String subtitle) {
//        try {
//            if (title != null) {
//                Object e = ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES")
//                        .get((Object) null);
//                Object chatTitle = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                        .getMethod("a", new Class[]{String.class})
//                        .invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
//                Constructor<?> subtitleConstructor = ReflectionHelper.getNMSClass("PacketPlayOutTitle")
//                        .getConstructor(new Class[]{
//                                ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
//                                ReflectionHelper.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
//                Object titlePacket = subtitleConstructor
//                        .newInstance(new Object[]{e, chatTitle, fadeIn, stay, fadeOut});
//                ReflectionHelper.sendPacket(player, titlePacket);
//
//                e = ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE")
//                        .get((Object) null);
//                chatTitle = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                        .getMethod("a", new Class[]{String.class})
//                        .invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
//                subtitleConstructor = ReflectionHelper.getNMSClass("PacketPlayOutTitle").getConstructor(
//                        new Class[]{ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
//                                ReflectionHelper.getNMSClass("IChatBaseComponent")});
//                titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle});
//                ReflectionHelper.sendPacket(player, titlePacket);
//            }
//            if (subtitle != null) {
//                Object e = ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES")
//                        .get((Object) null);
//                Object chatSubtitle = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                        .getMethod("a", new Class[]{String.class})
//                        .invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
//                Constructor<?> subtitleConstructor = ReflectionHelper.getNMSClass("PacketPlayOutTitle")
//                        .getConstructor(new Class[]{
//                                ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
//                                ReflectionHelper.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
//                Object subtitlePacket = subtitleConstructor
//                        .newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
//                ReflectionHelper.sendPacket(player, subtitlePacket);
//
//                e = ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE")
//                        .get((Object) null);
//                chatSubtitle = ReflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
//                        .getMethod("a", new Class[]{String.class})
//                        .invoke((Object) null, new Object[]{"{\"text\":\"" + subtitle + "\"}"});
//                subtitleConstructor = ReflectionHelper.getNMSClass("PacketPlayOutTitle")
//                        .getConstructor(new Class[]{
//                                ReflectionHelper.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
//                                ReflectionHelper.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
//                subtitlePacket = subtitleConstructor
//                        .newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
//                ReflectionHelper.sendPacket(player, subtitlePacket);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

}
