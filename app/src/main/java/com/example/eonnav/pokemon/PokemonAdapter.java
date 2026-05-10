package com.example.eonnav.pokemon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eonnav.R;
import com.example.eonnav.pokedex.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {

    List<Pokemon> pokemonList;
    List<Pokemon> pokemonListFull;
    private Context context;
    private RequestQueue queue;

    private String searchText = "";
    private String typeFilter = null;
    private Boolean onlyFavorites = false;
    private boolean isFavorite(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        return prefs.getBoolean(name, false);
    }
    public PokemonAdapter(Context context, List<Pokemon> names) {
        this.context = context;
        this.pokemonList = new ArrayList<>(names);
        this.pokemonListFull = new ArrayList<>(names);
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);

        String name = pokemon.getName();
        String url = pokemon.getUrl();


        holder.textView.setText(name);
        holder.cardBackground.setBackgroundResource(R.drawable.pokemon_card);

        StringRequest typeRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        JSONObject sprites = json.getJSONObject("sprites");

                        pokemon.setId(json.getInt("id"));

                        JSONArray types = json.getJSONArray("types");

                        List<String> typeList = new ArrayList<>();

                        for (int i = 0; i < types.length(); i++) {
                            String type = types.getJSONObject(i)
                                    .getJSONObject("type")
                                    .getString("name");

                            typeList.add(type);
                        }

                        pokemon.setTypes(typeList);

                        // Pixel sprite (puede ser null)
                        String pixelSprite = sprites.getString("front_default");

                        // Official artwork (existe 100%)
                        String officialSprite = sprites
                                .getJSONObject("other")
                                .getJSONObject("official-artwork")
                                .getString("front_default");

                        // Elegir cual usar
                        String imageUrl;

                        if (pixelSprite != null && !pixelSprite.equals("null")) {
                            imageUrl = pixelSprite; // URL a pixelSprites
                        } else {
                            imageUrl = officialSprite; // URL a sprites oficiales (por si no existe el pixelSprite)
                        }

                        // Limpiar imagen previa
                        holder.imageView.setImageDrawable(null);

                        // Cargar imagen
                        Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.pokemon_card)
                                .error(R.drawable.icon_pokeball)
                                .into(holder.imageView);

                        String type1 = types.getJSONObject(0)
                                .getJSONObject("type")
                                .getString("name");

                        String type2 = null;
                        if (types.length() > 1) {
                            type2 = types.getJSONObject(1)
                                    .getJSONObject("type")
                                    .getString("name");
                        }

                        applyColors(holder, type1, type2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(typeRequest);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("url", url);
            context.startActivity(intent);
        });
    }

    private void applyColors(ViewHolder holder, String type1, String type2) {
        Drawable bg = holder.cardBackground.getBackground().mutate();

        if (bg instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) bg;

            int color1 = getTypeColor(type1);
            drawable.setColor(color1);

            int textColor = getContrastColor(color1);
            holder.textView.setTextColor(textColor);

            if (type2 != null) {
                int color2 = getTypeColor(type2);
                drawable.setStroke(6, color2);
            } else {
                drawable.setStroke(6, darkenColor(color1));
            }
        }
    }

    private int getTypeColor(String type) {
        switch (type) {
            case "fire": return Color.parseColor("#F08030");
            case "water": return Color.parseColor("#6890F0");
            case "grass": return Color.parseColor("#78C850");
            case "electric": return Color.parseColor("#F8D030");
            case "ice": return Color.parseColor("#98D8D8");
            case "fighting": return Color.parseColor("#C03028");
            case "poison": return Color.parseColor("#A040A0");
            case "ground": return Color.parseColor("#E0C068");
            case "flying": return Color.parseColor("#A890F0");
            case "psychic": return Color.parseColor("#F85888");
            case "bug": return Color.parseColor("#A8B820");
            case "rock": return Color.parseColor("#B8A038");
            case "ghost": return Color.parseColor("#705898");
            case "dragon": return Color.parseColor("#7038F8");
            case "dark": return Color.parseColor("#705848");
            case "steel": return Color.parseColor("#B8B8D0");
            case "fairy": return Color.parseColor("#EE99AC");
            case "normal": return Color.parseColor("#A8A878");
            default: return Color.GRAY;
        }
    }

    private int darkenColor(int color) {
        float factor = 0.7f;
        return Color.rgb(
                (int)(Color.red(color) * factor),
                (int)(Color.green(color) * factor),
                (int)(Color.blue(color) * factor)
        );
    }

    private int getContrastColor(int color) {
        double luminance = (0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255;

        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        View cardBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textNombre);
            imageView = itemView.findViewById(R.id.imgPokemon);
            cardBackground = itemView.findViewById(R.id.cardBackground);
        }
    }

    public void filter(String text) {

        searchText = text.toLowerCase().trim();

        pokemonList.clear();

        for (Pokemon p : pokemonListFull) {

            String name = p.getName().toLowerCase();

            boolean matchesSearch = searchText.isEmpty() || name.contains(searchText);

            boolean matchesType = typeFilter == null || (p.getTypes() != null && p.getTypes().contains(typeFilter));

            boolean matchesFav = !onlyFavorites || isFavorite(context, p.getName().toLowerCase());

            if (matchesSearch && matchesType && matchesFav) {
                pokemonList.add(p);
            }
        }


        notifyDataSetChanged();
    }

    public void setTypeFilter(String type) {
        this.typeFilter = type;
    }

    public void setOnlyFavorites(boolean value) {
        this.onlyFavorites = value;
    }

}