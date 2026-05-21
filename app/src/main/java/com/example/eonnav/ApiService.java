package com.example.eonnav;

import com.example.eonnav.utils.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/favorites/")
    Call<List<Favorite>> getFavorites(
            @Query("user_id") int userId
    );
}
