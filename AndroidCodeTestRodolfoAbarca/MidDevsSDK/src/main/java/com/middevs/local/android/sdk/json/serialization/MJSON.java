package com.middevs.local.android.sdk.json.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;

import java.io.IOException;

/**
 * @author MidDevs
 * @since 10/14/16
 */
public final class MJSON {

    public static JSONObject toJSON(Object object) {

//			JSONParser jsonParser = new JSONParser ( object );
//			return jsonParser.parse ( );

        if (object == null) return new JSONObject();

        JSONObject jsonObject = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        try {
            jsonObject = new JSONObject(mapper.writeValueAsString(object));

            if (jsonObject.has("_id") && (jsonObject.getString("_id") == null || jsonObject.getString("_id")
                    .equals("null"))) {
                jsonObject.remove("_id");
            }
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            return jsonObject;
        }
        return jsonObject;
    }

    public static Object fromJSON(JSONObject jsonObject,
                                  Class clazz) {

        try {
            if (jsonObject.isEmpty()) return null;
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(mapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            return mapper.readValue(jsonObject.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void print(JSONObject jsonObject) {

        System.out.println(toString(jsonObject));
    }

    public static String toString(JSONObject jsonObject) {

        try {
            return jsonObject.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
