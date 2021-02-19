package zone.themcgamer.discordbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.UUID;

@CommandInfo(
        name = {"account", "profile"},
        usage = "<name>",
        description = "View your own account")
@Error(prefix = "Account » ",
        value = "An error occured in this command, please contact an administrator.")
public class AccountCommand extends Command {
    @Override
    protected void execute(CommandEvent commandEvent) {
        Message message = commandEvent.getMessage();
        String[] args = message.getContentDisplay().split(" ");


        UUID player;
        List<Member> mentioned = message.getMentionedMembers();
        if (args.length > 1) {
            Member target = mentioned.get(0);
            //(!mentioned.isEmpty()) ? mentioned.get(0) : args[0];

            //TODO check if account is linked

            player = UUID.randomUUID(); //"SET THE UUID";
        } else {
            //TODO your own account if you did not have more than 1 args.

            player = UUID.randomUUID(); //"YOUR OWN UUID";
        }

        //TODO sent the message with player information

    }
}

