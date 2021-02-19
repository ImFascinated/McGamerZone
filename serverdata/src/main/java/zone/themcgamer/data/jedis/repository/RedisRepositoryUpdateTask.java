package zone.themcgamer.data.jedis.repository;

import lombok.SneakyThrows;

/**
 * @author Braydon
 */
public class RedisRepositoryUpdateTask extends Thread {
    public RedisRepositoryUpdateTask() {
        super("Redis Repository Update Task");
    }

    /**
     * Loop through all of the {@link RedisRepository}'s and update their local cache
     */
    @Override
    @SneakyThrows
    public void run() {
        while (isAlive()) {
            for (RedisRepository<?, ?> repository : RedisRepository.getRepositories())
                repository.updateCache();
            Thread.sleep(30L);
        }
    }
}