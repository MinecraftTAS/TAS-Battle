package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.GameConfig.GameData;
import net.minecraft.client.main.GameConfig.ServerData;
import net.minecraft.client.main.Main;

/**
 * Manipulates the launch parameters of Minecraft
 * @author Pancake
 */
@Mixin(Main.class)
public class MixinLaunchArgs {

	/** Replace RunArgs with new ones that don't have any Session and Auto-Connect. */
	@ModifyArg(method = "main", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;<init>(Lnet/minecraft/client/main/GameConfig;)V"))
	private static GameConfig modifyRunArgs(GameConfig original) {
		return new GameConfig(original.user,
				original.display,
				original.location,
				new GameData(false, "1.16.5-TASBattle", "release", true, false), 
				new ServerData(null, 0));
	}
	
}
