package zone.themcgamer.core.badSportSystem;

import com.google.gson.*;
import zone.themcgamer.common.MiscUtils;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author Braydon
 */
public class PunishmentSerializer implements JsonSerializer<Punishment>, JsonDeserializer<Punishment> {
    @Override
    public Punishment deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!element.isJsonObject())
            return null;
        JsonObject object = (JsonObject) element;
        String removeStaffName = object.get("removeStaffName").getAsString();
        String removeReason = object.get("removeReason").getAsString();
        return new Punishment(
                object.get("id").getAsInt(),
                object.get("targetIp").getAsString(),
                UUID.fromString(object.get("targetUuid").getAsString()),
                PunishmentCategory.valueOf(object.get("category").getAsString()),
                PunishmentOffense.valueOf(object.get("offense").getAsString()),
                object.get("severity").getAsInt(),
                MiscUtils.getUuid(object.get("staffUuid").getAsString()),
                object.get("staffName").getAsString(),
                object.get("timeIssued").getAsLong(),
                object.get("duration").getAsLong(),
                object.get("reason").getAsString(),
                MiscUtils.getUuid(object.get("removeStaffUuid").getAsString()),
                removeStaffName.trim().isEmpty() ? null : removeStaffName,
                removeReason.trim().isEmpty() ? null : removeReason,
                object.get("timeRemoved").getAsLong()
        );
    }

    @Override
    public JsonElement serialize(Punishment punishment, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", punishment.getId());
        object.addProperty("targetIp", punishment.getTargetIp());
        object.addProperty("targetUuid", punishment.getTargetUuid().toString());
        object.addProperty("category", punishment.getCategory().name());
        object.addProperty("offense", punishment.getOffense().name());
        object.addProperty("severity", punishment.getSeverity());
        object.addProperty("staffUuid", punishment.getStaffUuid() == null ? "" : punishment.getStaffUuid().toString());
        object.addProperty("staffName", punishment.getStaffName());
        object.addProperty("timeIssued", punishment.getTimeIssued());
        object.addProperty("duration", punishment.getDuration());
        object.addProperty("reason", punishment.getReason());
        object.addProperty("removeStaffUuid", punishment.getRemoveStaffUuid() == null ? "" : punishment.getRemoveStaffUuid().toString());
        object.addProperty("removeStaffName", punishment.getRemoveStaffName() == null ? "" : punishment.getRemoveStaffName());
        object.addProperty("removeReason", punishment.getRemoveReason() == null ? "" : punishment.getRemoveReason());
        object.addProperty("timeRemoved", punishment.getTimeRemoved());
        return object;
    }
}