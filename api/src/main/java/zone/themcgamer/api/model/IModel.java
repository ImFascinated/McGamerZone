package zone.themcgamer.api.model;

import java.util.HashMap;

/**
 * This interface represents a custom Object that will displayed as json when handling requests
 *
 * @author Braydon
 */
public interface IModel {
    HashMap<String, Object> toMap();
}