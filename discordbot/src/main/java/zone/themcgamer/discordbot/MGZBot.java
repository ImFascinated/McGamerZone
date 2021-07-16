package zone.themcgamer.discordbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.APIKeyRepository;
import zone.themcgamer.discordbot.command.impl.*;
import zone.themcgamer.discordbot.events.GuildsListener;
import zone.themcgamer.discordbot.events.MainGuildListener;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;

@Getter
public class MGZBot {
    @Getter private static MGZBot instance;

    private JDA jda;

    public MGZBot() {
        instance = this;
        long time = System.currentTimeMillis();

        // Initializing Redis
        new JedisController().start();

        CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setPrefix(BotConstants.PREFIX);
        commandClientBuilder.setActivity(Activity.playing("McGamerZone"));
        commandClientBuilder.setStatus(OnlineStatus.ONLINE);
        commandClientBuilder.setOwnerId(BotConstants.OWNER_ID);
        for (String botAdmin : BotConstants.BOT_ADMINS)
            commandClientBuilder.setCoOwnerIds(botAdmin);
        commandClientBuilder.useHelpBuilder(false);

        commandClientBuilder.addCommand(new SuggestCommand());
        commandClientBuilder.addCommand(new SetActivityCommand());
        commandClientBuilder.addCommand(new InviteCommand());
        commandClientBuilder.addCommand(new MessageCommand());
        commandClientBuilder.addCommand(new EditMessageCommand());
        commandClientBuilder.addCommand(new AddReactionToMessageCommand());
        commandClientBuilder.addCommand(new MemberCountCommand());
        commandClientBuilder.addCommand(new PingCommand());
        commandClientBuilder.addCommand(new StopCommand());
        commandClientBuilder.addCommand(new OnlineCommand());

        try {
            jda = JDABuilder.createDefault(BotConstants.TOKEN)
                    .setCallbackPool(Executors.newScheduledThreadPool(10))
                    .setActivity(Activity.playing("Booting up..."))
                    .setStatus(OnlineStatus.IDLE)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS)
                    .addEventListeners(
                            commandClientBuilder.build(),
                            new GuildsListener(),
                            new MainGuildListener(this))
                    .build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println("Done (" + (System.currentTimeMillis() - time) + ")! For help, type \"help\" or \"?\"\n");
            EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
            embedBuilder.setTitle("Startup");
            embedBuilder.setDescription("The bot was started in "+ (System.currentTimeMillis() - time) + "ms!");
        MessageUtils.sendLogMessage(embedBuilder);
    }

    public static void main(String[] args) {
        new MGZBot();
    }
}