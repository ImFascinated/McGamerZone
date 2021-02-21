package zone.themcgamer.core.account;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.module.Module;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author Braydon
 * @implNote A mini account makes it easier to organize accounts into different sections.
 *           For instance, important information regarding a player is stored in
 *           {@link Account}, and other things will be stored in a mini account.
 */
@Getter
public abstract class MiniAccount<T> extends Module {
    private final Map<UUID, T> accounts = new HashMap<>();

    public MiniAccount(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Get the default account using the given account id, uuid, or name
     * @param accountId the account id
     * @param uuid the uuid
     * @param name the name
     * @return the default account
     */
    public abstract T getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp);

    /**
     * Get the query to fetch the account using the given account id, uuid, or name
     * @param accountId the account id
     * @param uuid the uuid
     * @param name the name
     * @return the query
     */
    public abstract String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp);

    /**
     * Called when an account is fetched from MySQL
     * @param accountId the account id
     * @param uuid the uuid
     * @param name the name
     * @param resultSet the result set that was fetched
     * @throws SQLException exception
     */
    public abstract void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) throws SQLException;

    /**
     * Add the provided account identified by the given {@link UUID}
     * @param uuid the uuid to identify the account
     * @param object the account
     */
    public void addAccount(UUID uuid, Object object) {
        accounts.put(uuid, (T) object);
    }

    /**
     * Get the {@link T} account for the given {@link UUID}
     * @param uuid the uuid of the account
     * @return the optional account
     */
    public Optional<T> lookup(UUID uuid) {
        return Optional.ofNullable(accounts.get(uuid));
    }

    /**
     * Get the {@link T} account that tests against the {@link Predicate}
     * @param predicate the predicate to test against
     * @return the optional account
     */
    public Optional<T> lookup(Predicate<T> predicate) {
        return accounts.values().stream().filter(predicate).findFirst();
    }
}