package com.kodedu.other;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

public class JsonHelper {
    public static JsonArray getJsonArrayOrEmpty(JsonObject jsonObject, String key) {

        if (jsonObject.containsKey(key) && jsonObject.get(key).getValueType() == JsonValue.ValueType.ARRAY) {
            return jsonObject.getJsonArray(key);
        }
        return JsonValue.EMPTY_JSON_ARRAY;
    }

    public static JsonObject getJsonObjectOrEmpty(JsonObject jsonObject, String key) {

        if (jsonObject.containsKey(key) && jsonObject.getValueType() == JsonValue.ValueType.OBJECT) {
            return jsonObject.getJsonObject(key);
        }
        return JsonValue.EMPTY_JSON_OBJECT;
    }

    public static boolean containsNumber(JsonObject jsonObject, String key) {
        return jsonObject.containsKey(key) && jsonObject.get(key).getValueType() == JsonValue.ValueType.NUMBER;
    }
}
