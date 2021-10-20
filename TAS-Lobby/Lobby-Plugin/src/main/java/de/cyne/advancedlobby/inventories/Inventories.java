package de.cyne.advancedlobby.inventories;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.crossversion.VMaterial;
import de.cyne.advancedlobby.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Inventories {


    /**public static void openAdvacedLobbySettings(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§bAdvacedLobby §8× §7Settings");

        ItemStack weather = new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("§bWeather Settings").setLore(" §8» §7Change Weather Settings");

        inventory.setItem(10, weather);
        p.openInventory(inventory);
    }


    public static void openSettings_Weather(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§bWeather Settings");

        ItemStack lock_weather = new ItemBuilder(Material.BARRIER).setDisplayName("§cLock Weather").setLore(" §8» §7Click to (un-)lock weather change");

        inventory.setItem(10, lock_weather);

        p.openInventory(inventory);
    }**/

    private static Inventory getCompassInventory() {
        Inventory inventory = Bukkit.createInventory(null, AdvancedLobby.getInt("inventories.teleporter.rows") * 9,
                AdvancedLobby.getString("inventories.teleporter.title"));

        for (String item : AdvancedLobby.cfg.getConfigurationSection("inventories.teleporter.items").getKeys(false)) {
            Material material = Material
                    .getMaterial(AdvancedLobby.cfg.getString("inventories.teleporter.items." + item + ".material"));
            String displayName = ChatColor.translateAlternateColorCodes('&',
                    AdvancedLobby.cfg.getString("inventories.teleporter.items." + item + ".displayname"));
            int subid = AdvancedLobby.cfg.getInt("inventories.teleporter.items." + item + ".subid");
            int slot = AdvancedLobby.cfg.getInt("inventories.teleporter.items." + item + ".slot");

            ItemStack itemStack = new ItemBuilder(material, 1, (short) subid).setDisplayName(displayName).setLore(AdvancedLobby.cfg.getStringList("inventories.teleporter.items." + item + ".lore"));

            inventory.setItem(slot, itemStack);
        }
        return inventory;
    }

    public static void openCompassInventory(Player player) {
        player.openInventory(getCompassInventory());
    }

    public static void openCosmetics(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45, AdvancedLobby.getString("inventories.cosmetics.title"));

        ItemStack hats = new ItemBuilder(Material.PUMPKIN)
                .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.hats.displayname"))
                .setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.hats.lore"));

        ItemStack particles = new ItemBuilder(Material.BLAZE_POWDER)
                .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.particles.displayname"))
                .setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.particles.lore"));

        ItemStack gadgets = new ItemBuilder(Material.FISHING_ROD)
                .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.gadgets.displayname"))
                .setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.gadgets.lore"));

        ItemStack balloons = new ItemBuilder(VMaterial.LEAD.getType())
                .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.ballons.displayname"))
                .setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.ballons.lore"));

        /**ItemStack wardrobe = new ItemBuilder(Material.IRON_BOOTS)
         .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.wardrobe.displayname"))
         .setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.wardrobe.lore"));**/

        inventory.setItem(19, hats);
        inventory.setItem(21, particles);
        inventory.setItem(23, balloons);
        inventory.setItem(25, gadgets);

        for (int z = 0; z <= 8; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));

        for (int z = 36; z <= 44; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));


        p.openInventory(inventory);
    }

    public static void openCosmetics_hats(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45,
                AdvancedLobby.getString("inventories.cosmetics_hats.title"));

        ItemStack melon_block = new ItemBuilder(VMaterial.MELON.getType()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.melon_hat.lore"));
        ItemStack tnt = new ItemBuilder(Material.TNT).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.tnt_hat.lore"));
        ItemStack glass = new ItemBuilder(Material.GLASS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.glass_hat.lore"));
        ItemStack sponge = new ItemBuilder(Material.SPONGE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.sponge_hat.lore"));
        ItemStack pumpkin = new ItemBuilder(Material.PUMPKIN).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.pumpkin_hat.lore"));
        ItemStack cactus = new ItemBuilder(Material.CACTUS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.cactus_hat.lore"));

        ItemStack delete = new ItemBuilder(VMaterial.RED_DYE.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.disable.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.disable.lore"));
        ItemStack back = new ItemBuilder(VMaterial.PLAYER_HEAD.toItemStack())
                .setSkullOwner(AdvancedLobby.GO_BACK_SKULL_TEXTURE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.go_back.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.go_back.lore"));

        inventory.setItem(11, melon_block);
        inventory.setItem(12, tnt);
        inventory.setItem(13, glass);
        inventory.setItem(14, sponge);
        inventory.setItem(15, pumpkin);
        inventory.setItem(20, cactus);

        for (int z = 36; z <= 44; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));

        inventory.setItem(40, delete);
        inventory.setItem(44, back);

        p.openInventory(inventory);
    }

    public static void openCosmetics_particles(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45,
                AdvancedLobby.getString("inventories.cosmetics_particles.title"));

        ItemStack redstone = new ItemBuilder(Material.REDSTONE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.heart_particles.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.heart_particles.lore"));
        ItemStack record_10 = new ItemBuilder(VMaterial.MUSIC_DISC_STRAD.getType()).addItemFlags(ItemFlag.HIDE_POTION_EFFECTS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.music_particles.lore"));
        ItemStack fireball = new ItemBuilder(VMaterial.FIRE_CHARGE.getType()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.flames_particles.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.flames_particles.lore"));
        ItemStack emerald = new ItemBuilder(Material.EMERALD).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.villager_particles.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics__particles.villager_particles.lore"));
        ItemStack nether_star = new ItemBuilder(Material.NETHER_STAR).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.rainbow_particles.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.rainbow_particles.lore"));

        ItemStack delete = new ItemBuilder(VMaterial.RED_DYE.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.disable.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.disable.lore"));
        ItemStack back = new ItemBuilder(VMaterial.PLAYER_HEAD.toItemStack())
                .setSkullOwner(AdvancedLobby.GO_BACK_SKULL_TEXTURE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.go_back.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.go_back.lore"));

        inventory.setItem(11, redstone);
        inventory.setItem(12, record_10);
        inventory.setItem(13, fireball);
        inventory.setItem(14, emerald);
        inventory.setItem(15, nether_star);

        for (int z = 36; z <= 44; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));

        inventory.setItem(40, delete);
        inventory.setItem(44, back);

        p.openInventory(inventory);
    }

    public static void openCosmetics_balloons(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45,
                AdvancedLobby.getString("inventories.cosmetics_balloons.title"));

        ItemStack stained_clay_yellow = new ItemBuilder(VMaterial.YELLOW_TERRACOTTA.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.yellow_balloon.lore"));
        ItemStack stained_clay_red = new ItemBuilder(VMaterial.RED_TERRACOTTA.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.red_balloon.lore"));
        ItemStack stained_clay_green = new ItemBuilder(VMaterial.LIME_TERRACOTTA.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.green_balloon.lore"));
        ItemStack stained_clay_blue = new ItemBuilder(VMaterial.LIGHT_BLUE_TERRACOTTA.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.blue_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.blue_balloon.lore"));
        ItemStack hay_block = new ItemBuilder(Material.HAY_BLOCK).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.hay_block_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.hay_block_balloon.lore"));
        ItemStack sea_lantern = new ItemBuilder(Material.SEA_LANTERN).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.sea_lantern_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.sea_lantern_balloon.lore"));
        ItemStack bookshelf = new ItemBuilder(Material.BOOKSHELF).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.bookshelf_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.bookshelf_balloon.lore"));
        ItemStack note_block = new ItemBuilder(Material.NOTE_BLOCK).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.note_block_balloon.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.note_block_balloon.lore"));

        ItemStack delete = new ItemBuilder(VMaterial.RED_DYE.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.disable.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.disable.lore"));
        ItemStack back = new ItemBuilder(VMaterial.PLAYER_HEAD.toItemStack())
                .setSkullOwner(AdvancedLobby.GO_BACK_SKULL_TEXTURE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.go_back.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.go_back.lore"));

        inventory.setItem(11, stained_clay_yellow);
        inventory.setItem(12, stained_clay_red);
        inventory.setItem(13, stained_clay_green);
        inventory.setItem(14, stained_clay_blue);
        inventory.setItem(15, hay_block);
        inventory.setItem(20, sea_lantern);
        inventory.setItem(21, bookshelf);
        inventory.setItem(22, note_block);

        for (int z = 36; z <= 44; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));

        inventory.setItem(40, delete);
        inventory.setItem(44, back);

        p.openInventory(inventory);
    }

    public static void openCosmetics_gadgets(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45,
                AdvancedLobby.getString("inventories.cosmetics_gadgets.title"));

        ItemStack fishing_rod = new ItemBuilder(Material.FISHING_ROD).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.grappling_hook_gadget.lore"));
        ItemStack feather = new ItemBuilder(Material.FEATHER).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.rocket_jump_gadget.lore"));


        ItemStack delete = new ItemBuilder(VMaterial.RED_DYE.toItemStack()).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.disable.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.disable.lore"));
        ItemStack back = new ItemBuilder(VMaterial.PLAYER_HEAD.toItemStack())
                .setSkullOwner(AdvancedLobby.GO_BACK_SKULL_TEXTURE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.go_back.displayname")).setLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.go_back.lore"));

        inventory.setItem(11, fishing_rod);
        inventory.setItem(12, feather);

        for (int z = 36; z <= 44; z++)
            inventory.setItem(z, new ItemBuilder(VMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).setDisplayName("§r"));

        inventory.setItem(40, delete);
        inventory.setItem(44, back);

        p.openInventory(inventory);
    }

    /**public static void openCosmetics_wardrobe(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 45,
                AdvancedLobby.getString("inventories.cosmetics_wardrobe.title"));

        ItemStack melon_block = new ItemBuilder(Material.MELON_BLOCK);
        ItemStack tnt = new ItemBuilder(Material.TNT);
        ItemStack glass = new ItemBuilder(Material.GLASS);
        ItemStack sponge = new ItemBuilder(Material.SPONGE);
        ItemStack hay_block = new ItemBuilder(Material.HAY_BLOCK);
        ItemStack note_block = new ItemBuilder(Material.NOTE_BLOCK);
        ItemStack workbench = new ItemBuilder(Material.WORKBENCH);
        ItemStack bookshelf = new ItemBuilder(Material.BOOKSHELF);
        ItemStack piston = new ItemBuilder(Material.PISTON_BASE);
        ItemStack cactus = new ItemBuilder(Material.CACTUS);

        ItemStack delete = new ItemBuilder(Material.INK_SACK, 1, (short) 1);
        ItemStack back = new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())
                .setSkullOwner(AdvancedLobby.GO_BACK_SKULL_TEXTURE);

        inventory.setItem(11, melon_block);
        inventory.setItem(12, tnt);
        inventory.setItem(13, glass);
        inventory.setItem(14, sponge);
        inventory.setItem(15, hay_block);
        inventory.setItem(20, note_block);
        inventory.setItem(21, workbench);
        inventory.setItem(22, bookshelf);
        inventory.setItem(23, piston);
        inventory.setItem(24, cactus);

        for (int z = 36; z <= 44; z++) {
            inventory.setItem(z, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayName("§r"));
        }

        inventory.setItem(40, delete);
        inventory.setItem(44, back);

        p.openInventory(inventory);
    }**/

}
