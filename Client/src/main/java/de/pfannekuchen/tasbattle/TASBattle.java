package de.pfannekuchen.tasbattle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.NativeImage;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class TASBattle implements ModInitializer {

	public static class TASServer {
		public String name;
		public String ip;
		public String[] text;
		public boolean isGameRunning;
		public ResourceLocation location;
		public TASServer(String name, String ip, String[] text, boolean isRunning, String imageurl) {
			this.name = name;
			this.ip = ip;
			this.text = text;
			this.isGameRunning = isRunning;
			this.location = new ResourceLocation("tasbattle", name.toLowerCase().replaceAll(" ", "_") + ".png");
			try {
				Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(NativeImage.read(new URL(imageurl).openStream())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final ResourceLocation CUSTOM_EDITION_RESOURCE_LOCATION = new ResourceLocation("tasbattle", "custom_edition.png");
	public static List<TASServer> servers;
	
	@Override
	public void onInitialize() { 	
		
	}
	
	public static void onTickratePacket(float tickrate) throws Exception {
		Field timerField = Minecraft.class.getDeclaredField("timer");
		timerField.setAccessible(true);
		Object timer = timerField.get(Minecraft.getInstance());
		Field tickrateField = Timer.class.getDeclaredField("msPerTick");
		tickrateField.setAccessible(true);
		tickrateField.setFloat(timer, tickrate);
	}
	
	public static void onGameInitialize() {
		try {
			Minecraft.getInstance().getTextureManager().register(CUSTOM_EDITION_RESOURCE_LOCATION, new DynamicTexture(NativeImage.read(TASBattle.class.getResourceAsStream("edition.png"))));
			servers = Arrays.asList(
					new TASServer("Anarchy Server", "mgnet.work:5000", new String[] {"The Anarchy Server hosted by Pancake", "running on tickrate 0.5.", "Hacks are not allowed!", "", "There are currently 0 players", "playing on the anarchy server."}, false, "http://mgnet.work/tasbattle_anarchy.jpg"),
					new TASServer("Minigames Server", "mgnet.work:25565", new String[] {"This Server is not available yet."}, false, "http://mgnet.work/tasbattle_anarchy.jpg"),
					new TASServer("FFA Server", "mgnet.work:25565", new String[] {"This Server is not available yet."}, false, "http://mgnet.work/tasbattle_anarchy.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
