package com.example.authrolibrary.http;


import com.example.authrolibrary.entity.AuthCodeEntity;
import com.example.authrolibrary.entity.RequestEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RequsetServer {

    /**
     * 设备端激活接口
     * @param requestBean 请求参数
     * @return
     */
    @POST("androidApp/soft/app/active")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<AuthCodeEntity> getAuthCode(@Body RequestEntity requestBean);
}
