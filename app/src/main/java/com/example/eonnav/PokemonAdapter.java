package com.example.eonnav;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

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
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(pokemonList.get(position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", pokemonList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public void filter(String text) {
        pokemonList.clear();

        if (text.isEmpty()) {
            pokemonList.addAll(pokemonListFull);
        } else {
            text = text.toLowerCase();
            for (String name : pokemonListFull) {
                if (name.toLowerCase().contains(text)) {
                    pokemonList.add(name);
                }
            }
        }

        notifyDataSetChanged();
    }
}