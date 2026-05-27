package com.example.eonnav.teams;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eonnav.PokeApiService;
import com.example.eonnav.R;
import com.example.eonnav.RetrofitInstance;
import com.example.eonnav.teams.teambuilder.TeamsTeamBuilderActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamsFragment extends Fragment {

    // VARIABLES //
    private RecyclerView recyclerView;
    private TeamsAdapter adapter;
    private List<Team> allTeams = new ArrayList<>();

    // CONSTRUCTOR //
    public TeamsFragment() {
        super(R.layout.fragment_teams);
    }

    // INTERFAZ //
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vista
        recyclerView = view.findViewById(R.id.rvTeams);
        EditText etSearch = view.findViewById(R.id.teamSearch);
        View fab = view.findViewById(R.id.fabCreateTeam);

        adapter = new TeamsAdapter(this::openTeam);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTeams(null);    // Cargar equipos desde el backend

        // BUSCADOR EQUIPOS //
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadTeams(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Crear equipo
        fab.setOnClickListener(v -> openTeam(null));    // Abre sin ID para crear un nuevo equipo
    }

    // CARGAR DATOS //

    // Traer equipos del backend
    private void loadTeams(String query) {

        RetrofitInstance
                .getRetrofit()
                .create(PokeApiService.class)
                .getTeams(query)
                .enqueue(new Callback<List<Team>>() {

                    @Override
                    public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {

                        Log.d("API", "Code: " + response.code());
                        Log.d("API", "Raw: " + response.raw());

                        if (response.isSuccessful() && response.body() != null) {

                            List<Team> result = response.body();

                            Log.d("API", "Equipos recibidos: " + result.size());

                            allTeams = result;
                            adapter.setTeams(allTeams);

                        } else {
                            Log.e("API", "ERROR BODY: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Team>> call, Throwable t) {
                        Log.e("API", "Error: " + t.getMessage());
                    }
                });
    }

    // REFRESH //

    // Usado al volver al fragment tras editar o borrar)
    @Override
    public void onResume() {
        super.onResume();
        loadTeams("");
    }

    // ABRIR CREAR/EDITAR EQUIPO //
    private void openTeam(Team team) {
        Intent intent = new Intent(getContext(), TeamsTeamBuilderActivity.class);

        // Si existe el equipo --> editar
        if (team != null) {
            intent.putExtra("team_id", team.getId());
        }

        // Si no existe --> Creamos nuevo equipo
        startActivity(intent);
    }
}