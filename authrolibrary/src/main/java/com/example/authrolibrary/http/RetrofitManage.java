package com.example.authrolibrary.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManage {

    private static String API_VERSION = "1.0";
    private static String BaseUrl = "http://cloud.a-ihome.com:10090/apop/";

    private static int TIMEOUT_IN_MILLIONS = 30;

    public static Retrofit getRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_IN_MILLIONS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_MILLIONS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_MILLIONS, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
