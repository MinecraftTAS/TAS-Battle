package de.pfannekuchen.tasbattle.mixin;

import java.io.File;
import java.nio.file.Path;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Modifies the saves/ folder
 * @author Pancake
 */
@Mixin(LevelStorageSource.class)
public class MixinLevelStorageSource {

	@Shadow @Final @Mutable
	Path baseDir;
	
	@SuppressWarnings("resource")
	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(CallbackInfo ci) {
		File newSavesDir = new File(Minecraft.getInstance().gameDirectory, "tasbattle");
		newSavesDir.mkdir();
		baseDir = newSavesDir.toPath();
	}
	
}
