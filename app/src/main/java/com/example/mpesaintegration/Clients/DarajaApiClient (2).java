package com.example.mpesaintegration.Clients;

import com.example.mpesaintegration.Constants.ConstantsClass;
import com.example.mpesaintegration.Interceptors.AccessTokenInterceptor;
import com.example.mpesaintegration.Interceptors.AuthInterceptor;
import com.example.mpesaintegration.Interfaces.STKPushService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DarajaApiClient {
    private Retrofit retrofit;
    private boolean isDebug;
    private boolean isGetAccessToken;
    private String mAuthToken;
    private HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    public DarajaApiClient setIsDebug(boolean isDebug){
        this.isDebug = isDebug;
        return this;
    }

    public DarajaApiClient setAuthToken(String authToken){
        mAuthToken = authToken;
        return this;
    }

    public DarajaApiClient setGetAccessToken(boolean isGetAccessToken){
        this.isGetAccessToken = isGetAccessToken;
        return this;
    }

    private OkHttpClient.Builder oKHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(ConstantsClass.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ConstantsClass.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ConstantsClass.READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor);

        return builder;
    }

    private Retrofit getRestAdapter(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConstantsClass.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        if (isDebug){
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        OkHttpClient.Builder okhttpClientBuilder = oKHttpClient();

        if (isGetAccessToken){
            okhttpClientBuilder.addInterceptor(new AccessTokenInterceptor());
        }
        if (mAuthToken != null && !mAuthToken.isEmpty()){
            okhttpClientBuilder.addInterceptor(new AuthInterceptor(mAuthToken));
        }
        builder.client(okhttpClientBuilder.build());
        retrofit = builder.build();
        return retrofit;
    }

    public STKPushService stkPushService(){
        return getRestAdapter().create(STKPushService.class);
    }
}
