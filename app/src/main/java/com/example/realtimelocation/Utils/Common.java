package com.example.realtimelocation.Utils;

import com.example.realtimelocation.Model.User;
import com.example.realtimelocation.Remote.IFCMService;
import com.example.realtimelocation.Remote.RetrofitClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String USER_INFO = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUID";
    public static final String TOKENS = "Tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUID";
    public static final String TO_UID = "ToUID";
    public static final String TO_NAME = "ToName";
    public static final String FRIEND_REQUEST = "FriendRequest";
    public static final String PUBLIC_LOCATION = "PublicLocation";
    public static User loggedUser;
    public static User trackingUser;

    public static IFCMService getIfcmService() {
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }

    public static Object convertTimeStampToDate(long time) {
        return new Date(new Timestamp(time).getTime());
    }

    public static String getDataFormatted(Object date) {
        return new SimpleDateFormat("dd-MM-yyy HH:mm").format(date);
    }
}
