package com.minecrafttas.tasbattle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.ffa.FFA;
import com.minecrafttas.tasbattle.ffa.utils.SerializationUtils;
import com.minecrafttas.tasbattle.gui.GuiHandler;
import com.minecrafttas.tasbattle.lobby.Lobby;
import com.minecrafttas.tasbattle.lobby.LobbyManager;
import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;

import lombok.Getter;
import net.kyori.adventure.text.Component;

public class TASBattle extends JavaPlugin {
	
	public static interface GameMode {
		abstract void startGameMode(List<Player> players);
		abstract List<LobbyManager> createManagers(JavaPlugin plugin);
	}
	
	@Getter
	private TickrateChanger tickrateChanger;
	@Getter
	private GameMode gameMode;
	@Getter
	private Lobby lobby;
	@Getter
	private boolean developmentMode;
	
	/**
	 * Enable TAS Battle mod
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);
		this.tickrateChanger = new TickrateChanger(this);
		
		// TODO: fix this
		this.developmentMode = true;
//		this.gameMode = new FFA(this);
//		this.lobby = new Lobby(this, this.gameMode);
	}
	
	// FIXME: find a better spot for this
	// TODO: some better error handling
	// TODO: tab completion
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp() || !(sender instanceof Player))
			return true;
		
		// save kit command
		if (command.getName().equals("saveffakit")) {
			
			// print help
			if (args.length == 0) {
				sender.sendMessage(Component.text("§b» §7/saveffakit §b<name> <material> <description>"));
				sender.sendMessage(Component.text("§b» §7- Material must start with \"minecraft:\""));
				sender.sendMessage(Component.text("§b» §7- Description can be split into multiple lines using \"|\""));
				return true;
			}
			
			// try to find material fragment
			int typeFrag = -1;
			for (int i = 0; i < args.length; i++)
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
			var name = Arrays.stream(Arrays.copyOfRange(args, 0, typeFrag)).collect(Collectors.joining(" "));
			var description = Arrays.stream(Arrays.copyOfRange(args, typeFrag + 1, args.length)).collect(Collectors.joining(" ")).split("|");
			
			if (description.length == 0) {
				sender.sendMessage(Component.text("§b» §cInvalid description"));
				return true;
			}
			
			// create snapshot of inventory
			var kit = new File(FFA.FFA_KITS, name.replace(' ', '_').replace('.', '_').replace('/', '_'));
			try {
				DataOutputStream stream = new DataOutputStream(new FileOutputStream(kit));
				stream.writeUTF(material.toString());
				stream.writeUTF(name);
				stream.writeInt(description.length);
				for (var line : description)
					stream.writeUTF(line);

				SerializationUtils.serializeInventory(((Player) sender).getInventory(), stream);
				stream.close();
			} catch (IOException e) {
				sender.sendMessage(Component.text("§b» §cUnable to write file, check console for stacktrace"));
				e.printStackTrace();
			}
			
		} else if (command.getName().equals("loadffakit")) {
			var name = Arrays.stream(args).collect(Collectors.joining("_")).replace('.', '_').replace('/', '_');
			
			if (name.isEmpty())
				return true;
			
			// try to load kit
			var kit = new File(FFA.FFA_KITS, name);
			try {
				DataInputStream stream = new DataInputStream(new FileInputStream(kit));
				stream.readUTF();
				stream.readUTF();
				
				int c = stream.readInt();
				for (int i = 0; i < c; i++)
					stream.readUTF();

				SerializationUtils.deserializeInventory(((Player) sender).getInventory(), stream);
				stream.close();
			} catch (IOException e) {
				sender.sendMessage(Component.text("§b» §cUnable to read file, check console for stacktrace"));
				e.printStackTrace();
			}
		} else if (command.getName().equals("delffakit")) {
			var name = Arrays.stream(args).collect(Collectors.joining("_")).replace('.', '_').replace('/', '_');
			
			if (name.isEmpty())
				return true;
			
			new File(FFA.FFA_KITS, name).delete();
			sender.sendMessage(Component.text("§b» §cKit successfully deleted."));
		}
		
		return true;
	}
	
}
