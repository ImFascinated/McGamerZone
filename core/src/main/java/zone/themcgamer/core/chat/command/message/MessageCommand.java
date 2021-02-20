package zone.themcgamer.core.chat.command.message;

import com.cryptomorin.xseries.XSound;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.badSportSystem.BadSportClient;
import zone.themcgamer.core.badSportSystem.BadSportSystem;
import zone.themcgamer.core.badSportSystem.Punishment;
import zone.themcgamer.core.badSportSystem.PunishmentCategory;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.player.PlayerDirectMessageEvent;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MessageCommand {
    private final AccountManager accountManager;
    private final BadSportSystem badSportSystem;

    @Command(name = "msg", aliases = { "whisper", "m", "message", "dm", "w" }, description = "Sent a private message to a player.",
            playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
        if (cacheRepository == null)
            return;
        Optional<BadSportClient> optionalBadSportClient = badSportSystem.lookup(player.getUniqueId());
        if (optionalBadSportClient.isEmpty())
            return;
        String[] args = command.getArgs();
        if (args.length < 2) {
            player.sendMessage(Style.error("Chat", "Usage: &b/" + command.getLabel() + " (player) (message)"));
            return;
        }
        Optional<Punishment> optionalMute = optionalBadSportClient.get().getMute();
        if (optionalMute.isPresent()) {
            player.sendMessage(Style.error("Bad Sport", PunishmentCategory.format(optionalMute.get())));
            return;
        }
        String target = args[0];
        if (player.getName().equalsIgnoreCase(target)) {
            player.sendMessage(Style.error("Chat", "You can not message yourself."));
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
