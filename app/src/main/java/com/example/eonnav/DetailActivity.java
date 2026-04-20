package com.example.eonnav;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String name = getIntent().getStringExtra("name");
        String nombre = name.substring(0, 1).toUpperCase() + name.substring(1);


        RequestQueue queue = Volley.newRequestQueue(this);

        ImageView image = findViewById(R.id.imagePokemon);
        TextView textName = findViewById(R.id.textName);
        TextView textInfo = findViewById(R.id.textInfo);
        TextView textNumber   = findViewById(R.id.textNumber);
        TextView textTypes    = findViewById(R.id.textTypes);
        TextView textGen      = findViewById(R.id.textGeneration);
        TextView textDesc     = findViewById(R.id.textDescription);
        TextView textMoves    = findViewById(R.id.textMoves);


        textName.setText(nombre);

        // Favorito
        ImageView buttonFav = findViewById(R.id.buttonFav);

        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        boolean isFav = prefs.getBoolean(name, false);
        if (isFav) {
            buttonFav.setImageResource(R.drawable.filled_star);
        } else {
            buttonFav.setImageResource(R.drawable.empty_star);
        }

        buttonFav.setOnClickListener(v -> {

            SharedPreferences.Editor editor = prefs.edit();
            boolean favorito = prefs.getBoolean(name, false);

            if (favorito) {
                editor.remove(name);
                buttonFav.setImageResource(R.drawable.empty_star);
                Toast.makeText(this, nombre + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
            } else {
                editor.putBoolean(name, true);
                buttonFav.setImageResource(R.drawable.filled_star);
                Toast.makeText(this, nombre + " añadido a favoritos", Toast.LENGTH_SHORT).show();
            }

            editor.apply();
        });

        // Peticion de datos a /pokemon/
        String url = "https://pokeapi.co/api/v2/pokemon/" + name;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        //Nº Pokedex
                        int id = json.getInt("id");
                        textNumber.setText(String.format("#%03d", id));

                        // Info
                        double height = json.getInt("height") / 10.0;
                        double weight = json.getInt("weight") / 10.0;
                        textInfo.setText("Altura: " + height + " m  |  Peso: " + weight + " kg");

                        // Tipos
                        JSONArray types = json.getJSONArray("types");
                        StringBuilder tiposStr = new StringBuilder("Tipo: ");
                        for (int i = 0; i < types.length(); i++) {
                            if (i > 0) tiposStr.append(" / ");
                            String tipo = types.getJSONObject(i)
                                    .getJSONObject("type")
                                    .getString("name");
                            tiposStr.append(tipo.substring(0, 1).toUpperCase()).append(tipo.substring(1));
                        }
                        textTypes.setText(tiposStr.toString());

                        // Movimientos (máximo 20 para no saturar)
                        JSONArray moves = json.getJSONArray("moves");
                        StringBuilder movesStr = new StringBuilder();

                        int limit = Math.min(moves.length(), 20);
                        for (int i = 0; i < limit; i++) {
                            String move = moves.getJSONObject(i)
                                    .getJSONObject("move")
                                    .getString("name");
                            move = move.substring(0, 1).toUpperCase() + move.substring(1);
                            movesStr.append("• ").append(move).append("\n");
                        }
                        textMoves.setText(movesStr.toString());

                        //Imagen
                        JSONObject sprites = json.getJSONObject("sprites");
                        String imageUrl = sprites
                                .getJSONObject("other")
                                .getJSONObject("official-artwork")
                                .getString("front_default");

                        Picasso.get().load(imageUrl).into(image);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    textInfo.setText("Error");
                });

        // Peticion de datos a /pokemon-species/
        String urlSpecies = "https://pokeapi.co/api/v2/pokemon-species/" + name;
        StringRequest speciesRequest = new StringRequest(Request.Method.GET, urlSpecies,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        // Generacion
                        String gen = json.getJSONObject("generation").getString("name");
                        // "generation-i" → "Generación I"
                        String genFormatted = gen.replace("generation-", "Generación ").toUpperCase()
                                .replace("GENERACIÓN ", "Generación ");
                        textGen.setText(genFormatted);

                        // Descripcion en espanhol
                        JSONArray entries = json.getJSONArray("flavor_text_entries");
                        String descripcion = "";
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            String lang = entry.getJSONObject("language").getString("name");
                            if (lang.equals("es")) {
                                descripcion = entry.getString("flavor_text")
                                        .replace("\n", " ")
                                        .replace("\f", " ");
                                break;
                            }
                        }
                        textDesc.setText(descripcion);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> textGen.setText(""));

        queue.add(request);
        queue.add(speciesRequest);

    }
}