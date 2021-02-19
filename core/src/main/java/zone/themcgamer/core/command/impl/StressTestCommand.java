package zone.themcgamer.core.command.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import zone.themcgamer.common.HashUtils;
import zone.themcgamer.common.RandomUtils;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * @author Braydon
 */
@RequiredArgsConstructor
public class StressTestCommand {
    private final JavaPlugin plugin;
    private BukkitTask task;

    @Command(name = "stresstest", ranks = { Rank.DEVELOPER }, description = "Stress test the server")
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        if (task != null) {
            task.cancel();
            task = null;
            sender.sendMessage(Style.main("Stress Test", "§cStopped task..."));
            return;
        }
        sender.sendMessage(Style.main("Stress Test", "Task started!"));
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            AtomicReference<Double> sqrt = new AtomicReference<>((double) 2);
            AtomicInteger count = new AtomicInteger();
            (ThreadLocalRandom.current().nextBoolean() ? IntStream.range(0, 18000).parallel() : IntStream.range(0, 8000)).forEach(i -> {
                sqrt.set(Math.sqrt(sqrt.get() * i) / 2.);

                try {
                    Class.forName("net.minecraft.server.v1_8_R3.MinecraftServer").getDeclaredMethod("a", String.class);
                } catch (Exception ignored) {}

                for (int j = 0; j < ThreadLocalRandom.current().nextInt(3); j++)
                    HashUtils.encryptSha256(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                count.incrementAndGet();
            });
            if (RandomUtils.randomInt(0, 50) > 43) {
                try {
                    System.out.println("Sleeping...");
                    Thread.sleep(30L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }, 1L, 1L);
    }
}