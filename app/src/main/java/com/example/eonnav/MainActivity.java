package com.example.eonnav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias
        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNav);

        // Adapter del ViewPager
        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Evita recrear fragments al cambiar
        viewPager.setOffscreenPageLimit(3);

        // Quitar colores por defecto
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemTextColor(null);

        // Opciones del BottomNavigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_pokedex) {
                viewPager.setCurrentItem(0, true);
                return true;
            }
            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(1, true);
                return true;
            }
            if (id == R.id.nav_teams) {
                viewPager.setCurrentItem(2, true);
                return true;
            }

            return false;
        });

        // Actualiza el BottomNavigation al cambiar entre fragments
        viewPager.registerOnPageChangeCallback(
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    bottomNav.getMenu().getItem(position).setChecked(true);
                }
            }
        );

        // Pagina inicial
        viewPager.setCurrentItem(1, false);
        bottomNav.setSelectedItemId(R.id.nav_home);

    }
}
