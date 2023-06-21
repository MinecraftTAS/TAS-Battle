package com.minecrafttas.tasbattle.mixin.gui;

import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    public int changeTicks(int i) {
        return 1;
    }

}
