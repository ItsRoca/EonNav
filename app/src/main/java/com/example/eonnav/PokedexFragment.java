package com.example.eonnav;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
}

