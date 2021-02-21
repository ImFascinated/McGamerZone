package zone.themcgamer.core.twoFactor.image;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class QRImageGenerator implements Runnable {
    private static final int MAP_SIZE = 132;

    private final Player player;
    private final String secretKey;
    private final Consumer<BufferedImage> imageConsumer;

    @Override
    public void run() {
        QRCodeWriter qrWriter = new QRCodeWriter();
        try {
            imageConsumer.accept(MatrixToImageWriter.toBufferedImage(qrWriter.encode(
                    "otpauth://totp/" + player.getName() + "@McGamerZone?secret=" + secretKey,
                    BarcodeFormat.QR_CODE, MAP_SIZE, MAP_SIZE)));
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
    }
}