package com.example.eonnav;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemTextColor(null);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                return true;
            }

            if (item.getItemId() == R.id.nav_pokedex) {
                startActivity(new Intent(this, PokedexActivity.class));
                return true;
            }

            if (item.getItemId() == R.id.nav_favorites) {
                // futura pantalla de favoritos
                return true;
            }

            return false;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1025"; //Ver pokedex actual entera
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1351"; //Ver pokedex actual entera con todas las variantes de cada pokemon

        EditText searchBar = findViewById(R.id.searchBar);

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

                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

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
