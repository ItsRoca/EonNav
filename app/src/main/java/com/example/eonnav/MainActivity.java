package com.example.eonnav;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.buttonPokedex);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemTextColor(null);
        bottomNav.setSelectedItemId(R.id.nav_home);

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
                // futura pantalla de favoritos
                return true;
            }

            return false;
        });

        WebView webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.loadUrl("https://pokedoku.com/");


    }


}