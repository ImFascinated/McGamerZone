package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.util.Collections;
import java.util.List;

public class ToggleNewsRoleCommand extends BaseCommand {

    public ToggleNewsRoleCommand() {
        name = "togglenews";
        aliases = new String[]{"news"};
        help = "Turn on or off news role.";
        guildOnly = true;
        guilds = Collections.singletonList(zone.themcgamer.discordbot.guild.Guild.MAIN);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if (member == null)
            return;

        Role newsRole = guild.getRoleById(812440883898875914L);
        if (newsRole == null)
            return;

        if (member.getRoles().contains(newsRole)) {
            guild.removeRoleFromMember(member.getIdLong(), newsRole).queue();
            EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
            embedBuilder.setDescription("You have successfully removed the " + newsRole.getAsMention() + " role from your account!");
            event.reply(embedBuilder.build());
        } else {
            guild.addRoleToMember(member.getIdLong(), newsRole).queue();
            EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
            embedBuilder.setDescription("You have successfully added the " + newsRole.getAsMention() + " role to your account!");
            event.reply(embedBuilder.build());
        }
    }
}
