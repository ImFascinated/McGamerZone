package zone.themcgamer.api.route;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AllArgsConstructor;
import spark.Request;
import spark.Response;
import zone.themcgamer.api.APIException;
import zone.themcgamer.api.APIVersion;
import zone.themcgamer.api.RestPath;
import zone.themcgamer.api.model.impl.AccountModel;
import zone.themcgamer.api.repository.AccountRepository;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.data.APIAccessLevel;
import zone.themcgamer.data.jedis.data.APIKey;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This route handles everything associated with {@link AccountModel}
 *
 * @author Braydon
 */
@AllArgsConstructor
public class AccountRoute {
    // Account model cache for players that were looked up via the account route
    public static final Cache<UUID, AccountModel> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final AccountRepository accountRepository;

    /**
     * This path handles displaying the {@link AccountModel} with the given {@link UUID}
     */
    @RestPath(path = "/account/:uuid", version = APIVersion.V1)
    public AccountModel get(Request request, Response response, APIKey apiKey) throws APIException {
        UUID uuid = MiscUtils.getUuid(request.params(":uuid"));
        if (uuid == null)
            throw new APIException("Invalid UUID");
        AccountModel account = CACHE.getIfPresent(uuid);
        if (account == null) {
            account = accountRepository.getAccount(uuid);
            account.setTimeCached(System.currentTimeMillis());
            CACHE.put(uuid, account);
        }
        if (apiKey.getAccessLevel() == APIAccessLevel.STANDARD)
            account.setEncryptedIpAddress("Unauthorized");
        return account;
    }
}