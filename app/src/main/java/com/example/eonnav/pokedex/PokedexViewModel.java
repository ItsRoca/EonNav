package com.example.eonnav.pokedex;

import androidx.lifecycle.ViewModel;

import com.example.eonnav.pokemon.Pokemon;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PokedexViewModel extends ViewModel {
    public String selectedType = null;
    public boolean onlyFavorites = false;
    public List<Pokemon> cachedPokemonList = null;

}
