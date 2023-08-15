package com.minecrafttas.tasbattle.mixin.gui;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Mixin to rewrite spectator gui
 * @author Pancake
 */
@Mixin(SpectatorGui.class)
public class MixinSpectatorGui {

    /**
     * Rewrite spectator gui
     * @author Pancake
     * @reason Rewrite spectator gui
     */
    @Overwrite
    public void renderHotbar(PoseStack poseStack) {
        TASBattle.instance.getSpectatingSystem().render(poseStack);
    }

    /**
     * Rewrite spectator gui
     * @author Pancake
     * @reason Rewrite spectator gui
     */
    @Overwrite
    public void onMouseScrolled(int i) {}

    /**
     * Rewrite spectator gui
     * @author Pancake
     * @reason Rewrite spectator gui
     */
    @Overwrite
    public void onMouseMiddleClick() {}

    /**
     * Rewrite spectator gui
     * @author Pancake
     * @reason Rewrite spectator gui
     */
    @Overwrite
    public void onHotbarSelected(int i) {}

}
