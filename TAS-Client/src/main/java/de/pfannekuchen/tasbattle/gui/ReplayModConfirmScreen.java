package de.pfannekuchen.tasbattle.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.Studio;
import com.replaymod.replaystudio.io.ReplayOutputStream;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.packet.State;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.version.ProtocolVersion;
import com.replaymod.replaystudio.protocol.PacketTypeRegistry;
import com.replaymod.replaystudio.replay.ReplayFile;
import com.replaymod.replaystudio.replay.ReplayMetaData;
import com.replaymod.replaystudio.replay.ZipReplayFile;
import com.replaymod.replaystudio.stream.PacketStream;
import com.replaymod.replaystudio.studio.ReplayStudio;

import de.pfannekuchen.tasbattle.rmfilter.ProgressFilter;
import de.pfannekuchen.tasbattle.rmfilter.SpeedupFilter;
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
	
	public static void convert(File f, BufferedOutputStream out, float tickrate, File temp) throws Exception {
		// Create a Studio
		Studio studio = new ReplayStudio();

		// Load Input File
		ReplayFile replayFileIn = new ZipReplayFile(studio, f);
		ReplayMetaData replayMetaIn = replayFileIn.getMetaData();
		ProtocolVersion replayVersionIn = replayMetaIn.getProtocolVersion();
		PacketStream replayStreamIn = replayFileIn.getPacketData(PacketTypeRegistry.get(replayVersionIn, State.PLAY)).asPacketStream();
		
		// Make Output Stream
		ReplayOutputStream replayStreamOut = new ReplayOutputStream(replayVersionIn, out, null);
		
		// Prepare Stream
		replayStreamIn.start();
		replayStreamIn.addFilter(new ProgressFilter(replayMetaIn.getDuration()));
		replayStreamIn.addFilter(new SpeedupFilter(tickrate), 0, replayMetaIn.getDuration());
		
		// Copy Input File to Output File while applying a Speedup Filter
		PacketData data;
		while ((data = replayStreamIn.next()) != null) {
			replayStreamOut.write(data);
		}
	
		for (PacketData dat : replayStreamIn.end()) {
			replayStreamOut.write(dat);
		}
		
		// Close all streams
		replayStreamOut.close();
		replayFileIn.close();
	}
	
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
					
					long time = System.currentTimeMillis();

					System.out.println("Converting " + f.getName() + "...");
					File temp = new File(minecraft.gameDirectory, "temp");
					temp.mkdir();
					convert(f, new BufferedOutputStream(new FileOutputStream(outFile)), 4.0f, temp);
					
					System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
					Thread.sleep(50);
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
