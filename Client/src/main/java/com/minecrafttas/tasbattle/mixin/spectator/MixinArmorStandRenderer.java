package com.minecrafttas.tasbattle.mixin.spectator;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStandRenderer.class)
public abstract class MixinArmorStandRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T,M> {
    public MixinArmorStandRenderer(EntityRendererProvider.Context context, M entityModel, float f) { super(context, entityModel, f); }

    @Override
    public void render(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (TASBattle.instance.getSpectatingSystem().isShowHUD())
            return;

        super.render(livingEntity, f, g, poseStack, multiBufferSource, i);
    }

}
