package de.pfannekuchen.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinLocalServer {

	@ModifyConstant(method = "runServer", constant = @Constant(longValue = 50L))
	public long runServer(long out) {
		return (long) (1000.0f / TASBattle.tickrate);
	}

}
