
package com.example.eonnav;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.eonnav.utils.Favorite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;


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

        Log.d("FAV", "Entrando a cargarFavoritos");

        ApiService api = RetrofitInstance.getRetrofit().create(ApiService.class);


        int userId = 1;
        api.getFavorites(userId).enqueue(new retrofit2.Callback<List<Favorite>>() {

            @Override
            public void onResponse(Call<List<Favorite>> call, retrofit2.Response<List<Favorite>> response) {


                Log.d("FAV", "Código HTTP: " + response.code());

                if (response.body() != null) {
                    Log.d("FAV", "Body recibido");
                } else {
                    Log.d("FAV", "Body es NULL");
                }

                if (response.isSuccessful() && response.body() != null) {

                    List<Favorite> favorites = response.body();
                    Log.d("FAV", "Favoritos: " + favorites.size());

                } else {
                    Log.d("FAV", "Error en respuesta");
                }
                if (response.isSuccessful() && response.body() != null) {

                    List<Favorite> favorites = response.body();

                    Log.d("FAV", "Favoritos recibidos: " + favorites.size());

                    List<Pokemon> favoritePokemon = new ArrayList<>();

                    for (Favorite fav : favorites) {
                        String name = fav.pokemon_name;
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
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        emptyText = null;
        adapter = null;
    }
}
