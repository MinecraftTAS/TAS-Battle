package com.minecrafttas.tasbattle.mixin.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;
import java.io.IOException;

/**
 * This mixin replaces the title screen buttons with a tas battle button
 * @author Scribble
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
	protected MixinTitleScreen(Component component) { super(component); }

	@Unique
	private static final String GDPR = """
Dear player,

In compliance with the General Data Protection Regulation (GDPR), we are providing you with this confirmation regarding the processing of your personal data on our Minecraft server.

Data Controller:
The data controller for your personal data on the TAS Battle network is MGNetwork, gamesupport@mgnet.work.

Data Collected:
We may collect and process the following personal data about you:

Player Name: We collect your Minecraft username for identification and authentication purposes.
IP Address: We collect your IP address to help prevent abuse and ensure the security of the server.
Server Interactions: We collect your server interactions (chat interactions, commands executed, etc) for moderation and community interaction purposes.

Purpose of Data Processing:
We process the above-mentioned data solely for the following purposes:

To allow you to access and use the Minecraft server.
To maintain server security and prevent abuse.
To moderate the server and ensure a positive and respectful gaming environment.

Legal Basis:
The legal basis for processing your personal data is our legitimate interest in operating and maintaining the Minecraft server and ensuring its security.

Data Retention:
We will retain your personal data only for as long as necessary to achieve the purposes outlined in this confirmation, or as required by applicable laws and regulations.

Data Sharing:
We do not share your personal data with any third parties except as required by law.

Your Rights:
Under the GDPR, you have the right to access, rectify, erase, restrict, and object to the processing of your personal data. If you wish to exercise any of these rights, please select "Back to main menu" or contact us via email.

Contact Information:
If you have any questions or concerns regarding the processing of your personal data on MGNetwork, please contact us at gamesupport@mgnet.work.

Please review this confirmation and let us know if you agree to the processing of your personal data as described above. If you do not agree, please refrain from using our Minecraft server.

By continuing to use our server, you are providing your explicit consent to the processing of your personal data as outlined in this confirmation.

Thank you for being a part of our gaming community.

Sincerely,
MGNetwork""";

	@Unique
	private static final String SHORT_GDPR = """
By playing on our server you give us permission to store the following information about you:
- Username, UUID, IP Address
- Chat messages, commands executed and more server interactions""";

	/**
	 * Replace menu buttons with tas battle
	 * @param height Screen height
	 * @param distanceBtn Distance between buttons
	 * @reason Replacing main menu buttons
	 * @author Pancake
	 */
	@Overwrite
	private void createNormalMenuOptions(int height, int distanceBtn) {
		this.addRenderableWidget(Button.builder(Component.literal("Join TAS Battle"), b -> {
			var file = new File(this.minecraft.gameDirectory, ".gdpr");
			if (file.exists()) {
				var address = "mgnet.work";
				if(hasControlDown() && hasShiftDown())
					address = "preview.mgnet.work";

				ConnectScreen.startConnecting(this, minecraft, new ServerAddress(address, 25565), new ServerData("MGNetwork", address, false));
				return;
			}


			this.minecraft.setScreen(new ConfirmScreen(bl -> {
				this.minecraft.resizeDisplay();
				if (bl) {
					try {
						ConnectScreen.startConnecting(this, minecraft, new ServerAddress("mgnet.work", 25565), new ServerData("MGNetwork", "mgnet.work", false));
						file.createNewFile();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					var guiScale = this.minecraft.options.guiScale();
					var guiScaleValue = guiScale.get();

					var width = this.minecraft.getWindow().getWidth();
					if (width > 1920)
						guiScale.set(2);
					else
						guiScale.set(1);

					this.minecraft.resizeDisplay();
					this.minecraft.setScreen(new ConfirmScreen(bl2 -> {
						guiScale.set(guiScaleValue);
						this.minecraft.resizeDisplay();

						if (bl2)
							try {
								ConnectScreen.startConnecting(this, minecraft, new ServerAddress("mgnet.work", 25565), new ServerData("MGNetwork", "mgnet.work", false));
								file.createNewFile();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						else
							Runtime.getRuntime().halt(-1);
					}, Component.literal("TAS Battle Privacy Confirmation"), Component.literal(GDPR), Component.literal("Accept and join"), Component.literal("Reject and quit")));
				}
			}, Component.literal("TAS Battle Privacy Confirmation"), Component.literal(SHORT_GDPR), Component.literal("Accept and join"), Component.literal("Show full...")));
		}).bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20).build());
	}
}
