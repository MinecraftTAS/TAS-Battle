package de.pfannekuchen.tasbattle.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ReplayModConfirmScreen extends Screen {

	private static boolean isWorking = false;
	private MultiLineLabel message = MultiLineLabel.EMPTY;
	private Screen s;
	
	public ReplayModConfirmScreen(Screen s) {
		super(TextComponent.EMPTY);
		this.s = s;
		if (isWorking)
			return;
		new Thread(() -> {
			try {
				isWorking = true;
				System.out.println("Starting new worker thread for converting replays...");
				File recordings = new File(this.minecraft.gameDirectory, "replay_recordings");
				for (File f : recordings.listFiles()) {
					if (f.getName().endsWith("-fix.mcpr")) continue;
					File outFile = new File(f.getParentFile(), f.getName().replace(".mcpr", "-fix.mcpr"));
					if (outFile.exists()) continue;
					
					System.out.println("Converting " + f.getName() + "...");

					// Load file
					final byte[] file = Files.readAllBytes(f.toPath());
					
					// Connect
					Socket client = new Socket("mgnet.work", 6663);
					BufferedInputStream in = new BufferedInputStream(client.getInputStream());
					DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
					
					long time = System.currentTimeMillis();
					
					// Send data
					out.writeFloat(4.0f); // Tickrate
					out.writeInt(file.length); // Data Length
					out.write(file);
					out.flush();
					
					// Read data
					Files.write(outFile.toPath(), in.readAllBytes(), StandardOpenOption.CREATE);
					
					System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
					client.close();
					Thread.sleep(5000);
				}
				System.out.println("Worker thread is done.");
				isWorking = false;
			} catch (Exception e) {
				e.printStackTrace();
				isWorking = false;
			}
		}).start();
	}

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, new TranslatableComponent("Before you can play back your replay file TAS Battle has to convert them into it's own format. This will run in the background. In case of corrupted files, delete the -fix version from replay_recordings/"), this.width - 50);
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableComponent("Back to Title Screen"), button -> this.minecraft.setScreen(new TitleScreen())));
        this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableComponent("Continue"), button -> this.minecraft.setScreen(this.s)));
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.message.renderCentered(poseStack, this.width / 2, 70);
        super.render(poseStack, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
	
}
