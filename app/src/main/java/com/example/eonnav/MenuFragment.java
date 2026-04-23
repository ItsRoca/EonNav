
package com.example.eonnav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MenuFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        // Visualizamos el layout del fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);


        // Botones de juegos
        Button btn1 = view.findViewById(R.id.btn1);
        Button btn2 = view.findViewById(R.id.btn2);
        Button btn3 = view.findViewById(R.id.btn3);
        Button btn4 = view.findViewById(R.id.btn4);
        Button btn5 = view.findViewById(R.id.btn5);
        Button btn6 = view.findViewById(R.id.btn6);
        Button btn7 = view.findViewById(R.id.btn7);
        Button btn8 = view.findViewById(R.id.btn8);

        btn1.setOnClickListener(v -> abrirWeb("https://victoryroad.pro/"));
        btn2.setOnClickListener(v -> abrirWeb("https://play.pokemonshowdown.com/"));
        btn3.setOnClickListener(v -> abrirWeb("https://nerd-of-now.github.io/NCP-VGC-Damage-Calculator/"));
        btn4.setOnClickListener(v -> abrirWeb("https://munchstats.com/"));
        btn5.setOnClickListener(v -> abrirWeb("https://pokedle.net/"));
        btn6.setOnClickListener(v -> abrirWeb("https://pokedoku.com/"));
        btn7.setOnClickListener(v -> abrirWeb("https://pkmnquiz.com/"));
        btn8.setOnClickListener(v -> abrirWeb("https://cajunavenger.github.io/"));

        return view;
    }

    private void abrirWeb(String url) {
        Intent intent = new Intent(requireContext(), WebActivity.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}