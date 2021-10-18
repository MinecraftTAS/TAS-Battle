package de.pfannekuchen.skywars;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Serialize Stuff
 * @author Pancake
 */
public class Serialization {

	/**
	 * Serialized the Player Inventory
	 *
	 * @param playerInventory to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content ]
	 * @throws IllegalStateException
	 */
	public static byte[][] serializeInventory(PlayerInventory playerInventory) throws IllegalStateException {
		//get the main content part, this doesn't return the armor
		byte[] content = serializeItemStack(playerInventory.getContents());
		byte[] additional = serializeItemStack(playerInventory.getExtraContents());
		byte[] armor = serializeItemStack(playerInventory.getArmorContents());
		
		return new byte[][] { content, additional, armor };
	}

	/**
	 * Return the icon name for the kit
	 * @param data data array
	 * @return Kit Icon
	 */
	public static String getIcon(byte[][] data) {
		return new String(data[3], StandardCharsets.US_ASCII);
	}
	
	/**
	 * Deserializes the Player Inventory
	 *
	 * @param playerInventory to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content ]
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static void deserializeInventory(Player p, byte[][] data) throws IllegalStateException, IOException {
		//get the main content part, this doesn't return the armor
		ItemStack[] content = deserializeItemStack(data[0]);
		ItemStack[] additional = deserializeItemStack(data[1]);
		ItemStack[] armor = deserializeItemStack(data[2]);
		p.getInventory().setContents(content);
		p.getInventory().setExtraContents(additional);
		p.getInventory().setArmorContents(armor);
	}

	/**
	 *
	 * Serialized an Item
	 *
	 * @param items to turn into a byte array
	 * @return item serialized
	 * @throws IllegalStateException
	 */
	private static byte[] serializeItemStack(ItemStack[] items) throws IllegalStateException {
		try {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        DataOutputStream stream = new DataOutputStream(outputStream);
	        stream.writeInt(items.length);
	        for (ItemStack item : items) {
	        	if (item == null) {
	        		stream.writeInt(-1);
	        		continue;
	        	}
				byte[] serialized = item.serializeAsBytes();
				stream.writeInt(serialized.length);
				stream.write(serialized);
	        }
	        stream.close();
	        return outputStream.toByteArray();
	    } catch (Exception e) {
	        throw new IllegalStateException("Unable to save item stack.", e);
	    }
	}

	/**
	 * Deserialize an Item
	 *
	 * @param data byte array to convert into item stack
	 * @return ItemStack item
	 * @throws IOException
	 */
	private static ItemStack[] deserializeItemStack(byte[] data) throws IOException {
		try {
	        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
	        DataInputStream stream = new DataInputStream(inputStream);
	        ItemStack[] items = new ItemStack[stream.readInt()];
	        for (int i = 0; i < items.length; i++) {
	        	int len = stream.readInt();
	        	if (len == -1) continue;
	        	byte[] serialized = new byte[len];
	        	stream.read(serialized);
	        	items[i] = ItemStack.deserializeBytes(serialized);
			}
	        stream.close();
	        return items;
	    } catch (Exception e) {
	        throw new IOException("Unable to load item stack.", e);
	    }
	}

	/**
	 * Deserializes all items into a list of Item Stacks
	 * @param data byte array to convert into item stacks
	 * @return ItemStack list
	 * @throws IOException 
	 */
	public static List<ItemStack> getItemStacks(byte[][] data) throws IOException {
		ItemStack[] content = deserializeItemStack(data[0]);
		ItemStack[] additional = deserializeItemStack(data[1]);
		ItemStack[] armor = deserializeItemStack(data[2]);
		List<ItemStack> list = new ArrayList<>();
		list.addAll(Arrays.asList(content));
		list.addAll(Arrays.asList(additional));
		list.addAll(Arrays.asList(armor));
		return list;
	}

}