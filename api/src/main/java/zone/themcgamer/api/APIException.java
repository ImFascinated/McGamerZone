package zone.themcgamer.api;

import lombok.NoArgsConstructor;

/**
 * This {@link RuntimeException} gets thrown when there is a problem handling a {@link RestPath}
 *
 * @author Braydon
 */
@NoArgsConstructor
public class APIException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public APIException(String message) {
        super(message);
    }
}