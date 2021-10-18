package de.pfannekuchen.tasbattle.mixin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		}, (b, stack, mouseX, mouseY) -> {
			if (mouseX >= (double)b.x && mouseY >= (double)b.y && mouseX < (double)(b.x + b.getWidth()) && mouseY < (double)(b.y + b.getHeight()) && !b.active) 
				this.renderTooltip(stack, this.minecraft.font.split(new TextComponent("Please update TAS Battle."), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		addRenderableWidget(new Button(this.width / 2 - 100, height + distanceBtn, 200, 20, new TextComponent("Speed up Videos"), b -> {
			throw new RuntimeException("How did you..");
		}, (b, stack, mouseX, mouseY) -> {
			if (mouseX >= (double)b.x && mouseY >= (double)b.y && mouseX < (double)(b.x + b.getWidth()) && mouseY < (double)(b.y + b.getHeight())) 
				this.renderTooltip(stack, this.minecraft.font.split(new TextComponent("This feature does not exist yet. Please use LoTAS or your favourite editing software to speed up videos."), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		})).active = false;
	}
	
	@Inject(at = @At("TAIL"), method = "init")
	public void afterInit(CallbackInfo ci) throws MalformedURLException, IOException {
		File f = new File("mods/tasbattle.jar");
		if (f.exists()) {
			long size1 = f.length();
			File temp = File.createTempFile("jar", "file");
			temp.deleteOnExit();
			Files.copy(new URL("https://data.mgnet.work/tasbattle/update.jar").openStream(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			long size2 = temp.length();
			if (size1 == size2) return;
			((Button) children().get(children().size() - 2)).setMessage(new TextComponent("Update and Quit"));
			((Button) children().get(children().size() - 6)).active = false;
		}
	}
	
}
