package com.example.eonnav;

import com.example.eonnav.teams.Team;
import com.example.eonnav.teams.teambuilder.AbilityListResponse;
import com.example.eonnav.teams.teambuilder.MoveListResponse;
import com.example.eonnav.teams.teambuilder.PokemonListResponse;
import com.example.eonnav.pokemon.PokemonResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {

    // POKEAPI //
    @GET("pokemon/{name}")
    Call<PokemonResponse> getPokemon(@Path("name") String name);    // Nombres

    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(@Query("limit") int limit);    // Datos

    @GET("ability")
    Call<AbilityListResponse> getAbilityList(@Query("limit") int limit);    // Habilidades

    @GET("move")
    Call<MoveListResponse> getMoveList(@Query("limit") int limit);  // Movimientos


    // BACKEND (equipos) //
    @POST("teams/save/")
    Call<Void> saveTeam(@Body Team team);   // Guardar nuevo equipo

    @GET("teams/")
    Call<List<Team>> getTeams(@Query("name") String name);    // Lista equipos

    @GET("teams/{id}/")
    Call<Team> getTeam(@Path("id") int id); // Detalles equipo

    @PUT("teams/{id}/")
    Call<Void> updateTeam(@Path("id") int id, @Body Team team); // Actualizar equipo

    @DELETE("teams/{id}/")
    Call<Void> deleteTeam(@Path("id") int id);  // Eliminar equipo
}
