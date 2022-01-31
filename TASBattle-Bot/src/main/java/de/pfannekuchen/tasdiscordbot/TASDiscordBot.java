package de.pfannekuchen.tasdiscordbot;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TASDiscordBot extends ListenerAdapter implements Runnable {
	
	public final JDA jda;
	private final Properties configuration;
	private static final long ID = 922095551493836840L;
	private static final long CID = 922094866341707806L;
	private static ArrayList<Long> availableUsers = new ArrayList<>();
	public static HashMap<String, String> link = new HashMap<>();
	public static HashMap<String, String> connected = new HashMap<>();
	
	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if (event.getCommandString().startsWith("/play")) {
			if (availableUsers.contains(event.getMember().getIdLong())) {
				availableUsers.remove(event.getMember().getIdLong());
				event.reply("You have been removed from the available players list.").complete();
				recreateMessage();
			} else {
				availableUsers.add(event.getMember().getIdLong());
				if (availableUsers.size() == 1) {
					event.reply("You have been added to the available players list. You will be informed if other players want to play too!\n\nCurrently no one else is available").complete();
				} else {
					event.reply("You have been added to the available players list. You will be informed if other players want to play too!\n\nCurrently" + allPings(event.getMember().getIdLong()) + " are available.").complete();
				}
				recreateMessage();
			}
		} else if (event.getCommandString().startsWith("/online")) {
			int count = ProxyServer.getInstance().getOnlineCount();
			String users = "";
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (p.isConnected()) users += "\n" + p.getName();
			}
			event.reply("Currently " + count + " player(s) are on the TAS Battle server" + (count == 0 ? "" : (":" + users))).complete();
		} else if (event.getCommandString().startsWith("/link")) {
			Random r = new Random();
			String random = "";
			for (int i = 0; i < 5; i++) {
				random += (char) (r.nextInt(24) + 'A');
			}
			final String outrandom = random;
			event.reply("To link your discord account to minecraft, type '" + outrandom + "' into the minecraft chat. This will expire in 60 seconds").setEphemeral(true).complete();
			link.put(outrandom, event.getUser().getAsTag());
			new Thread(() -> {
				try {
					Thread.sleep(60000);
					if (link.containsValue(event.getUser().getAsTag()))
						link.remove(outrandom);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		super.onSlashCommand(event);
	}
	
	private static String server = null;
	
	@Override
	public void onGenericMessage(GenericMessageEvent event) {
		TextChannel c = event.getTextChannel();
		c.retrieveMessageById(event.getMessageId()).queue(msg -> {
			if (msg.getAuthor().isBot()) return;
			server = null;
			switch (c.getId()) {
			case "911358374275342357":
				server = "ffa";
				break;
			case "911358339231911947":
				server = "skywars";
				break;
			case "917191353274875954":
				server = "juggernaut";
				break;
			case "930947058674827305":
				server = "knockffa";
				break;
			case "937723699665190973":
				server = "cores";
				break;
			case "937833217086599209":
				server = "survivalgames";
				break;	
			default: 
				break;
			}
			if (server != null) {
				String name = msg.getMember().getEffectiveName();
				String tag = msg.getMember().getUser().getAsTag();
				String content = msg.getContentDisplay();
				ProxyServer.getInstance().getPlayers().forEach(p -> {
					if (p.getServer().getInfo().getName().equals(server)) {
						for (Entry<String, String> entry : connected.entrySet()) {
							if (entry.getValue().equals(tag)) {
								p.sendMessage(new TextComponent("\u00A7f<" + entry.getKey() + "\u00A7f> " + content));
								return;
							}
						}
						p.sendMessage(new TextComponent("\u00A7f<" + name + " not-linked\u00A7f> " + content));
					}
				});
			}
		});
	}
	
	private String allPings(long exclude) {
		String userstring = "";
		for (Long user : availableUsers) {
			if (exclude == user) continue;
			userstring += " <@" + user + ">";
		}
		return userstring;
	}

	public TASDiscordBot(Properties configuration) throws InterruptedException, LoginException {
		this.configuration = configuration;
		final JDABuilder builder = JDABuilder.createDefault(this.configuration.getProperty("token"))
				.setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(this);
		this.jda = builder.build();
		this.jda.awaitReady();
	}

	@Override
	public void run() {
		for (Guild g : jda.getGuilds()) {
			CommandListUpdateAction updater = g.updateCommands();
			updater.addCommands(new CommandData("play", "Show other players that you are ready to play!"));
			updater.addCommands(new CommandData("online", "Show all players that are online on the server"));
			updater.addCommands(new CommandData("link", "Link your Discord account to your Minecraft Account"));
			updater.queue();
		}
		recreateMessage();
	}
	
	public void recreateMessage() {
		EmbedBuilder b = new EmbedBuilder();
		b.setFooter("Last updated " + new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now())));
		
		String userstring = "** **\n\n";
		for (Long user : availableUsers) {
			userstring += " <@" + user + ">";
		}
		
		b.addField("Available Players", "On this list you will find all players that can play right now. Add yourself using /play" + ("** **\n\n".equals(userstring) ? "\n\n*This list is empty* (\u256F\u00B0\u25A1\u00B0\uFF09\u256F\uFE35\u253B\u2501\u253B" : userstring), false);
		jda.getTextChannelById(CID).editMessageById(ID, new MessageBuilder().setEmbeds(b.build()).build()).queue();
	}
	
}
