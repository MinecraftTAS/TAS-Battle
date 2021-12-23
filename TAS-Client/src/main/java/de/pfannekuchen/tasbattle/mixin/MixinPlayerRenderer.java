package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public MixinPlayerRenderer(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) { super(context, entityModel, f); }

	@Inject(method = "renderNameTag", at = @At("RETURN"))
	public void renderLabel(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (!TASBattle.tags.containsKey(player.getUUID())) return;
		TextComponent text = new TextComponent(TASBattle.tags.get(player.getUUID())); //Prefix
		
		double d = this.entityRenderDispatcher.distanceToSqr(player);
        poseStack.pushPose();
        if (!(d > 4096.0D)) {
            boolean bl = !player.isShiftKeyDown();
            float f = player.getBbHeight() + 0.49F;
            poseStack.pushPose();
            poseStack.translate(0.0D, (double)f, 0.0D);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(-0.015F, -0.015F, 0.015F);
            Matrix4f matrix4f = poseStack.last().pose();
            @SuppressWarnings("resource")
			float g = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            Font textRenderer = this.getFont();
            float h = (float)(-textRenderer.width(text) / 2);
            textRenderer.drawInBatch(text, h, 15, 553648127, false, matrix4f, multiBufferSource, bl, j, i);
            if (bl) {
                textRenderer.drawInBatch(text, h, 15, -1, false, matrix4f, multiBufferSource, false, 0, i);
            }

            poseStack.popPose();
        }
		poseStack.popPose();
	}
	
}
