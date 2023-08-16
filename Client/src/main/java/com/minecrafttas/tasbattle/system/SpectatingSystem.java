package com.minecrafttas.tasbattle.system;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.BitSet;

/**
 * Client-side spectating module
 * @author Scribble
 */
public class SpectatingSystem {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation("spectatingsystem", "data");
	private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
	private static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");

	public enum SpectatorMode {
		FIXED, // forces the player to always look at the spectatingEntity
		ORBIT // forces the position and angle to the spactatingEntity. By moving the mouse, the player can orbit around the spectatingEntity
	}
	
	private Entity spectatedEntity;
	private SpectatorMode mode;
	private int distance = 5;
	@Setter
	private boolean showHUD;
	private GameProfile toBeSpectated;

	/**
	 * Main update loop of the spectator manager
	 * @param cameraEntity Camera entity
	 * @param c Camera
	 * @param f Partial ticks
	 */
	public void onCamera(Entity cameraEntity, Camera c, float f) {
		var entityPos = new Vec3(
				Mth.lerp(f, this.spectatedEntity.xo, this.spectatedEntity.getX()),
				Mth.lerp(f, this.spectatedEntity.yo, this.spectatedEntity.getY()) + this.spectatedEntity.getEyeHeight(),
				Mth.lerp(f, this.spectatedEntity.zo, this.spectatedEntity.getZ())
		);
		var playerX = Mth.lerp(f, cameraEntity.xo, cameraEntity.getX());
		var playerY = Mth.lerp(f, cameraEntity.yo, cameraEntity.getY()) + cameraEntity.getEyeHeight();
		var playerZ = Mth.lerp(f, cameraEntity.zo, cameraEntity.getZ());
        switch (this.mode) {
            case FIXED -> {
				// calculate rotation
                var xOff = entityPos.x - playerX;
                var yOff = entityPos.y - playerY;
                var zOff = entityPos.z - playerZ;
                var y = Math.sqrt(xOff * xOff + zOff * zOff);
                var xRot = Mth.wrapDegrees((float) (-Mth.atan2(yOff, y) * 57.2957763671875));
                var yRot = Mth.wrapDegrees((float) (Mth.atan2(zOff, xOff) * 57.2957763671875) - 90.0f);
				cameraEntity.setXRot(xRot);
				cameraEntity.setYRot(yRot);

				// update rotation
                c.setRotation(yRot, xRot);
                c.setPosition(playerX, playerY, playerZ);
            }
            case ORBIT -> {
				// calculate rotation
                var anglePitch = cameraEntity.getYRot() / 65.0;
                var angleYaw = -cameraEntity.getXRot() / 65.0;
                var posY = this.distance * Math.sin(angleYaw) + Mth.lerp(f, this.spectatedEntity.yo, this.spectatedEntity.getY());
                var hyp = this.distance * Math.cos(angleYaw);
                var posX = hyp * Math.sin(anglePitch) + Mth.lerp(f, this.spectatedEntity.xo, this.spectatedEntity.getX());
                var posZ = hyp * Math.cos(anglePitch) + Mth.lerp(f, this.spectatedEntity.zo, this.spectatedEntity.getZ());
                var xOff2 = entityPos.x - posX;
                var yOff2 = entityPos.y - posY;
                var zOff2 = entityPos.z - posZ;
                var y2 = Math.sqrt(xOff2 * xOff2 + zOff2 * zOff2);
                var xRot2 = Mth.wrapDegrees((float) (-Mth.atan2(yOff2, y2) * 57.2957763671875));
                var yRot2 = Mth.wrapDegrees((float) (Mth.atan2(zOff2, xOff2) * 57.2957763671875) - 90.0f);

				// update rotation
                c.setRotation(yRot2, xRot2);
                c.setPosition(posX, posY, posZ);
            }
        }
	}

	/**
	 * Spectate an entity
	 * @param e Entity
	 */
	public void spectate(Entity e) {
		this.spectatedEntity = e;
		this.mode = e == null ? null : SpectatorMode.FIXED;
	}

	/**
	 * Cycle between spectating modes
	 */
	public void changeMode() {
		if (this.spectatedEntity == null)
			return;

		this.mode = this.mode == SpectatorMode.FIXED ? SpectatorMode.ORBIT : SpectatorMode.FIXED;
		TASBattle.LOGGER.info("Cycling spectate to {}", this.mode);
	}

	/**
	 * Update distance to spectating entity
	 * @param i Scroll amount
	 */
	public void onScroll(int i) {
		this.distance = Math.min(Math.max(this.distance - i, 2), 15);
	}
	
