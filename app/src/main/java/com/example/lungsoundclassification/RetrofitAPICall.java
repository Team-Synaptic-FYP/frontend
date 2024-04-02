package com.example.lungsoundclassification;

import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPICall {

    @POST("giveResult")
    Call<ResponseObject> sendWav(@Body RequestBody requestBody);
}
