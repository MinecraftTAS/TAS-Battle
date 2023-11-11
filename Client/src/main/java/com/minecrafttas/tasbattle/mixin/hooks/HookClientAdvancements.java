package com.minecrafttas.tasbattle.mixin.hooks;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Disable client advancement toasts
 * @author Pancake
 */
@Mixin(ClientAdvancements.class)
public class HookClientAdvancements {

    /**
     * Disable advancement toast
     */
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    public void hookHandleAdvancementToast(ToastComponent toastComponent, Toast toast) {
        // don't show
    }

}