	public boolean isSpectating() {
		return this.mode != null;
	}

	/**
	 * Render spectating system hud
	 */
	public void render(PoseStack poseStack) {
		var mc = Minecraft.getInstance();
		if (!this.showHUD || mc.level == null)
			return;

		// find players
		var players = mc.getConnection().getListedOnlinePlayers().stream().filter(p -> p.getGameMode() == GameType.SURVIVAL).toList();

		// pancake says: i love minecraft commands :)
		// pancake also says: yeah i need to make this serverside

		// check keyboard
		for (int k = 0; k < players.size() + 1; k++) {
			if (KeybindSystem.isKeyDown(mc, GLFW.GLFW_KEY_1 + k)) {
				if (k == 0)
					mc.getConnection().send(new ServerboundChatCommandPacket("spectate", Instant.now(), 0L, ArgumentSignatures.EMPTY, new LastSeenMessages.Update(0, BitSet.valueOf(new long[0]))));
				else
					mc.getConnection().send(new ServerboundChatCommandPacket("spectate " + (this.toBeSpectated = players.get(k - 1).getProfile()).getName(), Instant.now(), 0L, ArgumentSignatures.EMPTY, new LastSeenMessages.Update(0, BitSet.valueOf(new long[0]))));
			}
		}

		// spectate to be spectated player
		if (this.toBeSpectated != null && mc.level.getPlayerByUUID(this.toBeSpectated.getId()) != null) {
			mc.getConnection().send(new ServerboundChatCommandPacket("spectate", Instant.now(), 0L, ArgumentSignatures.EMPTY, new LastSeenMessages.Update(0, BitSet.valueOf(new long[0]))));
			mc.getConnection().send(new ServerboundChatCommandPacket("spectate " + this.toBeSpectated.getName(), Instant.now(), 0L, ArgumentSignatures.EMPTY, new LastSeenMessages.Update(0, BitSet.valueOf(new long[0]))));

			this.toBeSpectated = null;
		}

		// find positions
		int i = (mc.getWindow().getGuiScaledWidth() / 2) - (players.size()+1) * 10 - 1;
		int j = Mth.floor(mc.getWindow().getGuiScaledHeight() - 50.0f);

		// push stack
		poseStack.pushPose();
		poseStack.translate(0.0f, 0.0f, -90.0f);

		// render first icon
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		SpectatorGui.blit(poseStack, i, j, 0, 0, 21, 22);
		var component = Component.literal("1");
		mc.font.drawShadow(poseStack, component, i + 20 - mc.font.width(component), j + 13, 0xFFFFFF);

		// render player icons (except last)
		for (int k = 0; k < players.size() - 1; k++)
			this.renderIcon(mc, poseStack, i, j, k, players.get(k).getProfile(), false);

		// render last player icon
		this.renderIcon(mc, poseStack, i + 1, j, players.size() - 1, players.get(players.size() - 1).getProfile(), true);

		// find slot
		int slot = 0;
		for (int k = 0; k < players.size(); k++)
			if (this.spectatedEntity instanceof Player p && players.get(k).getProfile().getName().equals(p.getGameProfile().getName()))
				slot = k + 1;

		// render slot
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		SpectatorGui.blit(poseStack, i - 1 + slot * 20, j - 1, 0, 22, 24, 24);
		RenderSystem.disableBlend();

		// reset stack
		poseStack.popPose();
	}

	/**
	 * Render player icon
	 * @param mc Minecraft instance
	 * @param poseStack Pose stack
	 * @param x X position
	 * @param y Y position
	 * @param k Player index
	 * @param profile Profile
	 * @param isLast Is last player
	 */
	private void renderIcon(Minecraft mc, PoseStack poseStack, int x, int y, int k, GameProfile profile, boolean isLast) {
		// render background
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		SpectatorGui.blit(poseStack, x + 21 + k * 20, y, isLast ? 162 : 21, 0, 21, 22);

		// translate player head
		poseStack.pushPose();
		poseStack.translate(x + 23 + k * 20 - (isLast ? 1 : 0), y + 3, 0.0f);

		// draw player head
		RenderSystem.setShaderTexture(0, mc.getSkinManager().getInsecureSkinLocation(profile));
		PlayerFaceRenderer.draw(poseStack, 2, 2, 12);

		// reset translate
		poseStack.popPose();

		// draw text
		var component = Component.literal((k+2) + "");
		mc.font.drawShadow(poseStack, component, x + k * 20 + 41 - mc.font.width(component) - (isLast ? 1 : 0), y + 13, 0xFFFFFF);
	}

}
