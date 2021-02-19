package zone.themcgamer.test;

import lombok.SneakyThrows;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 * @implNote This class is strictly for testing purposes
 */
public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        new JedisController().start();

        Optional<ServerGroupRepository> repository = RedisRepository.getRepository(ServerGroupRepository.class);
        while (repository.isPresent()) {
            List<ServerGroup> cached = repository.get().getCached();
            for (ServerGroup serverGroup : cached)
                System.out.println(serverGroup.toString());
            System.out.println("total cached = " + cached.size());
            Thread.sleep(1000L);
        }
    }
}