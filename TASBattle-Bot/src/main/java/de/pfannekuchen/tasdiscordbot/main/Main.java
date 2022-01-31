package de.pfannekuchen.tasdiscordbot.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import de.pfannekuchen.tasdiscordbot.TASDiscordBot;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.chat.TextComponent;
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
			new Thread(() -> {
				String name = ((ProxiedPlayer) e.getSender()).getName();
				String server = ((ProxiedPlayer) e.getSender()).getServer().getInfo().getName();
				String ch = null;
				switch (server) {
				case "ffa":
					ch
					break;
				case "skywars":
					ch
					break;
				case "juggernaut":
					ch
					break;
				case "knockffa":
					ch
					break;
				case "cores":
					ch
					break;
				default: 
					break;
				}
				if (ch != null) {
					String msg = e.getMessage();
					if (TASDiscordBot.link.containsKey(msg)) {
						TASDiscordBot.connected.put(name, TASDiscordBot.link.remove(msg));
						e.setCancelled(true);
						((ProxiedPlayer) e.getSender()).sendMessage(new TextComponent("Your accounts are now linked!"));
						try {
							ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(new File(getDataFolder(), "links.dat")));
							s.writeInt(TASDiscordBot.connected.size());
							for (Entry<String, String> entry : TASDiscordBot.connected.entrySet()) {
								s.writeUTF(entry.getKey());
								s.writeUTF(entry.getValue());
							}
							s.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return;
					}
					if (msg.startsWith("/"))
						return;
					try {
						if (TASDiscordBot.connected.getOrDefault(name, "aaa") != "aaa") {
							Member user = bot.jda.getGuildById(911342411467337728L).getMemberByTag(TASDiscordBot.connected.get(name));
							sendAndRecieveJson(ch, "{\"username\":\"" + user.getEffectiveName() + "\", \"content\": \"" + msg + "\", \"avatar_url\": \"" + user.getEffectiveAvatarUrl() + "\"}", true);
						} else {
							sendAndRecieveJson(ch, "{\"username\":\"Unknown User\", \"content\": \"" + msg + "\", \"avatar_url\": \"https://cdn.discordapp.com/embed/avatars/1.png\"}", true);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}).start();
		}
	}
	
	/**
	 * Method used to send Get or Post Requests to a server, and read the return as a JSON Object.
	 * @param url URL and/or Payload, if payload is null
	 * @param payload Payload that should be send if isPost is true
	 * @param isPost Whether Payload should be send or not
	 * @param headers Additional Headers for the Connection
	 * @return Returns the recieved JSON from the Server
	 * @throws Exception Something went wrong or the Server responded with an Error
	 */
	public static final String sendAndRecieveJson(final String url, final String payload, final boolean isPost, final String... headers) throws Exception {
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
		/* Open a Connection to the Server */
		final URL authServer = new URL(url);
		final HttpClient client = HttpClient.newHttpClient();
		final HttpRequest.Builder con = HttpRequest.newBuilder(authServer.toURI());
		
		/* Set Headers*/
		if (payload != null) con.setHeader("Content-Type", "application/json; utf-8");
		else {
			con.setHeader("Content-Type", "application/x-www-form-urlencoded; utf-8");
		}
		con.setHeader("Accept", "application/json");
		for (int i = 0; i < headers.length; i += 2) con.setHeader(headers[i], headers[i + 1]);
		
		/* Send Payload */
		if (isPost) {
			if (payload != null) {
				// Send the JSON Object as Payload
				con.POST(BodyPublishers.ofByteArray(payload.getBytes("utf-8")));
			} else {
				// Split the URL to payload (which is after the ?) and send that
				con.POST(BodyPublishers.ofByteArray(url.split("\\?", 2)[1].getBytes(StandardCharsets.UTF_8)));
			}
		}
		
		HttpRequest request = con.build();
		/* Read Input from Connection and parse to Json */
		try(final BufferedReader br = new BufferedReader(new InputStreamReader(client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body(), "utf-8"))) {
			String response = "";
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response += responseLine.trim();
			}
			return response;
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
			/* Load Discord Links from FIle */
			if (new File(getDataFolder(), "links.dat").exists()) {
				ObjectInputStream s = new ObjectInputStream(new FileInputStream(new File(getDataFolder(), "links.dat")));
				TASDiscordBot.connected = new HashMap<>();
				int size = s.readInt();
				for (int i = 0; i < size; i++) {
					String key = s.readUTF();
					TASDiscordBot.connected.put(key, s.readUTF());
				}
				s.close();
			}
			
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
