package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen{
	
	protected MixinTitleScreen(Component component) { super(component); }
	
	/**
	 * Removes Singleplayer, Multiplayer and Realms Button.
	 * Replaces them with TAS Battle Buttons.
	 */
	@Overwrite
	private void createNormalMenuOptions(int height, int distanceBtn) {
		this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), button -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20).build());
		this.addRenderableWidget(Button.builder(Component.literal("Connect to the server"), b -> {
			if(hasControlDown() && hasShiftDown()) {
				ConnectScreen.startConnecting(this, minecraft, new ServerAddress("preview.mgnet.work", 25565), new ServerData("MGNetwork", "preview.mgnet.work", false));
			}else {
				ConnectScreen.startConnecting(this, minecraft, new ServerAddress("mgnet.work", 25565), new ServerData("MGNetwork", "mgnet.work", false));
			}
			
		}).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build());
		
	}
}
