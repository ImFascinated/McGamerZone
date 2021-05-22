package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.HasteBinHandler;

import javax.annotation.Nonnull;
import java.io.IOException;

@RequiredArgsConstructor
public class HasteBinListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        if (member == null)
            return;
        if (member.getUser().isBot())
            return;

        if (event.getMessage().getContentRaw().contains("java.lang.")) {
            HasteBinHandler hasteBinHandler = new HasteBinHandler();
            try {
                String url = hasteBinHandler.post(event.getMessage().getContentDisplay(), false);
                EmbedBuilder embedBuilder = EmbedUtils.warnEmbed();
                embedBuilder.setTitle("Hastebin");
                embedBuilder.setDescription("I've noticed you pasted an error, this error has been pasted in hastebin for you!\n" +
                        "Please use this link and report it at github!");
                embedBuilder.addField("Error", url, false);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
