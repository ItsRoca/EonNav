package com.example.eonnav.teams.teambuilder;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eonnav.PokeApiService;
import com.example.eonnav.R;
import com.example.eonnav.RetrofitInstance;
import com.example.eonnav.pokemon.PokemonResponse;
import com.example.eonnav.teams.PokemonData;
import com.example.eonnav.teams.Team;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamsTeamBuilderActivity extends AppCompatActivity {

    // VARIABLES //
    private PokeApiService backendApi;
    private PokeApiService pokeApi;
    private List<PokemonTeam> team = new ArrayList<>();
    private static List<String> pokemonNames = new ArrayList<>();
    private static List<String> abilityNames = new ArrayList<>();
    private static List<String> moveNames = new ArrayList<>();
    private List<String> natureNames = new ArrayList<>();
    private int teamId;
    private EditText etTeamName;
    private Button btnDeleteTeam;
    private final int[] SLOT_IDS = {
            R.id.pokemon1, R.id.pokemon2, R.id.pokemon3,
            R.id.pokemon4, R.id.pokemon5, R.id.pokemon6
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_team_builder);

        initViews();
        initApis();
        initTeam();
        initToolbar();
        initSlots();

        loadTeam();
        loadStaticData();

        setupDeleteButton();
        loadNatures();
    }


    // INITS //

    // Inicializar equipo
    // Mostrar boton "Eliminar equipo" al editar
    private void initViews() {
        teamId = getIntent().getIntExtra("team_id", -1);

        etTeamName = findViewById(R.id.etTeamName);
        btnDeleteTeam = findViewById(R.id.btnDeleteTeam);

        btnDeleteTeam.setVisibility(teamId != -1 ? View.VISIBLE : View.GONE);
    }

    // Conexion con las APIs
    private void initApis() {
        backendApi = RetrofitInstance.getRetrofit().create(PokeApiService.class);

        Retrofit retrofitPoke = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pokeApi = retrofitPoke.create(PokeApiService.class);
    }

    // Equipo vacio
    private void initTeam() {
        for (int i = 0; i < 6; i++) {
            team.add(new PokemonTeam());
        }
    }

    // Barra superior
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // Asignar interaccion a los slots del equipo
    private void initSlots() {
        for (int i = 0; i < SLOT_IDS.length; i++) {
            int position = i;
            ImageView slot = findViewById(SLOT_IDS[i]);
            slot.setOnClickListener(v -> onPokemonSlotClicked(position));
        }
    }

    // Cargar datos desde pokeAPI (solo si no hay datos cargados)
    private void loadStaticData() {
        if (pokemonNames.isEmpty()) loadPokemonNames();
        if (abilityNames.isEmpty()) loadAbilityNames();
        if (moveNames.isEmpty()) loadMoveNames();
    }

    // Listar naturalezas
    private void loadNatures() {
        natureNames = Arrays.asList(
                "Hardy ( - )",
                "Adamant (+Atk; -SpA)",
                "Modest (+SpA; -Atk)",
                "Jolly (+Spe; -SpA)",
                "Timid (+Spe; -Atk)",
                "Bold (+Def; -Atk)",
                "Calm (+SpD; -Atk)",
                "Impish (+Def; -SpA)",
                "Careful (+SpD; -SpA)",
                "Brave (+Atk; -Spe)",
                "Quiet (+SpA; -Spe)",
                "Relaxed (+Def; -Spe)",
                "Sassy (+SpD; -Spe)"
        );
    }


    // MENU //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_builder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveTeam();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // TEAM //

    // Guardar equipo
    private void saveTeam() {

        List<PokemonData> backendList = new ArrayList<>();

        for (PokemonTeam p : team) {
            if (p.id != 0) {
                PokemonData data = new PokemonData();

                data.id = p.id;
                data.pokemonName = p.pokemonName;
                data.ability = p.ability;
                data.nature = p.nature;
                data.move1 = p.move1;
                data.move2 = p.move2;
                data.move3 = p.move3;
                data.move4 = p.move4;

                backendList.add(data);
            }
        }

        String teamName = etTeamName.getText().toString().trim();
        if (teamName.isEmpty()) teamName = "Equipo sin nombre";

        Team teamToSend = new Team(teamId == -1 ? -1 : teamId, teamName, backendList);

        if (teamId == -1) {
            // Crear equipo
            backendApi.saveTeam(teamToSend).enqueue(createCallback());
        } else {
            // Editar equipo
            backendApi.updateTeam(teamId, teamToSend).enqueue(updateCallback());
        }

    }
    // Callback crear equipo
    private Callback<Void> createCallback() {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(TeamsTeamBuilderActivity.this, "Equipo creado", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TeamsTeamBuilderActivity.this, "Error al crear", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //Callback editar equipo
    private Callback<Void> updateCallback() {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(TeamsTeamBuilderActivity.this, "Equipo actualizado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TeamsTeamBuilderActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Boton de borrado
    private void setupDeleteButton() {
        btnDeleteTeam.setOnClickListener(v -> showDeleteConfirmation());
    }

    // Confirmacion de borrado
    private void showDeleteConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar equipo")
                .setMessage("¿Seguro que quieres eliminar este equipo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deleteTeam();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Borrar equipo
    private void deleteTeam() {
        backendApi.deleteTeam(teamId).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() >= 200 && response.code() < 300) {
                    Toast.makeText(TeamsTeamBuilderActivity.this, "Equipo eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(
                            TeamsTeamBuilderActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TeamsTeamBuilderActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cargar equipo
    private void loadTeam() {

        clearTeamUI();

        backendApi.getTeam(teamId).enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                Team t = response.body();
                etTeamName.setText(t.getName());

                if (t.getPokemons() == null) return;

                for (int i = 0; i < t.getPokemons().size(); i++) {
                    applyPokemonData(i, t.getPokemons().get(i));
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                clearTeamUI();
                Toast.makeText(TeamsTeamBuilderActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearTeamUI() {
        for (int i = 0; i < team.size(); i++) {
            team.get(i).id = 0;
            updatePokemonSlot(i, null);
        }
    }

    // Cargar pokemon desde backend y actualizar imagen
    private void applyPokemonData(int position, PokemonData data) {
        PokemonTeam p = team.get(position);

        p.id = data.id;
        p.pokemonName = data.pokemonName;
        p.ability = data.ability;
        p.nature = data.nature;
        p.move1 = data.move1;
        p.move2 = data.move2;
        p.move3 = data.move3;
        p.move4 = data.move4;

        String imageUrl = getPokemonImage(p.id);
        p.imageUrl = imageUrl;

        updatePokemonSlot(position, imageUrl);
    }

    // HELPERS //
    private void updatePokemonSlot(int position, String imageUrl) {
        ImageView slot = findViewById(SLOT_IDS[position]);

        if (imageUrl == null) {
            slot.setImageResource(R.drawable.icon_pokeball_2);
            return;
        }

        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerInside()
                .placeholder(R.drawable.icon_pokeball_2)
                .error(R.drawable.icon_pokeball_2)
                .into(slot);
    }

    // Recibir imagenes en funcion del id
    private String getPokemonImage(int id) {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                + id + ".png";
    }

    // Click en pokeball
    private void onPokemonSlotClicked(int position) {
        showPokemonDialog(position);
    }

    // DIALOG //
    private void showPokemonDialog(int position) {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pokemon_data, null);

        AutoCompleteTextView etName = view.findViewById(R.id.etName);
        AutoCompleteTextView etAbility = view.findViewById(R.id.spAbility);

        AutoCompleteTextView etMove1 = view.findViewById(R.id.move1);
        AutoCompleteTextView etMove2 = view.findViewById(R.id.move2);
        AutoCompleteTextView etMove3 = view.findViewById(R.id.move3);
        AutoCompleteTextView etMove4 = view.findViewById(R.id.move4);

        Spinner spNature = view.findViewById(R.id.spNature);
        Button btnSave = view.findViewById(R.id.btnSavePokemon);

        // Adapters
        etName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pokemonNames));
        etAbility.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, abilityNames));

        ArrayAdapter<String> moveAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, moveNames);
        etMove1.setAdapter(moveAdapter);
        etMove2.setAdapter(moveAdapter);
        etMove3.setAdapter(moveAdapter);
        etMove4.setAdapter(moveAdapter);

        spNature.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, natureNames));

        etName.setThreshold(1);
        etAbility.setThreshold(1);

        etMove1.setThreshold(1);
        etMove2.setThreshold(1);
        etMove3.setThreshold(1);
        etMove4.setThreshold(1);

        PokemonTeam p = team.get(position);

        // Cargar datos existentes
        etName.setText(p.pokemonName);
        etAbility.setText(p.ability);
        etMove1.setText(p.move1);
        etMove2.setText(p.move2);
        etMove3.setText(p.move3);
        etMove4.setText(p.move4);
        if (p.nature != null) {
            int index = natureNames.indexOf(p.nature);
            if (index >= 0) {
                spNature.setSelection(index);
            }
        }


        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().toLowerCase().trim();

            pokeApi.getPokemon(name).enqueue(new Callback<PokemonResponse>() {
                @Override
                public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {

                    if (!response.isSuccessful() || response.body() == null) {
                        etName.setError("Pokémon no existe");
                        return;
                    }

                    int id = response.body().id;
                    String imageUrl = getPokemonImage(id);

                    PokemonTeam p = team.get(position);
                    p.id = id;
                    p.imageUrl = imageUrl;

                    updatePokemonSlot(position, imageUrl);
                }

                @Override
                public void onFailure(Call<PokemonResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            p.pokemonName = name;
            p.ability = etAbility.getText().toString();
            p.nature = spNature.getSelectedItem().toString();
            p.move1 = etMove1.getText().toString();
            p.move2 = etMove2.getText().toString();
            p.move3 = etMove3.getText().toString();
            p.move4 = etMove4.getText().toString();

            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    // CARGAR INFORMACION //

    // Nombres
    private void loadPokemonNames() {
        pokeApi.getPokemonList(1351).enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                pokemonNames.clear();

                for (PokemonListResponse.Result p : response.body().results) {
                    pokemonNames.add(p.name);
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Habilidades
    private void loadAbilityNames() {
        pokeApi.getAbilityList(350).enqueue(new Callback<AbilityListResponse>() {
            @Override
            public void onResponse(Call<AbilityListResponse> call, Response<AbilityListResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                abilityNames.clear();

                for (AbilityListResponse.Result a : response.body().results) {
                    abilityNames.add(a.name);
                }
            }

            @Override
            public void onFailure(Call<AbilityListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Movimientos
    private void loadMoveNames() {
        pokeApi.getMoveList(920).enqueue(new Callback<MoveListResponse>() {
            @Override
            public void onResponse(Call<MoveListResponse> call, Response<MoveListResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                moveNames.clear();

                for (MoveListResponse.Result m : response.body().results) {
                    moveNames.add(m.name);
                }
            }

            @Override
            public void onFailure(Call<MoveListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}