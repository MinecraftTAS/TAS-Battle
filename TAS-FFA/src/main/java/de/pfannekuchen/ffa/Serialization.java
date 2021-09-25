package de.pfannekuchen.ffa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Serialize Stuff
 * @author Pancake
 */
public class Serialization {
	
	/**
	 * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
	 * 
	 * @param playerInventory to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content ]
	 * @throws IllegalStateException
	 */
	public static byte[][] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
		//get the main content part, this doesn't return the armor
		byte[] content = itemStackArrayToBase64(playerInventory.getContents());
		byte[] additional = itemStackArrayToBase64(playerInventory.getExtraContents());
		byte[] armor = itemStackArrayToBase64(playerInventory.getArmorContents());
		
		return new byte[][] { content, additional, armor };
	}
	
	
	/**
	 * Converts the base 64 array to a Player Inventory. First string is the content and second string is the armor.
	 * 
	 * @param playerInventory to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content ]
	 * @throws IllegalStateException
	 * @throws IOException 
	 */
	public static void base64ToPlayerInventory(Player p, byte[][] data) throws IllegalStateException, IOException {
		//get the main content part, this doesn't return the armor
		ItemStack[] content = itemStackArrayFromBase64(data[0]);
		ItemStack[] additional = itemStackArrayFromBase64(data[1]);
		ItemStack[] armor = itemStackArrayFromBase64(data[2]);
		p.getInventory().setContents(content);
		p.getInventory().setExtraContents(additional);
		p.getInventory().setArmorContents(armor);
	}
	
	/**
	 * 
	 * A method to serialize an {@link ItemStack} array to Base64 String.
	 * 
	 * Based off of {@link #toBase64(Inventory)}.
	 * 
	 * @param items to turn into a Base64 String.
	 * @return Base64 string of the items.
	 * @throws IllegalStateException
	 */
	private static byte[] itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        DataOutputStream stream = new DataOutputStream(outputStream);
	        stream.writeInt(items.length);
	        for (int i = 0; i < items.length; i++) {
	        	if (items[i] == null) {
	        		stream.writeInt(-1);
	        		continue;
	        	}
				byte[] serialized = items[i].serializeAsBytes();
				stream.writeInt(serialized.length);
				stream.write(serialized);
	        }
	        stream.close();
	        return outputStream.toByteArray();
	    } catch (Exception e) {
	        throw new IllegalStateException("Unable to save item stacks.", e);
	    }
	}
	
	/**
	 * Gets an array of ItemStacks from Base64 string.
	 * 
	 * Base off of {@link #fromBase64(String)}.
	 * 
	 * @param data Base64 string to convert to ItemStack array.
	 * @return ItemStack array created from the Base64 string.
	 * @throws IOException
	 */
	private static ItemStack[] itemStackArrayFromBase64(byte[] data) throws IOException {
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
	        throw new IOException("Unable to decode class type.", e);
	    }
	}
	
}