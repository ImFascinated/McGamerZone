package zone.themcgamer.discordbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zone.themcgamer.discordbot.commands.BotStatusCommand;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MGZBot {

    private static final Logger LOG = LoggerFactory.getLogger(MGZBot.class);

    @Getter private static JDA jda;
    @Getter private static CommandClientBuilder commandClientBuilder;
    @Getter private static EventWaiter eventWaiter;
    @Getter private static ScheduledExecutorService executorService;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        eventWaiter = new EventWaiter();

        commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setPrefix(".");
        commandClientBuilder.setActivity(Activity.playing("McGamerZone"));
        commandClientBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        commandClientBuilder.setOwnerId("504069946528104471");
        commandClientBuilder.setCoOwnerIds("504147739131641857");
        commandClientBuilder.setEmojis("<:success:789354594651209738>", "<:warning:789354594877964324>", "<:error:789354595003793408>");
        commandClientBuilder.setAlternativePrefix("/");
        commandClientBuilder.useHelpBuilder(false);
        commandClientBuilder.addCommand(new BotStatusCommand(eventWaiter));

        executorService = Executors.newScheduledThreadPool(10);

        try {
            jda = JDABuilder.createDefault("ODA5NjMxMzcxNzg1Nzk3NjMz.YCX5-Q.t4S8qOmhAc98DKKw9rBsPNv82xM")
                    .setCallbackPool(getExecutorService())
                    .setActivity(Activity.playing("loading..."))
                    .setStatus(OnlineStatus.IDLE)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS)
                    .addEventListeners(eventWaiter,
                            commandClientBuilder.build())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        System.out.println("Done (" + (System.currentTimeMillis() - time) + ")! For help, type \"help\" or \"?\"\n");
    }
}
