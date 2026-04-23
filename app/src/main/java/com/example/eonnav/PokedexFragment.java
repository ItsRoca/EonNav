package com.example.eonnav;


import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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


public class PokedexFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PokemonAdapter adapter;
    private RequestQueue requestQueue;
    private String selectedType = null;
    private boolean onlyFavorites = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        // RecyclerView donde se mostrarán los Pokémon
        recyclerView = view.findViewById(R.id.recyclerView);
        // Barra de busqueda
        searchBar = view.findViewById(R.id.searchBar);

        //Boton de filtros
        ImageButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterDialog());

        // 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Librería volley para las peticiones HTTP
        requestQueue = Volley.newRequestQueue(requireContext());

        // URL de la PokeAPI
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=1025"; //Ver pokedex actual entera
        String url = "https://pokeapi.co/api/v2/pokemon?limit=1351"; //Ver pokedex actual entera con todas las variantes de cada pokemon




        // Peticion GET a pokeAPI
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

                adapter = new PokemonAdapter(requireContext(), pokemonList);
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


        requestQueue.add(request);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Instanciamos referencias a null para evitar errores
        recyclerView = null;
        searchBar = null;
        adapter = null;
    }

    private void loadAllPokemon() {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=1351";

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

                            pokemonList.add(new Pokemon(name, pokeurl));
                        }

                        adapter = new PokemonAdapter(requireContext(), pokemonList);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(request);
    }
    private void showFilterDialog() {

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_filters, null);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        TextView fire = view.findViewById(R.id.filterFire);
        TextView water = view.findViewById(R.id.filterWater);
        TextView grass = view.findViewById(R.id.filterGrass);
        TextView fav = view.findViewById(R.id.filterFavorites);
        Button apply = view.findViewById(R.id.applyFilters);

        fire.setOnClickListener(v -> {
                selectedType = "fire";
                selectType(fire, fire, water, grass);
        });
        water.setOnClickListener(v -> {
            selectedType = "water";
            selectType(water, fire, water, grass);
        });
        grass.setOnClickListener(v -> {
            selectedType = "grass";
            selectType(grass, fire, water, grass);
        });

        fav.setOnClickListener(v -> onlyFavorites = !onlyFavorites);
        fav.setOnClickListener(v -> {
            Log.d("DEBUG", "FAVORITOS CLICK");
        });

        apply.setOnClickListener(v -> {

            if (selectedType != null) {
                loadByType(selectedType);
            } else {
                loadAllPokemon();
            }

            dialog.dismiss();
        });

        dialog.show();

        Button clear = view.findViewById(R.id.clearFilters);

        clear.setOnClickListener(v -> {

            // reset variables del fragment
            selectedType = null;
            onlyFavorites = false;

            // reset adapter
            adapter.setTypeFilter(null);
            adapter.setOnlyFavorites(false);

            loadAllPokemon();

            dialog.dismiss();
        });
    }

    private void resetTypeUI(TextView... views) {
        for (TextView v : views) {
            v.setBackgroundColor(Color.TRANSPARENT);
            v.setTextColor(Color.BLACK);
        }
    }

    private void selectType(TextView selected, TextView... all) {
        resetTypeUI(all);

        selected.setBackgroundColor(Color.parseColor("#FFCC00"));
        selected.setTextColor(Color.BLACK);
    }

    private void loadByType(String type) {

        String url = "https://pokeapi.co/api/v2/type/" + type;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject json = new JSONObject(response);
                        JSONArray pokemonArray = json.getJSONArray("pokemon");

                        List<Pokemon> list = new ArrayList<>();

                        for (int i = 0; i < pokemonArray.length(); i++) {

                            JSONObject p = pokemonArray.getJSONObject(i)
                                    .getJSONObject("pokemon");

                            String name = p.getString("name");
                            String pokeUrl = p.getString("url");

                            String cleanName =
                                    name.substring(0, 1).toUpperCase() + name.substring(1);

                            list.add(new Pokemon(cleanName, pokeUrl));
                        }

                        adapter = new PokemonAdapter(requireContext(), list);
                        recyclerView.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(request);
    }
}

