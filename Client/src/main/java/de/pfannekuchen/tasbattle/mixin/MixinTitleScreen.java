package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import de.pfannekuchen.tasbattle.gui.TASBattleScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	
	protected MixinTitleScreen(Component component) { super(component); }

	/**
	 * Modifies the Texture that is being rendered below the Title, aka "Java Edition"
	 */
// TODO: @Mumfrey, fix this :D
//	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 2), index = 1)
//	private ResourceLocation modifyJavaEditionLabel(ResourceLocation original) {
//		return TASBattle.CUSTOM_EDITION_RESOURCE_LOCATION;
//	}
	
	/**
	 * Removes Singleplayer, Multiplayer and Realms Button.
	 * Replaces them with TAS Battle Buttons.
	 */
	@Overwrite
	private void createNormalMenuOptions(int height, int distanceBtn) {
		addRenderableWidget(new Button(this.width / 2 - 100, height, 200, 20, new TextComponent("Join TAS Battle"), b -> {
			minecraft.setScreen(new TASBattleScreen(this));
		}));
		addRenderableWidget(new Button(this.width / 2 - 100, height + distanceBtn, 200, 20, new TextComponent("Speed up Videos"), b -> {
			throw new RuntimeException("How did you..");
		}, (b, stack, mouseX, mouseY) -> {
			if (mouseX >= (double)b.x && mouseY >= (double)b.y && mouseX < (double)(b.x + b.getWidth()) && mouseY < (double)(b.y + b.getHeight())) 
				this.renderTooltip(stack, this.minecraft.font.split(new TextComponent("This feature does not exist yet. Please use LoTAS or your favourite editing software to speed up videos."), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		})).active = false;
	}
	
}
