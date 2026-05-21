package com.example.eonnav;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// CONEXION CON EL BACKEND
public class RetrofitInstance {

    //private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static final String BASE_URL = "http://192.168.42.126:8000/";

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}