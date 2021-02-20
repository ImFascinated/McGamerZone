package zone.themcgamer.api.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This class handles serializing of {@link IModel}'s
 *
 * @author Braydon
 */
public class ModelSerializer implements JsonSerializer<IModel> {
    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
     * non-trivial field of the {@code iModel} object. However, you should never invoke it on the
     * {@code iModel} object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).</p>
     *
     * @param iModel       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context
     * @return a JsonElement corresponding to the specified object.
     */
    @Override
    public JsonElement serialize(IModel iModel, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, Object> entry : iModel.toMap().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Enum<?>)
                value = ((Enum<?>) value).name();
            object.addProperty(entry.getKey(), value.toString());
        }
        return object;
    }
}