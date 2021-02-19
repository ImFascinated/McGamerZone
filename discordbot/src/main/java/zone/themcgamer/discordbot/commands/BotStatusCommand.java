package zone.themcgamer.discordbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Date;

public class BotStatusCommand extends Command {

    public BotStatusCommand(EventWaiter waiter) {
        this.name = "botstatus";
        //this.aliases = new String[]{"bot"};
        this.help = "view status of thee bot.";
        this.ownerCommand = true;
        this.guildOnly = false;
        this.category = new Category("Administration");
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        String title = ":information_source: Stats of **"+ commandEvent.getJDA().getSelfUser().getName()+"**:";
        String os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getName();
        String arch = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getArch();
        String version = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getVersion();
        os = os+" "+arch+" "+version;
        int cpus = Runtime.getRuntime().availableProcessors();
        String processCpuLoad = new DecimalFormat("###.###%").format(ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad());
        String systemCpuLoad = new DecimalFormat("###.###%").format(ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad());
        long ramUsed = ((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / (1024 * 1024));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.addField(":desktop: OS:", os, true);
        builder.addField(":computer: RAM usage:", ramUsed+"MB", true);
        builder.addField(":gear: CPU usage:", processCpuLoad + "/" + systemCpuLoad + " (" + cpus + " Cores)", true);
        builder.addField(":map: Guilds:", "" + commandEvent.getJDA().getGuilds().size() , true);
        builder.addField(":speech_balloon: Text Channels:", "" + commandEvent.getJDA().getTextChannels().size(), true);
        builder.addField(":speaker: Voice Channels:", "" + commandEvent.getJDA().getVoiceChannels().size(), true);
        builder.addField(":bust_in_silhouette: Users:", "" + commandEvent.getJDA().getUsers().size(), true);
        builder.setColor(Color.RED);
        builder.setFooter("© McGamerZone - " + Date.from(Instant.now()).getYear(), commandEvent.getJDA().getSelfUser().getEffectiveAvatarUrl());
        commandEvent.getChannel().sendMessage(builder.build()).queue();
    }
}

