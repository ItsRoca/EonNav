package com.example.eonnav;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botones de juegos
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);

        btn1.setOnClickListener(v -> abrirWeb("https://victoryroad.pro/"));
        btn2.setOnClickListener(v -> abrirWeb("https://play.pokemonshowdown.com/"));
        btn3.setOnClickListener(v -> abrirWeb("https://nerd-of-now.github.io/NCP-VGC-Damage-Calculator/"));
        btn4.setOnClickListener(v -> abrirWeb("https://munchstats.com/"));
        btn5.setOnClickListener(v -> abrirWeb("https://pokedle.net/"));
        btn6.setOnClickListener(v -> abrirWeb("https://pokedoku.com/"));
        btn7.setOnClickListener(v -> abrirWeb("https://pkmnquiz.com/"));
        btn8.setOnClickListener(v -> abrirWeb("https://cajunavenger.github.io/"));


        // Menú de navegación inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemTextColor(null);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            }
            if (item.getItemId() == R.id.nav_pokedex) {
                startActivity(new Intent(this, PokedexActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }
            return false;
        });
    }

    private void abrirWeb(String url) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}