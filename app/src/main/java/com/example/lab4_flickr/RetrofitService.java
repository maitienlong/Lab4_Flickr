package com.example.lab4_flickr;

import com.example.lab4_flickr.model.Photo;
import com.example.lab4_flickr.model.Photos;
import com.example.lab4_flickr.model.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {

    @POST("/services/rest")
    @FormUrlEncoded
    Call<Post> getAllPost(@Field("method") String method,
                          @Field("api_key") String api_key,
                          @Field("user_id") String user_id,
                          @Field("extras") String extras,
                          @Field("format") String format,
                          @Field("per_page") int per_page,
                          @Field("page") int page,
                          @Field("nojsoncallback") int nojsoncallback
    );

}
