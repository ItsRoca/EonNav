package com.example.eonnav.teams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eonnav.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamViewHolder> {

    // DATOS //
    private List<Team> teams = new ArrayList<>();
    private OnTeamClickListener listener;

    // CLICK //
    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    // CONSTRUCTOR //
    public TeamsAdapter(OnTeamClickListener listener) {
        this.listener = listener;
    }

    // ACTUALIZAR DATOS
    public void setTeams(List<Team> teams) {
        this.teams.clear();
        this.teams.addAll(teams);
        notifyDataSetChanged();
    }

    // CREAR VIEW HOLDER
    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    // BIND DE DATOS
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.bind(teams.get(position));
    }

    //TOTAL ITEMS
    @Override
    public int getItemCount() {
        return teams.size();
    }

    // VIEW HOLDER
    class TeamViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView[] slots = new ImageView[6];

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTeamName);

            // Inicializar slots
            slots[0] = itemView.findViewById(R.id.slot1);
            slots[1] = itemView.findViewById(R.id.slot2);
            slots[2] = itemView.findViewById(R.id.slot3);
            slots[3] = itemView.findViewById(R.id.slot4);
            slots[4] = itemView.findViewById(R.id.slot5);
            slots[5] = itemView.findViewById(R.id.slot6);

        }

        // BIND DE UN OBJETO
        public void bind(final Team team) {
            tvName.setText(team.getName());

            List<PokemonData> pokemons = team.getPokemons();

            for (int i = 0; i < slots.length; i++) {

                if (pokemons != null && i < pokemons.size()) {

                    int id = pokemons.get(i).id;

                    String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                            + id + ".png";

                    Picasso.get().load(url).into(slots[i]);

                } else {
                    slots[i].setImageResource(R.drawable.icon_pokeball_2);
                }
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTeamClick(team);
                }
            });

        }
    }
}
