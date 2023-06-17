package com.minecrafttas.tasbattle.ffa.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minecrafttas.tasbattle.TASBattle.GameMode.CommandHandler;
import com.minecrafttas.tasbattle.ffa.utils.SerializationUtils;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

/**
 * FFA Kit manager
 * @author Pancake
 */
public class KitManager extends LobbyManager implements CommandHandler {

	/**
	 * Kit struct
	 * @author Pancake
	 */
	@Getter
	@AllArgsConstructor
	public static class Kit {
		
		private File file;
		private String name;
		private String[] description;
		private Material material;
		private byte[] kit;
		
		/**
		 * Serialize player inventory into kit
		 * @param inv Player inventory
		 * @throws IOException Byte array stream exception
		 */
		public void serializeKit(PlayerInventory inv) throws IOException {
			var byteStream = new ByteArrayOutputStream();
			SerializationUtils.serializeInventory(inv, new DataOutputStream(byteStream));
			this.kit = byteStream.toByteArray();
			byteStream.close();
		}
		
		/**
		 * Deserialize kit into player inventory
		 * @param inv Player inventory
		 * @throws IOException Byte array stream exception
		 */
		public void deserializeKit(PlayerInventory inv) throws IOException {
			var byteStream = new DataInputStream(new ByteArrayInputStream(this.kit));
			SerializationUtils.deserializeInventory(inv, byteStream);
			byteStream.close();
		}
		
		/**
		 * Save kit to file
		 * @param f File
		 * @throws IOException Filesystem exception
		 */
		public void saveToFile() throws IOException {
			DataOutputStream stream = new DataOutputStream(new FileOutputStream(this.file));
			stream.writeUTF(this.name);
			stream.writeUTF(this.material.toString());
			stream.writeInt(this.description.length);
			for (var line : this.description)
				stream.writeUTF(line);

			stream.write(this.kit);
			stream.close();
		}
		
		/**
		 * Load kit from file
		 * @throws IOException Filesystem exception
		 */
		public static Kit loadFromFile(File file) throws IOException {
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			var name = stream.readUTF();
			var type = Material.valueOf(stream.readUTF());
			
			var description = new String[stream.readInt() + 2];
			description[0] = "§r§70 players voted for this kit";
			description[1] = "";
			for (int i = 2; i < description.length; i++)
				description[i] = "§r§5" + stream.readUTF();

			var kit = stream.readAllBytes();
			stream.close();
			return new Kit(file, name, description, type, kit);
		}
		
	}
	
	public static final File FFA_KITS = new File("/home/tasbattle/preview/default/plugins/TAS-Battle/ffa");
	
	@Getter
	private Inventory inventory;

	@Getter
	private BiMap<Kit, ItemStack> kits;
	
	@Getter
	private Map<Player, Kit> votes;
	
	/**
	 * Initialize kit manager
	 * @param plugin Plugin
	 */
	public KitManager(JavaPlugin plugin) {
		super(plugin);
		this.votes = new HashMap<>();
		this.inventory = Bukkit.createInventory(null, 27, Component.text("Kits"));
		
		// load all kits
		var kitFiles = FFA_KITS.listFiles();
		this.kits = HashBiMap.create(kitFiles.length);
		for (File f : kitFiles) {
			try {
				// create item
				var kit = Kit.loadFromFile(f);
				
				var item = new ItemStack(kit.getMaterial());
				item.editMeta(meta -> {
					meta.displayName(Component.text("§r§f" + kit.getName()));
					meta.lore(Arrays.stream(kit.getDescription()).map(Component::text).toList());
				});
				
				this.kits.put(kit, item);
				this.inventory.addItem(item);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle inventory click and vote for kit
	 * @param e Event
	 */
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		var player = (Player) e.getWhoClicked();
		var item = e.getCurrentItem();

		if (e.getInventory() != this.inventory || !this.isActive() || item == null)
			return;
		
		// find kit clicked
		var kit = this.kits.inverse().get(item);

		if (kit == null)
			return;
		
		// update votes
		this.votes.put(player, kit);
		for (var itemStack : this.inventory.getContents()) {
			if (itemStack == null)
				continue;
			
			var itemKit = this.kits.inverse().get(itemStack);
			
			// calculate votes for item
			var voteCount = 0;
			for (var vote : this.votes.entrySet())
				if (vote.getValue().equals(this.kits.inverse().get(itemStack)))
					voteCount++;
			
			// update first line of lore
			var lore = new ArrayList<>(itemStack.lore());
			lore.set(0, Component.text("§7" + voteCount + " players voted for this kit"));
			itemStack.lore(lore);
			this.kits.put(itemKit, itemStack);
		}
		
		player.sendMessage(Component.text("§b» §7You voted for §a").append(item.getItemMeta().displayName()));
		player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 1f));
		
	}
	
