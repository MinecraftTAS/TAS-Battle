package de.pfannekuchen.tasbattle.mixin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.pfannekuchen.accountapi.MicrosoftAccount;
import net.minecraft.client.User;
import net.minecraft.client.main.Main;

@Mixin(Main.class)
public class MixinMain {

	@Redirect(method = "main", at = @At(value = "NEW", target = "Lnet/minecraft/client/User;<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/client/User;"))
	private static User redirectMain(String s1, String s2, String s3, String s4) {
		System.setProperty("java.awt.headless", "false");
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		if ("FabricMC".equals(s3)) {
			try {
				File cacheToken = new File(".cacheToken");
				MicrosoftAccount account;
				if (cacheToken.exists()) {
					System.out.println("Using cached token...");
					account = new MicrosoftAccount(new String(Files.readAllBytes(cacheToken.toPath()), StandardCharsets.UTF_8));
				} else {
					System.out.println("Using uncached token...");
					account = new MicrosoftAccount();
					Files.write(cacheToken.toPath(), account.getAccountToken().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
				}
				return new User(account.getUsername(), account.getUuid().toString(), account.getAccessToken(), "MOJANG");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new User(s1, s2, s3, s4);
	}
	
}
