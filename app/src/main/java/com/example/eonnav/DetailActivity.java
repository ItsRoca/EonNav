package com.example.eonnav;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView detailBottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Referencias
        viewPager = findViewById(R.id.detailViewPager);
        detailBottomNav = findViewById(R.id.detailBottomNav);

        String name = getIntent().getStringExtra("name").toLowerCase();

        RequestQueue queue = Volley.newRequestQueue(this);
        Bundle pokemonData = new Bundle();
        pokemonData.putString("name", name.substring(0, 1).toUpperCase() + name.substring(1));

        // Petición /pokemon/
        StringRequest request = new StringRequest(Request.Method.GET,
            "https://pokeapi.co/api/v2/pokemon/" + name,
            response -> {
                try {
                    JSONObject json = new JSONObject(response);

                    int id = json.getInt("id");
                    double height = json.getInt("height") / 10.0;
                    double weight = json.getInt("weight") / 10.0;

                    JSONArray types = json.getJSONArray("types");
                    StringBuilder tiposStr = new StringBuilder();
                    for (int i = 0; i < types.length(); i++) {
                        if (i > 0) tiposStr.append(" / ");
                        String tipo = types.getJSONObject(i).getJSONObject("type").getString("name");
                        tiposStr.append(tipo.substring(0, 1).toUpperCase()).append(tipo.substring(1));
                    }

                    JSONArray moves = json.getJSONArray("moves");
                    StringBuilder movesStr = new StringBuilder();
                    int limit = Math.min(moves.length(), 20);
                    for (int i = 0; i < limit; i++) {
                        String move = moves.getJSONObject(i).getJSONObject("move").getString("name");
                        move = move.substring(0, 1).toUpperCase() + move.substring(1);
                        movesStr.append("• ").append(move).append("\n");
                    }

                    String imageUrl = json.getJSONObject("sprites")
                            .getJSONObject("other")
                            .getJSONObject("official-artwork")
                            .getString("front_default");

                    pokemonData.putString("number", String.format("#%03d", id));
                    pokemonData.putString("height", height + " m");
                    pokemonData.putString("weight", weight + " kg");
                    pokemonData.putString("types", tiposStr.toString());
                    pokemonData.putString("moves", movesStr.toString());
                    pokemonData.putString("imageUrl", imageUrl);

                    setupViewPagerIfReady(pokemonData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show()
        );

        // Petición /pokemon-species/
        StringRequest speciesRequest = new StringRequest(Request.Method.GET,
            "https://pokeapi.co/api/v2/pokemon-species/" + name,
            response -> {
                try {
                    JSONObject json = new JSONObject(response);

                    String gen = json.getJSONObject("generation").getString("name");
                    String genFormatted = gen.replace("generation-", "Generación ").toUpperCase()
                            .replace("GENERACIÓN ", "Generación ");

                    JSONArray entries = json.getJSONArray("flavor_text_entries");
                    String descripcion = "";
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject entry = entries.getJSONObject(i);
                        if (entry.getJSONObject("language").getString("name").equals("es")) {
                            descripcion = entry.getString("flavor_text")
                                    .replace("\n", " ").replace("\f", " ");
                            break;
                        }
                    }

                    pokemonData.putString("generation", genFormatted);
                    pokemonData.putString("description", descripcion);

                    setupViewPagerIfReady(pokemonData);

                    // Añade esto junto a los otros putString de speciesRequest
                    String evolutionChainUrl = json.getJSONObject("evolution_chain").getString("url");
                    pokemonData.putString("evolutionChainUrl", evolutionChainUrl);

                    // Formas alternativas
                    JSONArray varieties = json.getJSONArray("varieties");
                    StringBuilder formsStr = new StringBuilder();
                    for (int i = 0; i < varieties.length(); i++) {
                        String formName = varieties.getJSONObject(i)
                                .getJSONObject("pokemon")
                                .getString("name");
                        if (i > 0) formsStr.append(","); // separador para luego splitear
                        formsStr.append(formName);
                    }
                    pokemonData.putString("varieties", formsStr.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {}
        );

        queue.add(request);
        queue.add(speciesRequest);
    }

    private void setupViewPagerIfReady(Bundle data) {
        // Espera a que las dos peticiones hayan terminado
        if (!data.containsKey("number") || !data.containsKey("description")) return;

    // Adapter del ViewPager
    DetailPagerAdapter adapter = new DetailPagerAdapter(this, data);
    viewPager.setAdapter(adapter);

    // Evita recrear fragments al cambiar
    viewPager.setOffscreenPageLimit(5);

    // Opciones del BottomNavigation
    detailBottomNav.setOnItemSelectedListener(item -> {
        int id = item.getItemId();

        if (id == R.id.nav_description) {
            viewPager.setCurrentItem(0, true);
            return true;
        }
        if (id == R.id.nav_data) {
            viewPager.setCurrentItem(1, true);
            return true;
        }
        if (id == R.id.nav_family) {
            viewPager.setCurrentItem(2, true);
            return true;
        }
        if (id == R.id.nav_moves) {
            viewPager.setCurrentItem(3, true);
            return true;
        }
        if (id == R.id.nav_battleinfo) {
            viewPager.setCurrentItem(4, true);
            return true;
        }

        return false;
    });

    // Actualiza el BottomNavigation al cambiar entre fragments
    viewPager.registerOnPageChangeCallback(
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    detailBottomNav.getMenu().getItem(position).setChecked(true);
                }
            }
    );

    // Pagina inicial
    viewPager.setCurrentItem(0, false);
    detailBottomNav.setSelectedItemId(R.id.nav_description);

    }
}