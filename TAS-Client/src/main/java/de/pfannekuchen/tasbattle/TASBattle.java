package de.pfannekuchen.tasbattle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.arguments.FloatArgumentType;

import de.pfannekuchen.tasbattle.mixin.accessor.MinecraftAccessor;
import de.pfannekuchen.tasbattle.mixin.accessor.TimerAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

public class TASBattle implements ModInitializer {

	public static class TASServer {
		public String name;
		public String item;
		public String[] text;
		public ResourceLocation location;
		public TASServer(String name, String item, String imageurl, String[] text) {
			this.name = name;
			this.item = item;
			this.text = text;
			this.location = new ResourceLocation("tasbattle", name.toLowerCase().replaceAll(" ", "_") + ".png");
			try {
				Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(NativeImage.read(new URL(imageurl).openStream())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static double scroll = 7;
	public static HashMap<UUID, String> tags = new HashMap<>();
	public static final ResourceLocation CUSTOM_EDITION_RESOURCE_LOCATION = new ResourceLocation("tasbattle", "custom_edition.png");
	public static List<TASServer> servers = new ArrayList<>();
	public static float tickrate = 20f;
	public static HashMap<UUID, ResourceLocation> capes = new HashMap<>();
	public static String gamemode = "";
	public static int max = 0;
	public static int count = 0;
	public static int playerCountPrev = 0;
	public static long time = 0;
	
	@Override
	public void onInitialize() {
		try {
			@SuppressWarnings("resource")
			final File SAVES_DIR = new File(Minecraft.getInstance().gameDirectory, "tasbattle");
			if (SAVES_DIR.exists())
				FileUtils.deleteDirectory(SAVES_DIR);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("tickratechanger", "data"), (player, handler, data, d) -> {
			try {
				onTickratePacket(data.readFloat());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("tickratechanger", "data2"), (player, handler, data, d) -> {
			gamemode = data.readInt() == 0 ? "Skywars" : "FFA";
			count = data.readInt();
			max = data.readInt();
			time = data.readLong();
		});
		ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> {
			try {
				onTickratePacket(20.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(Commands.literal("tickrate").then(Commands.argument("ticks", FloatArgumentType.floatArg()).executes(c -> {
				float tickrate = FloatArgumentType.getFloat(c, "ticks");
				if (Minecraft.getInstance().isLocalServer()) {
					try {
						onTickratePacket(tickrate);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return 1;
			})).executes(c -> {
				System.out.println("Enter a tickrate");
				return 1;
			}));
		});
	}
	
	public static void onTickratePacket(float tickrate) throws Exception {
		((TimerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getTimer()).setMsPerTick(1000F / tickrate);
		TASBattle.tickrate = tickrate;
	}
	
	public static void onGameInitialize() {
		try {
			Minecraft.getInstance().getTextureManager().register(CUSTOM_EDITION_RESOURCE_LOCATION, new DynamicTexture(NativeImage.read(TASBattle.class.getResourceAsStream("edition.png"))));
			/* Read the Supercool TAS Battle Config File from the server! */
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/tasbattle/servers.dat").openStream()));
			int comments = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < comments; i++) reader.readLine();
			int servers = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < servers; i++) {
				String name = reader.readLine().trim();
				String item = reader.readLine().trim();
				String imageurl = reader.readLine().trim();
				TASBattle.servers.add(new TASServer(name, item.toLowerCase(), imageurl, readText(reader)));
				reader.readLine();
			}
			int capes = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < capes; i++) {
				UUID uuid = UUID.fromString(reader.readLine());
				ResourceLocation loc = new ResourceLocation("tasbattle", "cape_" + uuid.toString());
				Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(NativeImage.read(new URL(reader.readLine()).openStream())));
				TASBattle.capes.put(uuid, loc);
			}
			reader.readLine();
			int tags = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < tags; i++) {
				UUID uuid = UUID.fromString(reader.readLine());
				String tag = reader.readLine().replaceAll("&&", "\u00A7");
				TASBattle.tags.put(uuid, tag);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a String array
	 */
	private static String[] readText(BufferedReader reader) throws NumberFormatException, IOException {
		String[] array = new String[Integer.parseInt(reader.readLine().trim())];
		for (int i = 0; i < array.length; i++) array[i] = reader.readLine().trim();
		return array;
	}
	
}
