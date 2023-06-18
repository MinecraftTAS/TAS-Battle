package com.minecrafttas.tasbattle.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;

/**
 * This mixin replaces the title screen buttons with a tas battle button
 * @author Scribble
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
	protected MixinTitleScreen(Component component) { super(component); }
	
	/**
	 * Replace menu buttons with tas battle
	 * @param height Screen height
	 * @param distanceBtn Distance between buttons
	 */
	@Overwrite
	private void createNormalMenuOptions(int height, int distanceBtn) {
		this.addRenderableWidget(Button.builder(Component.literal("Join TAS Battle"), b -> {
			var address = "mgnet.work";
			if(hasControlDown() && hasShiftDown())
				address = "preview.mgnet.work";
			
			ConnectScreen.startConnecting(this, minecraft, new ServerAddress(address, 25565), new ServerData("MGNetwork", address, false));
		}).bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20).build());
	}
}
