package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.command.BaseCommand;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InviteCommand extends BaseCommand {

    public InviteCommand() {
        name = "invite";
        aliases = new String[]{"createinvite"};
        help = "Create invite link via the bot";
        guildOnly = true;
        guilds = Collections.singletonList(zone.themcgamer.discordbot.guild.Guild.MAIN);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        Guild guild = event.getGuild();

        TextChannel textChannelById = guild.getTextChannelById(791015530001596456L);
        if (textChannelById == null)
            return;
        textChannelById.createInvite()
                .timeout(1, TimeUnit.DAYS)
                .setTemporary(true).queue(inviteLink -> {
            event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("I have generated an invite link for you! This invite link will work for 24 hours! " + inviteLink.getUrl()).queue();
                event.reply("Check your dm's!");
            }, error -> {
                event.replyError("Could not sent you a dm!");
            });        }, error -> {
            event.replyError("Coulnd't create an invite link due an error!");
        });

    }
}