package com.example.eonnav;


import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.eonnav.utils.TypeUtils;
import androidx.lifecycle.ViewModelProvider;



public class PokedexFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PokemonAdapter adapter;
    private RequestQueue requestQueue;
    private PokedexViewModel viewModel;



    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        viewModel = new ViewModelProvider(this).get(PokedexViewModel.class);

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

    // Redimensionar iconos
    private void setScaledTypeIcon(TextView textView, String type) {
        Drawable drawable = ContextCompat.getDrawable(
                requireContext(),
                TypeUtils.getTypeIcon(type)
        );

        if (drawable == null) return;

        // Tamaño objetivo en dp (ALTURA)
        int targetHeightDp = 22;
        float density = getResources().getDisplayMetrics().density;
        int targetHeightPx = (int) (targetHeightDp * density);

        // Tamaño original del drawable
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) return;

        // Calcular ancho manteniendo proporcion
        float ratio = (float) intrinsicWidth / intrinsicHeight;
        int targetWidthPx = (int) (targetHeightPx * ratio);

        // Aplicar tamaño proporcional
        drawable.setBounds(0, 0, targetWidthPx, targetHeightPx);

        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(
                (int) (8 * density)
        );
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

        // Lista de tipos
        List<String> types = new ArrayList<>();
        types.add("fire");
        types.add("water");
        types.add("grass");
        types.add("electric");
        types.add("ice");
        types.add("fighting");
        types.add("poison");
        types.add("ground");
        types.add("flying");
        types.add("psychic");
        types.add("bug");
        types.add("rock");
        types.add("ghost");
        types.add("dragon");
        types.add("dark");
        types.add("steel");
        types.add("fairy");

        // Adapter iconos
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

        //Favoritos
        fav.setOnClickListener(v -> viewModel.onlyFavorites = !viewModel.onlyFavorites);

        //Aplicar filtros
        apply.setOnClickListener(v -> {

            if (viewModel.selectedType != null) {
                loadByType(viewModel.selectedType);
            } else {
                loadAllPokemon();
            }

            dialog.dismiss();
        });

        // Limpiar filtros activos
        clear.setOnClickListener(v -> {

            // reset variables del fragment
            viewModel.selectedType = null;
            viewModel.onlyFavorites = false;

            // reset adapter
            adapter.setTypeFilter(null);
            adapter.setOnlyFavorites(false);

            loadAllPokemon();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupTypeFilter(
            TextView view,
            String type,
            TextView... allViews
    ) {
        view.setOnClickListener(v -> {
            viewModel.selectedType = type;
            selectType(view, allViews);
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

