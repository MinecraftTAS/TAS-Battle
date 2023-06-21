package com.minecrafttas.tasbattle.mixin.hooks;


import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Disable client recipe book toasts
 * @author Pancake
 */
@Mixin(RecipeToast.class)
public class HookRecipeToast {

    /**
     * Disable recipe book toast
     */
    @Redirect(method = "addOrUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private static void hookHandleRecipeToast(ToastComponent toastComponent, Toast toast) {
        // don't show
    }

}
