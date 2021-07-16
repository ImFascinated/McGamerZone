package zone.themcgamer.core.announce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import zone.themcgamer.data.jedis.command.impl.announce.AnnounceType;

@AllArgsConstructor @Getter @Setter
public class Announcement {

    private String message;
    private long period, delay;
    private AnnounceType type;

}
