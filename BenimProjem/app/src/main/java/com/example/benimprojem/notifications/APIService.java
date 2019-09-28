package com.example.benimprojem.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA2tlN9F0:APA91bE2teCBwDoalF_3_qNZgIigt3d_VMiiveWYIfgZ4-UgC6sxThFctXGxUJa-jwCL787cLEHH8Ik2XBjDpSTDIZylC9a6rIZbX8poK-v8JAX7qELwm2VERnC2AcFO6SG-X40eu5oZ"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
