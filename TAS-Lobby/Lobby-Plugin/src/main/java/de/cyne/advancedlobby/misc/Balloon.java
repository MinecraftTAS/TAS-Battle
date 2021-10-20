package de.cyne.advancedlobby.misc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Balloon {

    private Player player;
    private Material material;
    private short subId;
    private FallingBlock fallingBlock;
    private Bat bat;

    public static HashMap<Player, FallingBlock> fallingBlocks = new HashMap<>();
    public static HashMap<Player, Bat> bats = new HashMap<>();

    public Balloon(Player player, Material material) {
        this.player = player;
        this.material = material;
        this.subId = 0;
    }

    public Balloon(Player player, ItemStack itemStack) {
        this.player = player;
        this.material = itemStack.getType();
        this.subId = (short) ((Damageable) itemStack.getItemMeta()).getDamage();
    }

    @SuppressWarnings("deprecation")
    public void create() {
        Location location = player.getLocation();
        location.setYaw(location.getYaw() + 90.0F);
        location.setPitch(-45.0F);
        Vector direction = location.getDirection().normalize();
        location.add(direction.getX() * 1.5D, direction.getY() * 1.5D + 0.5D, direction.getZ() * 1.5D);

        this.bat = (Bat) player.getWorld().spawnEntity(location, EntityType.BAT);

        this.fallingBlock = player.getWorld().spawnFallingBlock(bat.getLocation(), material, (byte) subId);
        this.fallingBlock.setDropItem(false);

        this.bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255));
        this.bat.setCanPickupItems(false);
        this.bat.setLeashHolder(player);
        this.bat.setPassenger(fallingBlock);
        this.bat.setRemoveWhenFarAway(false);

        bats.put(player, bat);
        fallingBlocks.put(player, fallingBlock);
    }

    public void remove() {
        if (fallingBlocks.containsKey(player)) {
            fallingBlocks.get(player).remove();
            fallingBlocks.remove(player);
        }
        if (bats.containsKey(player)) {
            bats.get(player).remove();
            bats.remove(player);
        }
    }

    public FallingBlock getFallingBlock() {
        return this.fallingBlock;
    }

    public Bat getBat() {
        return this.bat;
    }
}
