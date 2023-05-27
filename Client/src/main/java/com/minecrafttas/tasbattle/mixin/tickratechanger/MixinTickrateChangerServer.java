package com.minecrafttas.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.server.MinecraftServer;

/**
 * Changes the tickrate of the server
 * @author Pancake
 *
 */
@Mixin(MinecraftServer.class)
public class MixinTickrateChangerServer {

	@ModifyConstant(method = "runServer", constant = @Constant(longValue = 50L))
	public long runServer(long out) {
		return TASBattle.tickratechanger.getMsPerTick();
	}
	
}
