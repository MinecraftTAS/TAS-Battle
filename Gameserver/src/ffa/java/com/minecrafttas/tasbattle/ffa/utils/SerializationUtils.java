package com.minecrafttas.tasbattle.ffa.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Inventory serialization/deserialization utils
 * @author Pancake
 */
public class SerializationUtils {

	/**
	 * Serialize player inventory
	 *
	 * @param playerInventory Inventory
	 * @param stream Stream
	 * @return Serialized inventory
	 * @throws IOException Filesystem exception
	 */
	public static void serializeInventory(PlayerInventory playerInventory, DataOutputStream stream) throws IOException {
		for (ItemStack item : playerInventory) {
			if (item == null) {
				stream.writeInt(-1);
			} else {
				byte[] buf = item.serializeAsBytes();
				stream.writeInt(buf.length);
				stream.write(buf);
			}
		}
	}
	
	/**
	 * Deserialize player inventory
	 * 
	 * @param playerInventory Inventory
	 * @param stream Stream
	 * @throws IOException Filesystem exception
	 */
	public static void deserializeInventory(PlayerInventory playerInventory, DataInputStream stream) throws IOException {
		try {
			for (int i = 0; i < 99; i++) {
				var len = stream.readInt();
				if (len == -1) {
					playerInventory.setItem(i, null);
					continue;
				}
				var buf = new byte[len];
				stream.read(buf);
				playerInventory.setItem(i, ItemStack.deserializeBytes(buf));
			}
		} catch (EOFException e) {
			// end of file, ignore
		}
	}

}