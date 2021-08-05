package de.pfannekuchen.tasbattle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.NativeImage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class TASBattle implements ModInitializer {

	public static class TASServer {
		public String name;
		public String ip;
		public String[] text;
		public ResourceLocation location;
		public TASServer(String name, String ip, String imageurl, String[] text) {
			this.name = name;
			this.ip = ip;
			this.text = text;
			this.location = new ResourceLocation("tasbattle", name.toLowerCase().replaceAll(" ", "_") + ".png");
			try {
				Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(NativeImage.read(new URL(imageurl).openStream())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final ResourceLocation CUSTOM_EDITION_RESOURCE_LOCATION = new ResourceLocation("tasbattle", "custom_edition.png");
	public static List<TASServer> servers = new ArrayList<>();
	public static float tickrate = 20f;
	
	@Override
	public void onInitialize() { 	
		ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("tickratechanger", "data"), (player, handler, data, d) -> {
			try {
				onTickratePacket(data.readFloat());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> {
			try {
				onTickratePacket(20.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void onTickratePacket(float tickrate) throws Exception {
		Field timerField = Minecraft.class.getDeclaredField("timer");
		timerField.setAccessible(true);
		Object timer = timerField.get(Minecraft.getInstance());
		Field tickrateField = Timer.class.getDeclaredField("msPerTick");
		tickrateField.setAccessible(true);
		tickrateField.setFloat(timer, 1000F / tickrate);
		TASBattle.tickrate = tickrate;
	}
	
	public static void onGameInitialize() {
		try {
			Minecraft.getInstance().getTextureManager().register(CUSTOM_EDITION_RESOURCE_LOCATION, new DynamicTexture(NativeImage.read(TASBattle.class.getResourceAsStream("edition.png"))));
			/* Read the Supercool TAS Battle Config File from the server! */
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/TASBATTLE/servers.dat").openStream()));
			int comments = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < comments; i++) reader.readLine();
			int servers = Integer.parseInt(reader.readLine().trim());
			for (int i = 0; i < servers; i++) {
				String name = reader.readLine().trim();
				String id = reader.readLine().trim();
				String imageurl = reader.readLine().trim();
				TASBattle.servers.add(new TASServer(name, id, imageurl, readText(reader)));
				reader.readLine();
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
