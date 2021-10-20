package de.cyne.advancedlobby.crossversion;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.cyne.advancedlobby.AdvancedLobby;

public enum VMaterial {

    RED_DYE(1, "INK_SACK"),
    PLAYER_HEAD(3, "SKULL_ITEM"),
    BLACK_STAINED_GLASS_PANE(15, "STAINED_GLASS_PANE"),
    RED_TERRACOTTA(14, "STAINED_CLAY"),
    YELLOW_TERRACOTTA(4, "STAINED_CLAY"),
    LIME_TERRACOTTA(5, "STAINED_CLAY"),
    LIGHT_BLUE_TERRACOTTA(3, "STAINED_CLAY"),
    LEAD("LEASH"),
    MELON("MELON_BLOCK"),
    MUSIC_DISC_STRAD("RECORD_10"),
    FIRE_CHARGE("FIREBALL"),

    CRAFTING_TABLE("WORKBENCH"),
    ENCHANTING_TABLE("ENCHANTMENT_TABLE"),
    DAYLIGHT_DETECTOR("DAYLIGHT_DETECTOR_INVERTED"),
    COMPARATOR("REDSTONE_COMPARATOR", "REDSTONE_COMPARATOR_ON", "REDSTONE_COMPARATOR_OFF"),
    REPEATER("DIODE", "DIODE_BLOCK_ON", "DIODE_BLOCK_OFF"),

    BLACK_BED(15, "BED_BLOCK"),
    BLUE_BED(4, "BED_BLOCK"),
    BROWN_BED(12, "BED_BLOCK"),
    CYAN_BED(9, "BED_BLOCK"),
    GRAY_BED(7, "BED_BLOCK"),
    GREEN_BED(13, " BED_BLOCK"),
    LIGHT_BLUE_BED(3, "BED_BLOCK"),
    LIGHT_GRAY_BED(8, "BED_BLOCK"),
    LIME_BED(5, "BED_BLOCK"),
    MAGENTA_BED(2, "BED_BLOCK"),
    ORANGE_BED(1, "BED_BLOCK"),
    PINK_BED(6, "BED_BLOCK"),
    PURPLE_BED(10, "BED_BLOCK"),
    RED_BED(14, "BED_BLOCK"),
    WHITE_BED("BED_BLOCK"),
    YELLOW_BED(4, "BED_BLOCK"),

    ACACIA_BUTTON("WOOD_BUTTON"),
    BIRCH_BUTTON("WOOD_BUTTON"),
    DARK_OAK_BUTTON("WOOD_BUTTON"),
    JUNGLE_BUTTON("WOOD_BUTTON"),
    OAK_BUTTON("WOOD_BUTTON"),
    SPRUCE_BUTTON("WOOD_BUTTON"),

    ACACIA_PRESSURE_PLATE("WOOD_PLATE"),
    BIRCH_PRESSURE_PLATE("WOOD_PLATE"),
    DARK_OAK_PRESSURE_PLATE("WOOD_PLATE"),
    HEAVY_WEIGHTED_PRESSURE_PLATE("IRON_PLATE"),
    JUNGLE_PRESSURE_PLATE("WOOD_PLATE"),
    LIGHT_WEIGHTED_PRESSURE_PLATE("GOLD_PLATE"),
    OAK_PRESSURE_PLATE("WOOD_PLATE"),
    SPRUCE_PRESSURE_PLATE("WOOD_PLATE"),
    STONE_PRESSURE_PLATE("STONE_PLATE");

    private int subId;
    private String material;
    private String[] legacyMaterial;

    private VMaterial(int subId, String... legacyMaterial) {
        this.subId = subId;
        this.material = name();
        this.legacyMaterial = legacyMaterial;
    }

    private VMaterial(String... legacyMaterial) {
        this(0, legacyMaterial);
    }

    private VMaterial() {
        this(0);
    }

    public Material getType() {
        if (AdvancedLobby.isLegacyVersion()) {
            for (int i = this.legacyMaterial.length - 1; i >= 0; i--) {
                String legacy = this.legacyMaterial[i];

                if (i == 0 && legacy.charAt(1) == '.') return null;

                Material material = Material.getMaterial(legacy);

                if (material != null) return material;
            }
            return null;
        } else {
            return Material.getMaterial(material);
        }
    }

    public short getSubId() {
        return AdvancedLobby.isLegacyVersion() ? (short) subId : 0;
    }

    public ItemStack toItemStack(int amount) {
        return new ItemStack(this.getType(), amount);
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public boolean equals(ItemStack itemStack) {
        return itemStack.getType() == getType();
    }

}
