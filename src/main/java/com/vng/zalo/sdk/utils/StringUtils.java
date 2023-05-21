/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zalo.sdk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;

import javax.xml.transform.OutputKeys;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author ducanhnt22
 */
public class StringUtils {
    static ZaloOaClient client = new ZaloOaClient();
    static final String access_token = "YOUR-ACCESS-TOKEN";
    static final String API_KEY = "YOUR-OPENAI-KEY";
    static final String OA_KEY = "YOUR-ZALO-OA-KEY";

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String join(List<?> collection, String parten) {
        StringBuilder result = new StringBuilder();
        if (collection != null) {
            for (int i = 0; i < collection.size(); i++) {
                if (i == 0) {
                    result.append(collection.get(i));
                } else {
                    result.append(parten).append(collection.get(i));
                }
            }
        }
        return result.toString();
    }

    public static String[] getAllUserIds(String jsonResponse) {
        List<String> userIds = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray followersArray = jsonObject.getAsJsonObject("data").getAsJsonArray("followers");

        for (JsonElement followerElement : followersArray) {
            String userId = followerElement.getAsJsonObject().get("user_id").getAsString();
            userIds.add(userId);
        }
        String[] userIdsArray = userIds.toArray(new String[0]);

        return userIdsArray;
    }

    public static String handleJson(String jsonString) {
        String toId = "";
        JsonObject payload = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonArray dataArray = payload.getAsJsonArray("data");

        for (JsonElement element : dataArray) {
            JsonObject messageObj = element.getAsJsonObject();
            toId = messageObj.get("from_id").getAsString();
        }
        return toId;
    }

    public static String toIDJson(String jsonString) {
        String toId = "";
        JsonObject payload = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonArray dataArray = payload.getAsJsonArray("data");

        for (JsonElement element : dataArray) {
            JsonObject messageObj = element.getAsJsonObject();
            toId = messageObj.get("to_id").getAsString();
        }
        return toId;
    }

    public static String handleMessage(String jsonString) {
        String toMessage = "";

        JsonObject payload = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonArray dataArray = payload.getAsJsonArray("data");

        for (JsonElement element : dataArray) {
            JsonObject messageObj = element.getAsJsonObject();
            toMessage = messageObj.get("message").getAsString();
        }
        return toMessage;
    }

    public static String extractTextFromJson(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Check if the "choices" array exists
        if (jsonObject.has("choices") && jsonObject.get("choices").isJsonArray()) {
            // Extract the "text" from the first element in the "choices" array
            JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
            if (choicesArray.size() > 0) {
                JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
                if (firstChoice.has("text")) {
                    String text = firstChoice.get("text").getAsString();
                    return text;
                }
            }
        }

        // Return an empty string if the "text" data is not found
        return "";
    }

    public static void main(String[] args) throws APIException {
        Map<String, String> headers = new HashMap<>();
        headers.put("access_token", access_token);

        JsonObject data = new JsonObject();
        data.addProperty("offset", 0);
        data.addProperty("count", 6);

        Map<String, Object> params = new HashMap<>();
        params.put("data", data.toString());

        JsonObject excuteRequest = client.excuteRequest("https://openapi.zalo.me/v2.0/oa/listrecentchat", "GET", params, null, headers, null);
        handleJson(excuteRequest.toString());
        String check = toIDJson(excuteRequest.toString());

        JsonObject id = new JsonObject();

        String userId = handleJson(excuteRequest.toString());
        String msg = handleMessage(excuteRequest.toString()); // msg

        id.addProperty("user_id", userId);
        while (true) {
            if (OA_KEY != check) {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("Authorization", "Bearer " + API_KEY);
                JsonObject bodys = new JsonObject();
                bodys.addProperty("model", "text-davinci-003");
                bodys.addProperty("prompt", msg);
                bodys.addProperty("max_tokens", 100);
                JsonObject excuteOpenAISendMessageRequest = client.excuteRequest("https://api.openai.com/v1/completions", "POST", null, bodys, header, null);
                System.out.println(excuteOpenAISendMessageRequest.toString()); //log - ti xoa

                String txt = extractTextFromJson(excuteOpenAISendMessageRequest.toString());

                JsonObject text = new JsonObject();
                text.addProperty("text", txt); //send msg

                JsonObject body = new JsonObject();
                body.add("recipient", id);
                body.add("message", text);

                JsonObject excuteSendMessageRequest = client.excuteRequest("https://openapi.zalo.me/v2.0/oa/message", "POST", null, body, headers, null);
            }
        }
    }
}

