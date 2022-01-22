package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

@Mixin(Camera.class)
public abstract class MixinCamera {
	
    @Shadow private boolean initialized;
    @Shadow private BlockGetter level;
    @Shadow private Entity entity;
    @Shadow private boolean detached;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow private float eyeHeight;
    @Shadow private float eyeHeightOld;
    
    @Unique private float localYRot;
    @Unique private boolean isReentry;
    
	@Inject(method = "setup", cancellable = true, at = @At("HEAD"))
    public void setup(BlockGetter blockGetter, Entity __unused, boolean bl, boolean bl2, float f1, CallbackInfo ci) {
		if (__unused != Minecraft.getInstance().player && Minecraft.getInstance().player.isSpectator()) ci.cancel();
		else {
			TASBattle.scroll = 7;
			return;
		}
		this.initialized = true;
		this.level = blockGetter;
		this.detached = bl;
		this.entity = Minecraft.getInstance().player;
		Vec3 otherPlayerPos = __unused.position();
		localYRot = (float) ((Minecraft.getInstance().mouseHandler.xpos()/2.5f) % 360.0f);
		Vec3 pos = otherPlayerPos.add(Vec3.directionFromRotation(0, localYRot).multiply(-5, 0, -5).add(0, TASBattle.scroll, 0));
		Vec3 oldPos = new Vec3(__unused.xo, __unused.yo, __unused.zo).add(Vec3.directionFromRotation(0, localYRot).multiply(-5, 0, -5).add(0, TASBattle.scroll, 0));
		
		double d = otherPlayerPos.x - pos.x;
		double e = otherPlayerPos.y - pos.y;
		double f = otherPlayerPos.z - pos.z;
		double g = Math.sqrt(d * d + f * f);
		float xRot = Mth.wrapDegrees((float)(-(Mth.atan2(e, g) * 57.2957763671875)));
		
		this.setRotation(localYRot, xRot);
		this.setPosition(oldPos.lerp(pos, f1));
	}
	
	@Shadow protected abstract void move(double d, double e, double f);
	@Shadow protected abstract void setRotation(float f, float g);
	@Shadow protected abstract void setPosition(double d, double e, double f);
	@Shadow protected abstract void setPosition(Vec3 vec3);
	@Shadow protected abstract double getMaxZoom(double d);
	
}
