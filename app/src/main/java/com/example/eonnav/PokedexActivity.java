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

        // RecyclerView donde se mostrarán los Pokémon
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Librería volley para las peticiones HTTP
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL de la PokeAPI
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1025"; //Ver pokedex actual entera
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1351"; //Ver pokedex actual entera con todas las variantes de cada pokemon

        // Barra de búsqueda
        EditText searchBar = findViewById(R.id.searchBar);

        // Petición GET a la API
        StringRequest request = new StringRequest(Request.Method.GET, url,
        response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray results = json.getJSONArray("results");

                List<Pokemon> pokemonList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject pokemon = results.getJSONObject(i);
                    String rawName = pokemon.getString("name");
                    String name = rawName.substring(0, 1).toUpperCase() + rawName.substring(1);
                    String pokeurl = pokemon.getString("url");

                    pokemonList.add(new Pokemon(name, pokeurl ));
                }

                PokemonAdapter adapter = new PokemonAdapter(this, pokemonList);
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

        // Inicialización del menú inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Quita colores por defecto para usar los personalizados
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemTextColor(null);

        // Listener para moverse entre pantallas
        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }

            if (item.getItemId() == R.id.nav_pokedex) {
                return true;
            }

            if (item.getItemId() == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }

            return false;
        });

        queue.add(request);
    }

}
