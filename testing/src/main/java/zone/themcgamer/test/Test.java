package zone.themcgamer.test;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author Braydon
 * @implNote This class is strictly for testing purposes
 */
public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30L))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.cnetwork.club/v1/status/Rainnny"))
                .timeout(Duration.ofMinutes(1L))
                .header("Content-Type", "application/json")
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("status = " + response.statusCode());
                    System.out.println("body = " + response.body());
                });
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("status = " + response.statusCode());
//        System.out.println("body = " + response.body());
//
//        Optional<ServerGroupRepository> repository = RedisRepository.getRepository(ServerGroupRepository.class);
//        while (repository.isPresent()) {
//            List<ServerGroup> cached = repository.get().getCached();
//            for (ServerGroup serverGroup : cached)
//                System.out.println(serverGroup.toString());
//            System.out.println("total cached = " + cached.size());
//            Thread.sleep(1000L);
//        }
        Thread.sleep(2000L);
    }
}