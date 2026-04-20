package com.example.eonnav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        RecyclerView recyclerView = findViewById(R.id.recyclerFavorites);
        TextView emptyText = findViewById(R.id.textEmpty);

        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        List<String> favoriteNames = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                favoriteNames.add(entry.getKey());
            }
        }

        // Convertir los nombres a objetos Pokemon
        List<Pokemon> favoritePokemon = new ArrayList<>();
        for (String name : favoriteNames) {
            String url = "https://pokeapi.co/api/v2/pokemon/" + name;
            favoritePokemon.add(new Pokemon(name, url));
        }

        if (favoritePokemon.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            PokemonAdapter adapter = new PokemonAdapter(this, favoritePokemon);
            recyclerView.setAdapter(adapter);
        }

        // Inicialización del menú inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_favorites);


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
                startActivity(new Intent(this, PokedexActivity.class));
                return true;
            }

            if (item.getItemId() == R.id.nav_favorites) {
                return true;
            }

            return false;
        });
    }

}