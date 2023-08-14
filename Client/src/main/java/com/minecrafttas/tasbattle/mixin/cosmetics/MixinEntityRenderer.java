package com.minecrafttas.tasbattle.mixin.cosmetics;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin expands the nametag renderer by adding custom tags below
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

	@Shadow
	public EntityRenderDispatcher entityRenderDispatcher;
	
	@Shadow
	public Font font;
	
	/**
	 * Render custom tag below username
	 * 
	 * @param player Player
	 * @param component Name
	 * @param poseStack Pose stack
	 * @param multiBufferSource Multi buffer source
	 * @param i Distance
	 * @param ci Callback Info
	 */
	@Inject(method = "renderNameTag", at = @At("RETURN"))
	public void renderLabel(T player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		var tags = TASBattle.instance.getDataSystem().getTags();
		if (!tags.containsKey(player.getUUID()))
			return;
		
		var distance = this.entityRenderDispatcher.distanceToSqr(player);
        if (distance >= 4096) 
        	return;

        var tag = Component.literal(tags.get(player.getUUID()));
        var drawMode = player.isShiftKeyDown() ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH;
        var mc = Minecraft.getInstance();
        var opacity = mc.options.getBackgroundOpacity(0.25F);
        
        // orient pose stack
        var x = -this.font.width(tag) / 2.0f;
        var y = player.getBbHeight() + 0.49F;
        poseStack.pushPose();
        poseStack.translate(0.0f, y, 0.0f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.015f, -0.015f, 0.015f);
        
        // render nametag
        var matrix4f = poseStack.last().pose();
        var color = (int)(opacity * 255.0f) << 24;
        this.font.drawInBatch(tag, x, 15, 0x20FFFFFF, false, matrix4f, multiBufferSource, drawMode, color, i);
        
        if (!player.isShiftKeyDown())
        	this.font.drawInBatch(tag, x, 15, -1, false, matrix4f, multiBufferSource, Font.DisplayMode.NORMAL, 0, i);

        poseStack.popPose();
	}
	
}
