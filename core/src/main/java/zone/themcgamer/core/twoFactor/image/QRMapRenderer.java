package zone.themcgamer.core.twoFactor.image;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

/**
 * @author Braydon
 */
public class QRMapRenderer extends MapRenderer {
    private final Player player;
    private BufferedImage bufferedImage;

    public QRMapRenderer(Player player, BufferedImage bufferedImage) {
        super(true);
        this.player = player;
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (bufferedImage == null)
            return;
        if (this.player.equals(player)) {
            mapCanvas.drawImage(0, 0, bufferedImage);
            bufferedImage = null;
        }
    }
}