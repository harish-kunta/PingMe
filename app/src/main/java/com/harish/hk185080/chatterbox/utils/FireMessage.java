package com.harish.hk185080.chatterbox.utils;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FireMessage {
    //private final String SERVER_KEY = "AIzaSyA0FB_ByKW7-UIGhLzpE4E0NpROWccjwbs";
    private final String SERVER_KEY = "AAAA4a0Db_E:APA91bHQFEh62S2uhCTacBYghGH16nsX6YhJFSEm29g7Z-XWO61XmELOov1fTXM4QMNevj7Ew_TmJAxkUfsBbJ_u_7QUtCy1T3tUtSZTKC2lZxLo5DlOuX_k3G7Hyevm4IdOvVIiTjrg";
    private final String API_URL_FCM = "POST https://fcm.googleapis.com/v1/projects/chatterbox-f606d/messages:send HTTP/1.1";
    private JSONObject root;

    public FireMessage(String title, String message) throws JSONException {
        root = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("title", title);
        data.put("message", message);
        root.put("data", data);
    }


    public String sendToTopic(String topic) throws Exception { //SEND TO TOPIC
        System.out.println("Send to Topic");
        root.put("condition", "'" + topic + "' in topics");
        return sendPushNotification(true);
    }

    public String sendToGroup(JSONArray mobileTokens) throws Exception { // SEND TO GROUP OF PHONES - ARRAY OF TOKENS
        root.put("registration_ids", mobileTokens);
        return sendPushNotification(false);
    }

    public String sendToToken(String token) throws Exception {//SEND MESSAGE TO SINGLE MOBILE - TO TOKEN
        root.put("to", token);
        return sendPushNotification(false);
    }


    private String sendPushNotification(boolean toTopic) throws Exception {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
            URL url = new URL(API_URL_FCM);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);

            System.out.println(root.toString());

            try {
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(root.toString());
                wr.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output;
                StringBuilder builder = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    builder.append(output);
                }
                System.out.println(builder);
                String result = builder.toString();

                JSONObject obj = new JSONObject(result);

                if (toTopic) {
                    if (obj.has("message_id")) {
                        return "SUCCESS";
                    }
                } else {
                    int success = Integer.parseInt(obj.getString("success"));
                    if (success > 0) {
                        return "SUCCESS";
                    }
                }

                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }



}
