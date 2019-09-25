package com.example.realtimelocation.Utils;

import com.example.realtimelocation.Model.User;
import com.example.realtimelocation.Remote.IFCMService;
import com.example.realtimelocation.Remote.RetrofitClient;

public class Common {

    public static final String USER_INFO = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUID";
    public static final String TOKENS = "Tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUID";
    public static final String TO_UID = "ToUID";
    public static final String TO_NAME = "ToName";
    public static User loggedUser;

    public static IFCMService getIfcmService() {
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }
}
