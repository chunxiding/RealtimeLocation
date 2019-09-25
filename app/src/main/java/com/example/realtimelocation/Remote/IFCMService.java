package com.example.realtimelocation.Remote;

import com.example.realtimelocation.Model.MyResponse;
import com.example.realtimelocation.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

// Firebase Cloud Messaging

public interface IFCMService {
    // cloud messaging server key
    @Headers({
            "Content-Type:application/json",
                "Authorization:key=AAAAs7d4fLc:APA91bEamV5HMl_yFQRyc2LjnvxmFqXEvYxN2tOs7QaS4N0E3Ba3K1XSG-IkJUYJm4_g2UYteQy-bSscNn__dclmCgha6QI_5bUQXkiZgrUDlDRCArF9RkAdMehulxEhG72LotFOuWwR"
    })

    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser (@Body Request body);
}
