package de.pfannekuchen.tasbattle.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import de.pfannekuchen.tasbattle.TASBattle;
import de.pfannekuchen.tasbattle.TASBattle.TASServer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

/**
 * Modified Server List Screen with buttons for TAS Battle
 * @author Pancake
 */
public class TASBattleScreen extends Screen {

	Screen parentScreen;
	
	public TASBattleScreen(Screen screen) {
		super(new TextComponent("TASBattle Screen"));
		this.parentScreen = screen;
	}
	
	List<Button> btns = new ArrayList<>();
	TASServer selected = null;
	
	/**
	 * Adds all TASBattle Widgets
	 */
	@Override
	protected void init() {
		int widthPerBtn = (width - 10) / TASBattle.servers.size();
		int x = 5;
		for (int i = 0; i < TASBattle.servers.size(); i++) {
			int FUCKYOUJAVAIWILLKILLYOU = i;
			btns.add(addRenderableWidget(new Button(x, 35, widthPerBtn, 20, new TextComponent(TASBattle.servers.get(i).name), b -> {
				for (GuiEventListener btnl : new ArrayList<>(children())) if (btns.contains(btnl)) ((Button) btnl).active = true;
				b.active = false;
				/* Add Labels */
				selected = TASBattle.servers.get(FUCKYOUJAVAIWILLKILLYOU);
			})));
			x += widthPerBtn;
		}
		addRenderableWidget(new Button(5, height - 30, 250, 20, new TextComponent("Connect to the server"), b -> {
			String ip = selected.ip;
			String port = 25565 + "";
			if (ip.contains(":")) {
				port = ip.split(":")[1];
				ip = ip.split(":")[0];
			}
			if (selected != null) ConnectScreen.startConnecting(this, minecraft, new ServerAddress(ip, Integer.parseInt(port)), new ServerData(selected.name, selected.ip, false));
		}));
	}
	
	/**
	 * Renders the weird background..
	 */
	@Override
	public void render(PoseStack poseStack, int i, int j, float f) {
		this.renderBackground(poseStack);
		int x0 = 0;
		int x1 = width;
		int y0 = 32;
		int y1 = height;
		
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferBuilder.vertex((double)x0, (double)y1, 0.0D).uv((float)x0 / 32.0F, (float)(y1 + 0) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0D).uv((float)x1 / 32.0F, (float)(y1 + 0) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferBuilder.vertex((double)x1, (double)y0, 0.0D).uv((float)x1 / 32.0F, (float)(y0 + 0) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferBuilder.vertex((double)x0, (double)y0, 0.0D).uv((float)x0 / 32.0F, (float)(y0 + 0) / 32.0F).color(32, 32, 32, 255).endVertex();
		tesselator.end();
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(519);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferBuilder.vertex((double)x0, (double)y0, -100.0D).uv(0.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)(x0 + width), (double)y0, -100.0D).uv((float)width / 32.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)(x0 + width), 0.0D, -100.0D).uv((float)width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)x0, (double)height, -100.0D).uv(0.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)(x0 + width), (double)height, -100.0D).uv((float)width / 32.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)(x0 + width), (double)y1, -100.0D).uv((float)width / 32.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferBuilder.vertex((double)x0, (double)y1, -100.0D).uv(0.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		tesselator.end();
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.vertex((double)x0, (double)(y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
		bufferBuilder.vertex((double)x1, (double)(y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
		bufferBuilder.vertex((double)x1, (double)y0, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferBuilder.vertex((double)x0, (double)y0, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferBuilder.vertex((double)x0, (double)y1, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferBuilder.vertex((double)x1, (double)(y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
		bufferBuilder.vertex((double)x0, (double)(y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
		tesselator.end();

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		/* Label Rendering */
		drawCenteredString(poseStack, font, new TextComponent("Connect to a TASBattle Server"), width / 2, 12, 0xFFFFFF);
		if (selected != null) {
			RenderSystem.setShaderTexture(0, selected.location);
			int imgW = (int) (width / 2.2);
			int imgH = imgW / 16 * 9;
			GuiComponent.blit(poseStack, 5, 64, 0, 0, imgW, imgH, imgW, imgH);
			
			int y = 64;
			int x = imgW + 10;
			for (String img : selected.text) {
				drawString(poseStack, font, new TextComponent(img), x, y, 0xFFFFFF);
				y += 11;
			}
		}
		super.render(poseStack, i, j, f);
	}
	
}
