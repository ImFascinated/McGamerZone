package zone.themcgamer.core.command.impl.social;

import com.cryptomorin.xseries.XSound;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.player.PlayerDirectMessageEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MessageCommand {
    private final AccountManager accountManager;
    private final CacheRepository cacheRepository;

    @Command(name = "msg", aliases = { "whisper", "m", "message", "dm", "w" }, description = "Sent a private message to a player.",
            playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 2) {
            player.sendMessage(Style.main("Chat", "&7Please use &b/" + command.getLabel() + " (player) (message)"));
            return;
        }
        String target = args[0];
        if (player.getName().equalsIgnoreCase(target)) {
            player.sendMessage(Style.main("Chat", "&7You can not message yourself."));
            return;
        }
        String message = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        /*
        TODO
         check if player has ignored you.
         check if player has messages disabled
        */

        accountManager.lookup(target, targetAccount -> {
            if (targetAccount == null) {
                player.sendMessage(Style.error("Chat", "That player does not exist, have they logged in before?"));
                return;
            }
            Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
            if (optionalAccount.isEmpty())
                return;
            Optional<PlayerStatusCache> optionalPlayerStatusCache = cacheRepository.lookup(PlayerStatusCache.class, targetAccount.getUuid());
            if (optionalPlayerStatusCache.isEmpty()) {
                player.sendMessage(Style.error("Chat", "That player is not online."));
                return;
            }
            PlayerStatusCache statusCache = optionalPlayerStatusCache.get();
            statusCache.setLastReply(player.getName());
            cacheRepository.post(statusCache);

            player.sendMessage(Style.color("&b\u2709 &7(to " + targetAccount.getDisplayName() + "&7) &8\u00BB &f" + message));
            player.playSound(player.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 0.9f, 1f);
            JedisCommandHandler.getInstance().send(new PlayerDirectMessageEvent(
                    optionalAccount.get().getDisplayName(),
                    message,
                    targetAccount.getUuid(),
                    false));
        });
    }
}
