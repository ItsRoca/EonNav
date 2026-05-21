package com.example.eonnav.pokemon;

import static com.example.eonnav.utils.TypeUtils.darkenColor;
import static com.example.eonnav.utils.TypeUtils.getContrastColor;
import static com.example.eonnav.utils.TypeUtils.getTypeColor;

import android.content.Context;
import android.content.Intent;
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
    private final Context context;
    private final RequestQueue queue;
    private String selectedType = null;
    private String searchText = "";
    private Boolean onlyFavorites = false;
    private List<String> favoriteNames = new ArrayList<>();


    public void setFavoriteNames(List<String> favorites) {
        this.favoriteNames = favorites;
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
                                .error(R.drawable.icon_pokeball_2)
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

            boolean matchesFav = !onlyFavorites ||
                    (favoriteNames != null &&
                            favoriteNames.contains(p.getName().toLowerCase()));

            boolean matchesType = true;

            if (selectedType != null) {
                List<String> types = p.getTypes();

                matchesType = types != null && types.contains(selectedType);
            }

            if (matchesSearch && matchesFav && matchesType) {
                pokemonList.add(p);
            }
        }

        notifyDataSetChanged();
    }


    public void setOnlyFavorites(boolean value) {
        this.onlyFavorites = value;
    }


    public void setSelectedType(String type) {
        this.selectedType = type;
    }



}