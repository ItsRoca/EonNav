package com.example.eonnav;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {

    List<String> pokemonList;
    List<String> pokemonListFull;
    private Context context;
    public PokemonAdapter(Context context, List<String> names) {
        this.context = context;
        this.pokemonList = new ArrayList<>(names);
        this.pokemonListFull = new ArrayList<>(names);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = pokemonList.get(position);
        String[] parts = data.split("\\|");

        String name = parts[0];
        String url = parts[1];

        String[] urlParts = url.split("/");
        int id = Integer.parseInt(urlParts[urlParts.length - 1]);

        holder.textView.setText(name);

        //String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png"; // Imagenes oficiales
        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png"; // Sprites Pixel art
        //String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-i/red-blue/" + id + ".png"; //Sprites GB

        Picasso.get().load(imageUrl).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textNombre);
            imageView = itemView.findViewById(R.id.imgPokemon);
        }
    }

    public void filter(String text) {
        pokemonList.clear();

        if (text.isEmpty()) {
            pokemonList.addAll(pokemonListFull);
        } else {
            text = text.toLowerCase();
            for (String data : pokemonListFull) {
                String[] parts = data.split("\\|");
                String name = parts[0];

                if (name.toLowerCase().contains(text)) {
                    pokemonList.add(data);
                }
            }
        }

        notifyDataSetChanged();
    }
}