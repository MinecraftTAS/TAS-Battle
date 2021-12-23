package de.pfannekuchen.tasbattle.gui;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.lingala.zip4j.ZipFile;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Singleplayer screen that downloads TAS Challenges
 * @author Pancake
 */
public class TASChallengesScreen extends SelectWorldScreen {

	String[] PROGRESS_BAR_STAGES = new String[] { "oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO" };
	public static AtomicBoolean isDownloading = new AtomicBoolean(true);

	public TASChallengesScreen() {
		super(null);
		this.minecraft = Minecraft.getInstance();
		if (isDownloading.get()) new Thread(() -> {
			try {
				Thread.sleep(2000);
				final File SAVES_DIR = new File(minecraft.gameDirectory, "tasbattle");
				if (SAVES_DIR.exists())
					FileUtils.deleteDirectory(SAVES_DIR);
				SAVES_DIR.mkdir();
				
				Files.copy(new URL("https://data.mgnet.work/tasbattle/maps/maps.txt").openStream(), new File(minecraft.gameDirectory, "tasbattle_maps.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
				List<String> maps = Files.readAllLines(new File(minecraft.gameDirectory, "tasbattle_maps.txt").toPath());
				for (String s : maps) {
					URL url = new URL("https://data.mgnet.work/tasbattle/maps/" + s + ".zip");
					File out = new File(SAVES_DIR, s);
					File _temp = File.createTempFile("tasbattle", "zip");
					Files.copy(url.openStream(), _temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
					ZipFile file = new ZipFile(_temp);
					file.extractAll(out.getAbsolutePath());
					file.close();
					_temp.delete();
				}
				isDownloading.set(false);
				searchBox.setMaxLength(32);
			} catch (Exception e) {
				e.printStackTrace();
				minecraft.stop();
			}
		}).start();
	}

	public static boolean redoSaves = false;
	
	@Override
	protected void init() {
		super.init();
		children().forEach(c -> {
			if (c instanceof Button) {
				((Button) c).active = false;
				((Button) c).visible = false;
				if (((Button) c).getMessage() instanceof TranslatableComponent) {
					if (((Button) c).getMessage().getString().equals(I18n.get("selectWorld.select"))) {
						((Button) c).visible = true;
						((Button) c).setWidth(200);
						((Button) c).x = this.width / 2 - 100;
					} else if (((Button) c).getMessage().getString().equals(CommonComponents.GUI_CANCEL.getString())) {
						((Button) c).visible = true;
						((Button) c).active = true;
						((Button) c).setWidth(200);
						((Button) c).x = this.width / 2 - 100;
						
					}  
				}
			}
		});
	}
	
	@Override
	public void render(PoseStack matrices, int i, int j, float f) {
		if (isDownloading.get()) {
			this.renderBackground(matrices);
			drawCenteredString(matrices, minecraft.font, new TextComponent("Downloading TAS Maps"), this.width / 2, this.height / 2, 16777215);
			String var10002 = PROGRESS_BAR_STAGES[(int) (Util.getMillis() / 150L % (long) PROGRESS_BAR_STAGES.length)];
			int var10003 = this.width / 2;
			int var10004 = this.height / 2;
			drawCenteredString(matrices, minecraft.font, var10002, var10003, var10004 + 9 * 2, 16777215);
			return;
		} else if (!redoSaves) {
			redoSaves = true;
			this.minecraft.setScreen(new TASChallengesScreen());
		}
		super.render(matrices, i, j, f);
	}
}
