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
import java.util.stream.Stream;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
import com.minecrafttas.tasbattle.TASBattleGameserver.GameMode.CommandHandler;
import com.minecrafttas.tasbattle.ffa.utils.SerializationUtils;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * FFA Kit manager
 * @author Pancake
 */
@Getter
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
			description[0] = "<!italic><gray>0 players voted for this kit</gray>";
			description[1] = "";
			for (int i = 2; i < description.length; i++)
				description[i] = "<!italic><dark_purple>" + stream.readUTF() + "</dark_purple>";

			var kit = stream.readAllBytes();
			stream.close();
			return new Kit(file, name, description, type, kit);
		}
		
	}
	
	public static final File FFA_KITS = new File("/home/tasbattle/default/plugins/TAS-Battle-Gameserver/ffa");
	
	private final Inventory inventory;
	private final BiMap<Kit, ItemStack> kits;
	private final Map<Player, Kit> votes;
	
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
					meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><white>" + kit.getName()));
					meta.lore(Arrays.stream(kit.getDescription()).map(MiniMessage.miniMessage()::deserialize).toList());
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
			lore.set(0, MiniMessage.miniMessage().deserialize("<!italic><gray>" + voteCount + " players voted for this kit</gray>"));
			itemStack.lore(lore);
			this.kits.put(itemKit, itemStack);
		}
		
		player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>You voted for</gray> <green>").append(item.getItemMeta().displayName()));
		player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 1f));
		
	}
	
	/**
	 * Handle /ffa command
	 */
	@Override
	public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!sender.isOp())
			return true;
		
		// print help
		if (args.length == 0) {
			sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>/ffa <green>save <aqua>\\<name> \\<material> \\<description></aqua></green></gray>"));
			sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>/ffa <green>delete <aqua>\\<name></aqua></green></gray>"));
			sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>/ffa <green>load <aqua>\\<name></aqua></green></gray>"));
			return true;
		}

        switch (args[0]) {
            case "save" -> {
                // try to find material fragment
                int typeFrag = -1;
                for (int i = 1; i < args.length; i++)
                    if (args[i].toLowerCase().startsWith("minecraft:"))
                        typeFrag = i;

                if (typeFrag == -1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Material must start with \"minecraft:\"</red>"));
                    return true;
                }

                // try to parse material
                var material = Material.matchMaterial(args[typeFrag]);
                if (material == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Invalid material</red>"));
                    return true;
                }

                // try to find kit name and description
                var name = String.join(" ", Arrays.copyOfRange(args, 1, typeFrag));
                var description = String.join(" ", Arrays.copyOfRange(args, typeFrag + 1, args.length)).split("\\|");

                if (description.length == 0) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Invalid description</red>"));
                    return true;
                }

                // create kit
                try {
                    var file = new File(KitManager.FFA_KITS, name.replace(' ', '_').replace('.', '_').replace('/', '_'));
                    var kit = new Kit(file, name, description, material, null);
                    kit.serializeKit(((Player) sender).getInventory());
                    kit.saveToFile();
                } catch (IOException e) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Unable to write file, check console for stacktrace</red>"));
                    e.printStackTrace();
                    return true;
                }

                sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <green>Kit successfully saved.</green>"));
            }
            case "delete" -> {
                var name = String.join("_", Arrays.copyOfRange(args, 1, args.length)).replace('.', '_').replace('/', '_');

                if (name.isEmpty())
                    return true;

                // try to delete kit
                var file = new File(KitManager.FFA_KITS, name);
                if (!file.exists()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Kit not found.</red>"));
                    return true;
                }

                file.delete();
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Kit successfully deleted.</red>"));
            }
            case "load" -> {
                var name = String.join("_", Arrays.copyOfRange(args, 1, args.length)).replace('.', '_').replace('/', '_');

                if (name.isEmpty())
                    return true;

                // try to load kit
                try {
                    var file = new File(KitManager.FFA_KITS, name);
                    var kit = Kit.loadFromFile(file);
                    kit.deserializeKit(((Player) sender).getInventory());
                } catch (IOException e) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>Unable to read file, check console for stacktrace</red>"));
                    e.printStackTrace();
                    return true;
                }

                sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <green>Kit successfully loaded.</green>"));
            }
        }
		return true;
	}
	
	/**
	 * Handle /ffa tab completion
	 */
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length == 0)
			return Arrays.asList("save", "load", "delete");
		
		if (args.length == 1)
			return Stream.of("save", "load", "delete").filter(i -> i.startsWith(args[0])).toList();
		
		if (args[args.length-1].startsWith("minecraft:"))
			return Arrays.stream(Material.values()).filter(i -> i.toString().startsWith(args[args.length-1].replace("minecraft:", ""))).map(f -> f.toString().toLowerCase()).toList();

		return List.of("");
	}

	
	@Override
	protected Material getItem() {
		return Material.CHEST;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(""), MiniMessage.miniMessage().deserialize("<!italic><dark_purple>The most voted kit will be equipped to</dark_purple>"), MiniMessage.miniMessage().deserialize("<!italic><dark_purple>all players at the beginning of the game</dark_purple>"));
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
