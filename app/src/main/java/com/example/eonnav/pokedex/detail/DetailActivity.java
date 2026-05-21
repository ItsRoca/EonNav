package com.example.eonnav.pokedex.detail;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eonnav.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView detailBottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // REFERENCIAS
        viewPager = findViewById(R.id.detailViewPager);
        detailBottomNav = findViewById(R.id.detailBottomNav);
        detailBottomNav.setItemIconTintList(null);

        String name = getIntent().getStringExtra("name").toLowerCase();

        RequestQueue queue = Volley.newRequestQueue(this);
        Bundle pokemonData = new Bundle();
        pokemonData.putString("name", name.substring(0, 1).toUpperCase() + name.substring(1));

        StringRequest pokemonRequest = new StringRequest(
                Request.Method.GET,
                "https://pokeapi.co/api/v2/pokemon/" + name,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        //Necesario para las formas
                        String speciesUrl = json
                            .getJSONObject("species")
                            .getString("url");
                    pokemonData.putString("speciesUrl", speciesUrl);

                    int id = json.getInt("id");
                    double height = json.getInt("height") / 10.0;
                    double weight = json.getInt("weight") / 10.0;


                    JSONArray typesJson = json.getJSONArray("types");
                    ArrayList <String> typesList = new ArrayList <>();

                    for (int i = 0; i < typesJson.length(); i++) {
                        String type = typesJson
                                .getJSONObject(i)
                                .getJSONObject("type")
                                .getString("name");
                        typesList.add(type);
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

                    pokemonData.putString("number", String.format("#%03d", id)); // Formato de texto específico
                    pokemonData.putString("height", height + " m");
                    pokemonData.putString("weight", weight + " kg");
                    pokemonData.putStringArrayList("types", typesList);
                    pokemonData.putString("moves", movesStr.toString());
                    pokemonData.putString("imageUrl", imageUrl);

                    setupViewPagerIfReady(pokemonData);

                    JSONArray abilitiesJson = json.getJSONArray("abilities");
                    ArrayList<String> abilitiesList = new ArrayList<>();

                    for (int i = 0; i < abilitiesJson.length(); i++) {
                        JSONObject abilityObj = abilitiesJson.getJSONObject(i);
                        String abilityName = abilityObj
                                .getJSONObject("ability")
                                .getString("name");

                        boolean isHidden = abilityObj.getBoolean("is_hidden");

                        // Formatear nombre
                        abilityName = abilityName.substring(0, 1).toUpperCase()
                                + abilityName.substring(1).replace("-", " ");

                        if (isHidden) {
                            abilityName += " (Oculta)";
                        }

                        abilitiesList.add(abilityName);
                    }

                    pokemonData.putStringArrayList("abilities", abilitiesList);

                    loadSpecies(queue, pokemonData);

                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show()
        );

        queue.add(pokemonRequest);
    }

    private void loadSpecies(RequestQueue queue, Bundle pokemonData) {

        String speciesUrl = pokemonData.getString("speciesUrl");

        StringRequest speciesRequest = new StringRequest(
                Request.Method.GET,
                speciesUrl,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        // Descripción en español
                        JSONArray entries = json.getJSONArray("flavor_text_entries");
                        String description = "";
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            if (entry.getJSONObject("language").getString("name").equals("es")) {
                                description = entry.getString("flavor_text")
                                        .replace("\n", " ")
                                        .replace("\f", " ");
                                break;
                            }
                        }
                        pokemonData.putString("description", description);

                        // Grupo huevo
                        JSONArray eggArray = json.getJSONArray("egg_groups");
                        ArrayList<String> eggs = new ArrayList<>();
                        for (int i = 0; i < eggArray.length(); i++) {
                            String egg = eggArray.getJSONObject(i).getString("name");
                            eggs.add(
                                    egg.substring(0, 1).toUpperCase()
                                            + egg.substring(1)
                            );
                        }
                        pokemonData.putString("eggGroups", String.join(" / ", eggs));

                        // Cadena evolutiva
                        pokemonData.putString(
                                "evolutionChainUrl",
                                json.getJSONObject("evolution_chain").getString("url")
                        );

                        // Formas alternativas
                        JSONArray varieties = json.getJSONArray("varieties");
                        StringBuilder formsStr = new StringBuilder();
                        for (int i = 0; i < varieties.length(); i++) {
                            if (i > 0) formsStr.append(",");
                            formsStr.append(
                                    varieties.getJSONObject(i)
                                            .getJSONObject("pokemon")
                                            .getString("name")
                            );
                        }
                        pokemonData.putString("varieties", formsStr.toString());

                        // Montar el ViewPager
                        setupViewPagerIfReady(pokemonData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

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
        if (id == R.id.nav_battleinfo) {
            viewPager.setCurrentItem(1, true);
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