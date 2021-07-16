package zone.themcgamer.arcade.map;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.ZipUtils;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.server.ServerManager;
import zone.themcgamer.core.world.MGZWorld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Map Manager") @Getter
public class MapManager extends Module {
    private final List<MGZWorld> maps = new ArrayList<>();

    public MapManager(JavaPlugin plugin) {
        super(plugin);
    }

    public void withConsumer(Consumer<CompletableFuture<Void>> consumer) {
        consumer.accept(CompletableFuture.runAsync(() -> {
            File mapsDirectory = new File("maps");
            if (!mapsDirectory.exists())
                mapsDirectory.mkdirs();
            for (MGZGame game : MGZGame.values()) {
                File parsedMapsDirectory = new File(File.separator + "home" + File.separator + "minecraft" + File.separator + "ftp" + File.separator + "upload" + File.separator + "upload" + File.separator + "maps" + File.separator + game.name());
                if (!parsedMapsDirectory.exists())
                    continue;
                File[] files = parsedMapsDirectory.listFiles();
                if (files == null)
                    continue;
                for (File file : files) {
                    String fileName = file.getName();
                    String[] split = fileName.split("\\.");
                    if (split.length < 1)
                        continue;
                    String lastDottedString = split[split.length - 1];
                    if (!lastDottedString.equals("zip"))
                        continue;
                    File targetDirectory = new File(mapsDirectory, game.name() + File.separator +
                            file.getName().substring(0, fileName.indexOf(lastDottedString) - 1));
                    if (!targetDirectory.exists())
                        targetDirectory.mkdirs();
                    try {
                        ZipUtils.unzip(file, targetDirectory);
                        maps.add(new MGZWorld(targetDirectory));
                    } catch (IOException ex) {
                        ex.printStackTrace();

                        MGZWorld mgzWorld;
                    }
                }
            }
        }));
    }
}