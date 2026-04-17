package com.example.eonnav;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String name = getIntent().getStringExtra("name");

        ImageView image = findViewById(R.id.imagePokemon);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeapi.co/api/v2/pokemon/" + name;

        TextView text = findViewById(R.id.textDetail);
        text.setText(name);

        ImageView buttonFav = findViewById(R.id.buttonFav);
        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        boolean isFav = prefs.getBoolean(name, false);
        if (isFav) {
            buttonFav.setImageResource(R.drawable.filled_star);
        } else {
            buttonFav.setImageResource(R.drawable.empty_star);
        }


        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        String nombre = json.getString("name");
                        nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
                        int height = json.getInt("height");
                        double heightMeters = height / 10.0;

                        text.setText(nombre + "\nAltura: " + heightMeters + " m");

                        JSONObject sprites = json.getJSONObject("sprites");
                        String imageUrl = sprites.getString("front_default");

                        Picasso.get().load(imageUrl).into(image);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    text.setText("Error");
                });

        queue.add(request);

        buttonFav.setOnClickListener(v -> {

            SharedPreferences.Editor editor = prefs.edit();
            boolean favorito = prefs.getBoolean(name, false);

            String nombre = name;
            nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);

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


    }
}