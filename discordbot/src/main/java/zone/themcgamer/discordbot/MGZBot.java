package zone.themcgamer.discordbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import zone.themcgamer.discordbot.command.impl.SetActivityCommand;
import zone.themcgamer.discordbot.command.impl.SuggestCommand;
import zone.themcgamer.discordbot.command.impl.ToggleNewsRoleCommand;
import zone.themcgamer.discordbot.events.GuildMemberJoinQuitListener;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;

@Getter
public class MGZBot {
    @Getter private static MGZBot instance;

    private JDA jda;

    public MGZBot() {
        instance = this;
        long time = System.currentTimeMillis();

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
        commandClientBuilder.addCommand(new ToggleNewsRoleCommand());

        try {
            jda = JDABuilder.createDefault(BotConstants.TOKEN)
                    .setCallbackPool(Executors.newScheduledThreadPool(10))
                    .setActivity(Activity.playing("Booting up..."))
                    .setStatus(OnlineStatus.IDLE)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS)
                    .addEventListeners(
                            commandClientBuilder.build(),
                            new GuildMemberJoinQuitListener(this))
                    .build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println("Done (" + (System.currentTimeMillis() - time) + "ms)!");
    }

    public static void main(String[] args) {
        new MGZBot();
    }
}