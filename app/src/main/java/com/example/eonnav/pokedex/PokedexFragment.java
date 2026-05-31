package com.example.eonnav.pokedex;


import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import com.example.eonnav.pokemon.Pokemon;
import com.example.eonnav.pokemon.PokemonAdapter;
import com.example.eonnav.R;
import com.example.eonnav.TypeDropdownAdapter;
import com.example.eonnav.utils.TypeUtils;
import androidx.lifecycle.ViewModelProvider;


public class PokedexFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PokemonAdapter adapter;
    private RequestQueue requestQueue;
    private PokedexViewModel viewModel;

    // URL de la PokeAPI
    //private static final String POKEMON_LIST_URL = "https://pokeapi.co/api/v2/pokemon?limit=151";// 1ª Gen
    //private static final String POKEMON_LIST_URL = "https://pokeapi.co/api/v2/pokemon?limit=1025";// Pokedex Nacional
    private static final String POKEMON_LIST_URL = "https://pokeapi.co/api/v2/pokemon?limit=1351"; // Nacional + variantes

    List<String> favoriteNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        viewModel = new ViewModelProvider(this).get(PokedexViewModel.class);

        // RecyclerView donde se mostraran los Pokémon
        recyclerView = view.findViewById(R.id.recyclerView);
        // Barra de busqueda
        searchBar = view.findViewById(R.id.searchBar);

        //Boton de filtros
        ImageButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterDialog());

        // 2 columnas de cards
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Librería volley para las peticiones HTTP
        requestQueue = Volley.newRequestQueue(requireContext());

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Peticion GET a pokeAPI
        if (viewModel.cachedPokemonList != null) {

            adapter = new PokemonAdapter(requireContext(), viewModel.cachedPokemonList);
            recyclerView.setAdapter(adapter);

        } else {
            loadAllPokemon();
        }

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

        StringRequest request = new StringRequest(Request.Method.GET, POKEMON_LIST_URL,
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

                    viewModel.cachedPokemonList = pokemonList;
                    adapter = new PokemonAdapter(requireContext(), pokemonList);
                    recyclerView.setAdapter(adapter);
                    adapter.setFavoriteNames(favoriteNames);

                    adapter.setOnlyFavorites(viewModel.onlyFavorites);
                    adapter.filter(searchBar.getText().toString());


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

        AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .create();

        AutoCompleteTextView typeDropdown = view.findViewById(R.id.typeDropdown);
        TextView fav = view.findViewById(R.id.filterFavorites);
        Button apply = view.findViewById(R.id.applyFilters);
        Button clear = view.findViewById(R.id.clearFilters);

        List<String> types = TypeUtils.getAllTypes();

        TypeDropdownAdapter typeAdapter = new TypeDropdownAdapter(requireContext(), types);

        typeDropdown.setAdapter(typeAdapter);

        typeDropdown.setOnClickListener(v -> typeDropdown.showDropDown());

        // Seleccion de tipo
        typeDropdown.setOnItemClickListener((parent, v, position, id) -> {

            String type = types.get(position);
            viewModel.selectedType = types.get(position);

            // Forzar que no haya texto
            typeDropdown.setText("", false);


            // Poner el icono
            Drawable drawable = ContextCompat.getDrawable(
                    requireContext(),
                    TypeUtils.getTypeIcon(type)
            );

            if (drawable != null) {
                float density = getResources().getDisplayMetrics().density;
                int targetHeight = (int) (20 * density);

                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                float ratio = (float) w / h;

                int targetWidth = (int) (targetHeight * ratio);
                drawable.setBounds(0, 0, targetWidth, targetHeight);

                typeDropdown.setCompoundDrawables(drawable, null, null, null);
                typeDropdown.setCompoundDrawablePadding(
                        (int) (8 * density)
                );
            }

        });

        typeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                typeDropdown.showDropDown();
            }
        });


        //Favoritos
        fav.setOnClickListener(v -> {
            viewModel.onlyFavorites = !viewModel.onlyFavorites;

            if (viewModel.onlyFavorites) {
                fav.setBackgroundColor(Color.parseColor("#FFD54F")); // Indicar seleccionado
            } else {
                fav.setBackgroundColor(Color.TRANSPARENT);
            }
        });


        if (viewModel.selectedType != null) {

            Drawable drawable = ContextCompat.getDrawable(
                requireContext(),
                TypeUtils.getTypeIcon(viewModel.selectedType)
            );

            if (drawable != null) {
                float density = getResources().getDisplayMetrics().density;
                int targetHeight = (int) (20 * density);

                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                float ratio = (float) w / h;

                int targetWidth = (int) (targetHeight * ratio);
                drawable.setBounds(0, 0, targetWidth, targetHeight);

                typeDropdown.setCompoundDrawables(drawable, null, null, null);
            }
        }

        //Aplicar filtros
        apply.setOnClickListener(v -> {

            loadFavorites(() -> {

                if (adapter == null && viewModel.selectedType == null) return;

                if (viewModel.selectedType != null) {

                    loadByType(viewModel.selectedType);

                } else {

                    adapter.setFavoriteNames(favoriteNames);
                    adapter.setOnlyFavorites(viewModel.onlyFavorites);
                    adapter.filter(searchBar.getText().toString());

                }

            });

            dialog.dismiss();
        });


        // Limpiar filtros activos
        clear.setOnClickListener(v -> {

            // Reset variables del fragment
            viewModel.selectedType = null;
            viewModel.onlyFavorites = false;

            // Reset adapter
            adapter.setOnlyFavorites(false);
            adapter.setSelectedType(null);

            loadAllPokemon();

            dialog.dismiss();
        });

        dialog.show();
    }

    // FILTRO DE TIPOS
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

                        if (viewModel.onlyFavorites) {

                            List<Pokemon> filtered = new ArrayList<>();

                            for (Pokemon p : list) {
                                if (favoriteNames.contains(p.getName().toLowerCase())) {
                                    filtered.add(p);
                                }
                            }

                            list = filtered;
                        }

                        adapter = new PokemonAdapter(requireContext(), list);
                        recyclerView.setAdapter(adapter);

                        adapter.setFavoriteNames(favoriteNames);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(request);
    }

    // FILTRO DE FAVORITOS
    private void loadFavorites(Runnable onFinished) {

        String url = "http://10.0.2.2:8000/api/favorites/?user_id=1";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        favoriteNames.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            favoriteNames.add(obj.getString("pokemon_name").toLowerCase());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    onFinished.run();
                },
                error -> {
                    error.printStackTrace();
                    onFinished.run();
                }
        );

        requestQueue.add(request);
    }
}

