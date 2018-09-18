package com.harish.hk185080.chatterbox.Network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseApi {
    @Headers({"Authorization: key=AAAA4a0Db_E:APA91bHQFEh62S2uhCTacBYghGH16nsX6YhJFSEm29g7Z-XWO61XmELOov1fTXM4QMNevj7Ew_TmJAxkUfsBbJ_u_7QUtCy1T3tUtSZTKC2lZxLo5DlOuX_k3G7Hyevm4IdOvVIiTjrg",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<FirebaseMessage> sendMessage(@Body FirebaseMessage message);
}
