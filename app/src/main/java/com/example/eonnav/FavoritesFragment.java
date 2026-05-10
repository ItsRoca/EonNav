
package com.example.eonnav;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eonnav.pokemon.Pokemon;
import com.example.eonnav.pokemon.PokemonAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private PokemonAdapter adapter;

    @Nullable
    @Override


    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerFavorites);
        emptyText = view.findViewById(R.id.textEmpty);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Cargar datos aqui
        cargarFavoritos();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        cargarFavoritos();
    }

    private void cargarFavoritos() {

        SharedPreferences prefs = requireContext().getSharedPreferences("favorites", requireContext().MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        List<String> favoriteNames = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                favoriteNames.add(entry.getKey());
            }
        }

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

            adapter = new PokemonAdapter(requireContext(), favoritePokemon);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        emptyText = null;
        adapter = null;
    }
}
