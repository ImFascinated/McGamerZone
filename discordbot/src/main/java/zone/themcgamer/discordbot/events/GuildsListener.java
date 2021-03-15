package zone.themcgamer.discordbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GuildsListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Member Joined");
        embedBuilder.addField("Member", user.getAsTag() + " (" + user.getId() + ")", true);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;
        EmbedBuilder embedBuilder = EmbedUtils.errorEmbed();
        embedBuilder.setTitle("Member Left");
        embedBuilder.addField("Member", user.getAsTag() + " (" + user.getId() + ")", true);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        Guild guild = event.getGuild();
        List<Role> roles = event.getRoles();

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Member Role Added");
        embedBuilder.setDescription(event.getUser().getAsTag());
        embedBuilder.addField("Role", roles.stream().map(Role::getName).collect(Collectors.joining("&7, &f")), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        Guild guild = event.getGuild();
        List<Role> roles = event.getRoles();
        if (roles.size() == 0)
            return;

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Member Role Added");
        embedBuilder.setDescription(event.getUser().getAsTag());
        embedBuilder.addField("Role", roles.stream().map(Role::getName).collect(Collectors.joining(", ")), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onRoleUpdateColor(@NotNull RoleUpdateColorEvent event) {
        Guild guild = event.getGuild();
        Role role = event.getRole();

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Role Color Update");
        embedBuilder.setColor(role.getColor());
        embedBuilder.addField("Role", role.getAsMention(), false);
        embedBuilder.addField("Id", role.getId(), false);
        embedBuilder.addField("Color", Objects.requireNonNull(role.getColor()).toString(), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onRoleUpdateName(@NotNull RoleUpdateNameEvent event) {
        Guild guild = event.getGuild();
        Role role = event.getRole();

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Role Name Update");
        embedBuilder.setColor(role.getColor());
        embedBuilder.setDescription(event.getOldName() + " **->** " + event.getNewName());
        embedBuilder.addField("Role", role.getAsMention(), false);
        embedBuilder.addField("Id", role.getId(), false);
        embedBuilder.addField("Color", Objects.requireNonNull(role.getColor()).toString(), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onRoleUpdatePermissions(@NotNull RoleUpdatePermissionsEvent event) {
        Guild guild = event.getGuild();
        Role role = event.getRole();
        EnumSet<Permission> permission = event.getNewPermissions();
        EnumSet<Permission> oldPermission = event.getOldPermissions();

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Role Permission Update");
        embedBuilder.setColor(role.getColor());
        embedBuilder.addField("Role", role.getAsMention(), false);
        embedBuilder.addField("Id", role.getId(), false);
        embedBuilder.addField("Permissions Added", permission.stream().map(Permission::getName).collect(Collectors.joining(", ")), false);
        embedBuilder.addField("Permissions Removed", oldPermission.stream().map(Permission::getName).collect(Collectors.joining(", ")), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }

    @Override
    public void onRoleUpdatePosition(@NotNull RoleUpdatePositionEvent event) {
        Guild guild = event.getGuild();
        Role role = event.getRole();

        EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
        embedBuilder.setTitle("Role Position Update");
        embedBuilder.setColor(role.getColor());
        embedBuilder.setDescription(event.getOldPosition() + " -> " + event.getNewPosition());
        embedBuilder.addField("Role", role.getAsMention(), false);
        embedBuilder.addField("Id", role.getId(), false);
        embedBuilder.addField("Color", Objects.requireNonNull(role.getColor()).toString(), false);
        embedBuilder.addField("Guild", guild.getName(), true);
        MessageUtils.sendLogMessage(embedBuilder);
    }
}