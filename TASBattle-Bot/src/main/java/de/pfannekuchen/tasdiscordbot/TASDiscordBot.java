package de.pfannekuchen.tasdiscordbot;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
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
		}
		super.onSlashCommand(event);
	}
	
	private static String server = null;
	
	@Override
	public void onGenericMessage(GenericMessageEvent event) {
		if (event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor().isBot()) return;
		server = null;
		switch (event.getTextChannel().getId()) {
		case "911358374275342357":
			server = "ffa";
			break;
		case "911358339231911947":
			server = "skywars";
			break;
		case "917191353274875954":
			server = "juggernaut";
			break;
		default: 
			break;
		}
		if (server != null) {
			ProxyServer.getInstance().getPlayers().forEach(p -> {
				if (p.getServer().getInfo().getName().equals(server)) {
					p.sendMessage(new TextComponent("<" + event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().getMember().getEffectiveName() + " #" + server + "> " + event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay()));
				}
			});
		}
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
		
		b.addField("Available Players", "On this list you will find all players that can play right now. Add yourself using /play" + ("** **\n\n".equals(userstring) ? "\n\n*This list is empty* (\u256F°\u25A1°\uFF09\u256F\uFE35\u253B\u2501\u253B" : userstring), false);
		jda.getTextChannelById(CID).editMessageById(ID, new MessageBuilder().setEmbeds(b.build()).build()).queue();
	}
	
}
