package zone.themcgamer.core.chat;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.api.WebAPI;
import zone.themcgamer.core.badSportSystem.BadSportClient;
import zone.themcgamer.core.badSportSystem.BadSportSystem;
import zone.themcgamer.core.badSportSystem.Punishment;
import zone.themcgamer.core.badSportSystem.PunishmentCategory;
import zone.themcgamer.core.chat.command.ClearChatCommand;
import zone.themcgamer.core.chat.command.EmotesCommand;
import zone.themcgamer.core.chat.command.message.MessageCommand;
import zone.themcgamer.core.chat.command.message.ReplyCommand;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.cooldown.CooldownHandler;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.data.ChatFilterLevel;
import zone.themcgamer.data.Rank;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Chat")
public class ChatManager extends Module {
    private final BadSportSystem badSportSystem;
    private final IChatComponent[] chatComponents;
    @Getter private final Map<String, String> emotes = new HashMap<>();

    {
        emotes.put(":shrug:", "¯\\_(ツ)_/¯");
        emotes.put(":happyghost:", "⊂(´･◡･⊂ )∘˚˳°");
        emotes.put(":donger:", "ヽ༼ຈل͜ຈ༽ﾉ");
        emotes.put(":disapproval:", "ಠ_ಠ");
        emotes.put(":squint:", "(≖_≖)");
        emotes.put(":lenny:", "(౮⦦ʖ౮)");
        emotes.put(":unflip:", "┬─┬ ノ( ゜-゜ノ)");
        emotes.put(":same:", "(☞ﾟヮﾟ)☞");
        emotes.put(":why:", "ლ(ಥ Д ಥ )ლ");
        emotes.put(":tableflip:", "(╯°□°）╯︵ ┻━┻");
        emotes.put(":angryghost:", "⊂(•̀_•́⊂ )∘˚˳°");
        emotes.put(":kiss:", "( ˘ ³˘)♥");
        emotes.put(":ameno:", "༼ つ ◕_◕ ༽つ");
        emotes.put(":heart:", "❤");
        emotes.put("<3", "❤");
    }

    public ChatManager(JavaPlugin plugin, AccountManager accountManager, BadSportSystem badSportSystem, IChatComponent[] chatComponents) {
        super(plugin);
        this.badSportSystem = badSportSystem;
        this.chatComponents = chatComponents;
        registerCommand(new ClearChatCommand());
        registerCommand(new MessageCommand(accountManager, badSportSystem));
        registerCommand(new ReplyCommand(accountManager, badSportSystem));
        registerCommand(new EmotesCommand(this));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setCancelled(true);

        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Optional<BadSportClient> optionalBadSportClient = badSportSystem.lookup(player.getUniqueId());
        if (optionalBadSportClient.isEmpty()) {
            player.sendMessage(Style.error("Chat", "§cCannot find bad sport profile"));
            return;
        }
        Optional<Punishment> optionalMute = optionalBadSportClient.get().getMute();
        if (optionalMute.isPresent()) {
            player.sendMessage(Style.error("Bad Sport", PunishmentCategory.format(optionalMute.get())));
            return;
        }
        try {
            message = WebAPI.filterText(message, ChatFilterLevel.HIGH);
        } catch (Exception ex) {
            player.sendMessage(Style.error("Chat", "§cProblem filtering chat message: §f" + ex.getLocalizedMessage()));
        }
        if (message.trim().isEmpty()) {
            player.sendMessage(Style.error("Chat", "§cCannot send empty chat message"));
            return;
        }
        if (chatComponents.length <= 0) {
            player.sendMessage(Style.error("Chat", "§cCannot format chat message"));
            return;
        }
        if (!CooldownHandler.canUse(player, "Chat", TimeUnit.SECONDS.toMillis(3L), false)
                && !optionalAccount.get().hasRank(Rank.GAMER) && !optionalAccount.get().hasRank(Rank.HELPER)) {
            player.sendMessage(Style.error("Chat", "You are chatting too quickly! To bypass this cooldown," +
                    " please consider purchasing a donator rank over at §bstore.mcgamerzone.net§7."));
            return;
        }
        for (Map.Entry<String, String> emote : emotes.entrySet())
            message = message.replace(emote.getKey(), emote.getValue());
        List<BaseComponent> components = new ArrayList<>();
        for (IChatComponent chatComponent : chatComponents) {
            BaseComponent component = chatComponent.getComponent(player);
            if (component == null)
                continue;
            components.add(component);
            components.add(new TextComponent(" "));
        }
        components.add(new TextComponent("§8» §7"));
        components.addAll(Arrays.asList(new ComponentBuilder(message).color(ChatColor.GRAY).create()));

        BaseComponent[] baseComponents = components.toArray(new BaseComponent[0]);
        for (Player online : Bukkit.getOnlinePlayers())
            online.sendMessage(baseComponents);
    }
}