	/**
	 * Handle /ffa command
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp())
			return true;
		
		// print help
		if (args.length == 0) {
			sender.sendMessage(Component.text("§b» §7/ffa save §b<name> <material> <description>"));
			sender.sendMessage(Component.text("§b» §7/ffa delete §b<name>"));
			sender.sendMessage(Component.text("§b» §7/ffa load §b<name>"));
			return true;
		}
		
		if (args[0].equals("save")) {
			// try to find material fragment
			int typeFrag = -1;
			for (int i = 1; i < args.length; i++)
				if (args[i].toLowerCase().startsWith("minecraft:"))
					typeFrag = i;
			
			if (typeFrag == -1) {
				sender.sendMessage(Component.text("§b» §cMaterial must start with \"minecraft:\""));
				return true;
			}
			
			// try to parse material
			var material = Material.matchMaterial(args[typeFrag]);
			if (material == null) {
				sender.sendMessage(Component.text("§b» §cInvalid material"));
				return true;
			}
			
			// try to find kit name and description
			var name = Arrays.stream(Arrays.copyOfRange(args, 1, typeFrag)).collect(Collectors.joining(" "));
			var description = Arrays.stream(Arrays.copyOfRange(args, typeFrag + 1, args.length)).collect(Collectors.joining(" ")).split("\\|");
			
			if (description.length == 0) {
				sender.sendMessage(Component.text("§b» §cInvalid description"));
				return true;
			}
			
			// create kit
			try {
				var file = new File(KitManager.FFA_KITS, name.replace(' ', '_').replace('.', '_').replace('/', '_'));
				var kit = new Kit(file, name, description, material, null);
				kit.serializeKit(((Player) sender).getInventory());
				kit.saveToFile();
			} catch (IOException e) {
				sender.sendMessage(Component.text("§b» §cUnable to write file, check console for stacktrace"));
				e.printStackTrace();
				return true;
			}
			
			sender.sendMessage(Component.text("§b» §aKit successfully saved."));
		} else if (args[0].equals("delete")) {
			var name = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).collect(Collectors.joining("_")).replace('.', '_').replace('/', '_');
			
			if (name.isEmpty())
				return true;
			
			// try to delete kit
			var file = new File(KitManager.FFA_KITS, name);
			if (!file.exists()) {
				sender.sendMessage(Component.text("§b» §cKit not found."));
				return true;
			}

			file.delete();
			sender.sendMessage(Component.text("§b» §cKit successfully deleted."));
		} else if (args[0].equals("load")) {
			var name = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).collect(Collectors.joining("_")).replace('.', '_').replace('/', '_');
			
			if (name.isEmpty())
				return true;
			
			// try to load kit
			try {
				var file = new File(KitManager.FFA_KITS, name);
				var kit = Kit.loadFromFile(file);
				kit.deserializeKit(((Player) sender).getInventory());
			} catch (IOException e) {
				sender.sendMessage(Component.text("§b» §cUnable to read file, check console for stacktrace"));
				e.printStackTrace();
				return true;
			}
			
			sender.sendMessage(Component.text("§b» §aKit successfully loaded."));
		}
		return true;
	}
	
	/**
	 * Handle /ffa tab completion
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return Arrays.asList("save", "load", "delete");
		
		if (args.length == 1)
			return Arrays.asList("save", "load", "delete").stream().filter(i -> i.startsWith(args[0])).toList();
		
		if (args[args.length-1].startsWith("minecraft:"))
			return Arrays.stream(Material.values()).filter(i -> i.toString().startsWith(args[args.length-1].replace("minecraft:", ""))).map(f -> f.toString().toLowerCase()).toList();

		return Arrays.asList("");
	}

	
	@Override
	protected Material getItem() {
		return Material.CHEST;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(""), Component.text("§5The most voted kit will be equipped to"), Component.text("§5all players at the beginning of the game"));
	}

	@Override
	public void interact(Player p) {
		p.openInventory(this.inventory);
	}

	@Override
	protected String getName() {
		return "Vote for a kit";
	}

}