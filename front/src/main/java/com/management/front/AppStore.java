package com.management.front;

import com.management.front.request.JwtResponse;

public class AppStore {
    private static JwtResponse jwt;

    private AppStore(){
    }

    public static JwtResponse getJwt() {
        return jwt;
    }

    public static void setJwt(JwtResponse jwt) {
        AppStore.jwt = jwt;
    }
}
