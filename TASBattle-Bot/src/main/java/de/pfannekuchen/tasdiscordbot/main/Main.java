package de.pfannekuchen.tasdiscordbot.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import de.pfannekuchen.tasdiscordbot.TASDiscordBot;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {

	@EventHandler
	public void onChat(ChatEvent e) {
		if (e.getMessage().toLowerCase().contains("@everyone") || e.getMessage().toLowerCase().contains("@here")) return;
		if (e.getSender() instanceof ProxiedPlayer) {
			String name = ((ProxiedPlayer) e.getSender()).getName();
			String server = ((ProxiedPlayer) e.getSender()).getServer().getInfo().getName();
			String ch = null;
			switch (server) {
			case "ffa":
				ch = "911358374275342357";
				break;
			case "skywars":
				ch = "911358339231911947";
				break;
			case "juggernaut":
				ch = "917191353274875954";
				break;
			default: 
				break;
			}
			if (ch != null) {
				bot.jda.getTextChannelById(ch).sendMessage("<" + name + "> " + e.getMessage()).queue();
			}
		}
	}
	
	public TASDiscordBot bot;
	
	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		try {
			getDataFolder().mkdirs();
			File propertiesFile = new File(getDataFolder(), "bot.properties");
			/* Load Configuration from File */
			final Properties configuration = new Properties();
			if (!propertiesFile.exists()) loadDefaultConfiguration(propertiesFile);
			configuration.load(new FileInputStream(propertiesFile));
			
			/* Create and run Bot */
			bot = new TASDiscordBot(configuration);
			new Thread(bot).run();
		} catch (LoginException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void loadDefaultConfiguration(File propertiesFile) throws IOException {
		propertiesFile.createNewFile();
		FileOutputStream stream = new FileOutputStream(propertiesFile);
		stream.write("# This is an auto-generated Configuration File. Please set a value for \"token\"\ntoken=".getBytes(Charset.defaultCharset()));
		stream.close();
		System.exit(0);
	}
	
}
