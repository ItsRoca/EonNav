package com.example.eonnav;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokedexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1025"; //Ver pokedex actual entera

        StringRequest request = new StringRequest(Request.Method.GET, url,
        response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray results = json.getJSONArray("results");

                List<String> names = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject pokemon = results.getJSONObject(i);
                    names.add(pokemon.getString("name"));
                }

                PokemonAdapter adapter = new PokemonAdapter(this, names);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
        error -> {
            error.printStackTrace();
        });

        queue.add(request);
    }

}
