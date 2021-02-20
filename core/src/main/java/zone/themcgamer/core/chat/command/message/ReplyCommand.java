package zone.themcgamer.core.chat.command.message;

import com.cryptomorin.xseries.XSound;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class ReplyCommand {
    private final AccountManager accountManager;
    private final BadSportSystem badSportSystem;

    @Command(name = "reply", aliases = { "r" }, description = "Reply to a player that sent you a private message.", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
        if (cacheRepository == null)
            return;
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Chat", "&7Please use &b/" + command.getLabel() + " (message)"));
            return;
        }
        Optional<BadSportClient> optionalBadSportClient = badSportSystem.lookup(player.getUniqueId());
        if (optionalBadSportClient.isEmpty())
            return;
        Optional<Punishment> optionalMute = optionalBadSportClient.get().getMute();
        if (optionalMute.isPresent()) {
            player.sendMessage(Style.error("Bad Sport", PunishmentCategory.format(optionalMute.get())));
            return;
        }
        String message = Arrays.stream(args).skip(0).collect(Collectors.joining(" "));
        Optional<PlayerStatusCache> optionalPlayerStatusCache = cacheRepository.lookup(PlayerStatusCache.class, player.getUniqueId());
        if (optionalPlayerStatusCache.isEmpty()) {
            player.sendMessage(Style.error("Chat", "That player is not online."));
            return;
        }
        PlayerStatusCache statusCache = optionalPlayerStatusCache.get();
        if (statusCache.getLastReply().isEmpty()) {
            player.sendMessage(Style.main("Chat", "&7You have nobody to reply to."));
            return;
        }
        accountManager.lookup(statusCache.getLastReply(), account -> {
            if (account == null) {
                player.sendMessage(Style.error("Chat", "That player does not exist, have they logged in before?"));
                return;
            }
            Optional<PlayerStatusCache> optionalTargetPlayerStatusCache = cacheRepository.lookup(PlayerStatusCache.class, account.getUuid());
            if (optionalTargetPlayerStatusCache.isEmpty()) {
                player.sendMessage(Style.error("Chat", "That player is not online anymore!"));
                optionalPlayerStatusCache.get().setLastReply("");
                cacheRepository.post(optionalPlayerStatusCache.get());
                return;
            }
            PlayerStatusCache statusTargetCache = optionalTargetPlayerStatusCache.get();
            statusTargetCache.setLastReply(player.getName());
            cacheRepository.post(statusTargetCache);

            Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
            if (optionalAccount.isEmpty())
                return;
            player.sendMessage(Style.color("&b\u2709 &7(to " + account.getDisplayName() + "&7) &8\u00BB &f" + message));
            player.playSound(player.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 0.9f, 1f);
            JedisCommandHandler.getInstance().send(new PlayerDirectMessageEvent(
                    optionalAccount.get().getDisplayName(),
                    message,
                    account.getUuid(),
                    true));
        });
    }
}
