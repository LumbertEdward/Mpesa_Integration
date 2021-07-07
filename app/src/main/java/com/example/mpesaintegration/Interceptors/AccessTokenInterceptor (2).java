package com.example.mpesaintegration.Interceptors;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AccessTokenInterceptor implements Interceptor {

    public AccessTokenInterceptor() {
    }

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        //String keys = BuildConfig.CONSUMER_KEY + ":" + BuildConfig.CONSUMER_SECRET;
        String keys = "EwS4McYxSIpa7ekwOTXG2l5MRGF17Q3a" + ":" + "MIu9cBGLL4pQOFpX";
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Basic " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP))
                .build();
        return chain.proceed(request);
    }
}
