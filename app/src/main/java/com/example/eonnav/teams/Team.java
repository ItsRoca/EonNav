package com.example.eonnav.teams;

import java.util.List;

public class Team {

    private int id;
    private String name;
    public List<PokemonData> pokemons;

    public Team(int id, String name, List<PokemonData> pokemons) {
        this.id = id;
        this.name = name;
        this.pokemons = pokemons;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PokemonData> getPokemons() {
        return pokemons;
    }
}