package com.example.eonnav.pokedex.detail;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.AlignItems;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eonnav.R;
import com.example.eonnav.pokemon.WeaknessResistancesCalculator;
import com.example.eonnav.pokemon.WeaknessResistancesResult;
import com.example.eonnav.utils.TypeUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DetailBattleInfoFragment extends Fragment {

    private TextView textName;
    private TextView textNumber;
    private LinearLayout layoutStats;
    private LinearLayout layoutWeakness;
    private LinearLayout layoutMoves;
    private RequestQueue queue;

    public DetailBattleInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(
            R.layout.fragment_detailbattleinfo,
            container,
            false
        );

        textName = view.findViewById(R.id.textName);
        textNumber = view.findViewById(R.id.textNumber);

        Bundle data = getArguments();

        queue = Volley.newRequestQueue(requireContext());

        layoutStats = view.findViewById(R.id.layoutStats); // Visualizar stats
        layoutWeakness = view.findViewById(R.id.layoutWeakness); //Visualizar debilidades y resistencias
        layoutMoves = view.findViewById(R.id.layoutMoves); //Visualizar movimientos

        if (data != null) {

            String name = data.getString("name");
            String number = data.getString("number");

            textName.setText(name);
            textNumber.setText(number);

            loadPokemonStats(name);
        }

        return view;

    }

    // CARGAR STATS
    private void loadPokemonStats(String name) {

        String url = "https://pokeapi.co/api/v2/pokemon/" + name;

        StringRequest request = new StringRequest(
            Request.Method.GET,
            url,
            response -> {

                if (!isAdded()) return;

                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray statsArray = json.getJSONArray("stats");

                    JSONArray movesArray = json.getJSONArray("moves");
                    loadMoves(movesArray);

                    JSONArray typesArray = json.getJSONArray("types");
                    loadWeaknesses(typesArray);


                    layoutStats.removeAllViews();

                    int totalStats = 0; // Suma de todas las stats

                    for (int i = 0; i < statsArray.length(); i++) {
                        JSONObject statObject = statsArray.getJSONObject(i);

                        int value = statObject.getInt("base_stat");
                        String statName = statObject
                            .getJSONObject("stat")
                            .getString("name");

                        totalStats += value;

                        addStatRow(statName, value); // Añade una fila para cada stat
                    }

                    addTotalStatsRow(totalStats); // Muestra la suma total de las stats

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                error.printStackTrace();
            }
        );

        queue.add(request);
    }

    // NOMBRES STATS
    private String formatStatName(String stat) {
        switch (stat) {
            case "hp": return "PS";
            case "attack": return "Ataque";
            case "defense": return "Defensa";
            case "special-attack": return "At. Especial";
            case "special-defense": return "Def. Especial";
            case "speed": return "Velocidad";
            default: return stat;
        }
    }

    // MOSTRAR STATS
    private void addStatRow(String statName, int value) {

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(8, 8, 8, 8);

        LinearLayout topRow = new LinearLayout(getContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView nameView = new TextView(getContext());
        nameView.setText(formatStatName(statName));
        nameView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView valueView = new TextView(getContext());
        valueView.setText(String.valueOf(value));
        valueView.setGravity(Gravity.END);

        topRow.addView(nameView);
        topRow.addView(valueView);

        LinearLayout barBackground = new LinearLayout(getContext());
        barBackground.setLayoutParams(
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                64 //Alto de las barras
            )
        );
        barBackground.setBackgroundColor(0xFFDDDDDD);


        View barFill = new View(getContext());

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        int maxWidth = screenWidth; // ancho maximo de la barra dejando margen

        int barWidth = (int) ((value / 255f) * maxWidth);

        barBackground.setPadding(4, 4, 4, 4);

        LinearLayout.LayoutParams fillParams =
            new LinearLayout.LayoutParams(
                barWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

        barFill.setLayoutParams(fillParams);

        barFill.setBackgroundColor(getStatColor(value)); // Rellenado con color segun el valor de la stat

        barBackground.addView(barFill);

        row.addView(topRow);
        row.addView(barBackground);

        layoutStats.addView(row);
    }

    // CONVERTIR STAT EN COLOR
    private int getStatColor(int value) {

        float ratio = value / 255f;

        if (ratio < 0.2f) {
            // De rojo a naranja
            float t = ratio / 0.2f;
            return interpolateColor(0xFFFF0000, 0xFFFF7F00, t);
        }
        else if (ratio < 0.4f) {
            // De naranja a amarillo
            float t = (ratio - 0.2f) / 0.2f;
            return interpolateColor(0xFFFF7F00, 0xFFFFFF00, t);
        }
        else if (ratio < 0.6f) {
            // De amarillo a verde
            float t = (ratio - 0.4f) / 0.2f;
            return interpolateColor(0xFFFFFF00, 0xFF00FF00, t);
        }
        else if (ratio < 0.8f) {
            // De verde a cian
            float t = (ratio - 0.6f) / 0.2f;
            return interpolateColor(0xFF00FF00, 0xFF00FFFF, t);
        }
        else {
            // De cian a azul
            float t = (ratio - 0.8f) / 0.2f;
            return interpolateColor(0xFF00FFFF, 0xFF0000FF, t);
        }
    }

    // MEZCLAR 2 COLORES PARA OBTENER EL INTERMEDIO
    private int interpolateColor(int colorStart, int colorEnd, float t) {

        // Separamos el color inicial en RGB
        int r1 = (colorStart >> 16) & 0xFF;
        int g1 = (colorStart >> 8) & 0xFF;
        int b1 = colorStart & 0xFF;

        // Separamos el color final en RGB
        int r2 = (colorEnd >> 16) & 0xFF;
        int g2 = (colorEnd >> 8) & 0xFF;
        int b2 = colorEnd & 0xFF;

        // Mezclamos los colores
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        // Reconstruimos el color con el resultado
        return android.graphics.Color.rgb(r, g, b);
    }

    // MOSTRAR TOTAL STATS
    private void addTotalStatsRow(int total) {

        TextView totalView = new TextView(getContext());

        totalView.setText("Total: " + total);
        totalView.setTextSize(18);
        totalView.setPadding(8, 16, 8, 8);
        totalView.setTypeface(null, android.graphics.Typeface.BOLD);
        totalView.setGravity(Gravity.END);

        layoutStats.addView(totalView);
    }

    // CARGAR INFORMACION DE LOS TIPOS
    private void loadWeaknesses(JSONArray typesArray) {

        if (!isAdded()) return;

        try {
            // Crear lista con los datos de cada tipo
            List<JSONObject> typeDataList = new ArrayList<>();

            for (int i = 0; i < typesArray.length(); i++) {

                String typeName = typesArray
                        .getJSONObject(i)
                        .getJSONObject("type")
                        .getString("name");

                String typeUrl = "https://pokeapi.co/api/v2/type/" + typeName;

                StringRequest typeRequest = new StringRequest(
                        Request.Method.GET,
                        typeUrl,
                        typeResponse -> {
                            try {
                                JSONObject typeJson = new JSONObject(typeResponse);

                                typeDataList.add(typeJson);

                                // Cualcular
                                if (typeDataList.size() == typesArray.length()) {

                                    Map<String, Double> map =
                                            WeaknessResistancesCalculator.calculate(typeDataList);

                                    WeaknessResistancesResult result =
                                            WeaknessResistancesCalculator.classify(map);

                                    renderWeaknesses(result);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {}
                );

                queue.add(typeRequest);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //MOSTRAR DEBILIDADES
    private void renderWeaknesses(WeaknessResistancesResult result) {

        layoutWeakness.removeAllViews();

        addTypeRow("x4", result.x4);
        addDivider();
        addTypeRow("x2", result.x2);
        addDivider();
        addTypeRow("Normal", result.x1);
        addDivider();
        addTypeRow("½", result.x05);
        addDivider();
        addTypeRow("¼", result.x025);
        addDivider();
        addTypeRow("0", result.x0);
    }

    // MOSTRAR ICONO DEBILIDADES
    private int getMultiplierIcon(String label) {
        switch (label) {
            case "x4": return R.drawable.x4;
            case "x2": return R.drawable.x2;
            case "Normal": return R.drawable.icon_pokeball;
            case "½": return R.drawable.x05;
            case "¼": return R.drawable.x025;
            case "0": return R.drawable.x0;
            default: return R.drawable.icon_pokeball;
        }
    }

    // AÑADIR LINEA DIVISORIA
    private void addDivider() {
        View divider = new View(getContext());

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                4 // Grosor de la linea
            );

        params.setMargins(0, 16, 0, 16);

        divider.setLayoutParams(params);
        divider.setBackgroundColor(0xFFCCCCCC); // Color de la linea

        layoutWeakness.addView(divider);
    }

    // CREAR FILAS CON TIPOS POR DEBILIDADES
    private void addTypeRow(String label, List<String> types) {

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(12, 12, 12, 12);

        row.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Añadir icono multiplicador
        ImageView multiplierIcon = new ImageView(getContext());
        multiplierIcon.setImageResource(getMultiplierIcon(label));
        multiplierIcon.setAdjustViewBounds(true);
        multiplierIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(220, 110); // tamaño icono multiplicador
        iconParams.setMargins(0, 0, 24, 0);

        multiplierIcon.setLayoutParams(iconParams);

        row.addView(multiplierIcon);


        // Crear contenedor de tipos
        FlexboxLayout iconsLayout = new FlexboxLayout(getContext());

        iconsLayout.setFlexWrap(FlexWrap.WRAP);
        iconsLayout.setAlignItems(AlignItems.CENTER);

        LinearLayout.LayoutParams iconsParams =
            new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
            );

        iconsLayout.setLayoutParams(iconsParams);

        if (types.isEmpty()) {
            TextView none = new TextView(getContext());
            none.setText("Ninguno");
            iconsLayout.addView(none);
        } else {
            // Mostrar iconos de los tipos
            for (String type : types) {
                ImageView icon = new ImageView(getContext());

                icon.setImageResource(TypeUtils.getTypeIcon(type));
                icon.setAdjustViewBounds(true);
                icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

                FlexboxLayout.LayoutParams params =
                        new FlexboxLayout.LayoutParams(dp(84), dp(32));
                params.setMargins(8, 8, 8, 8);

                icon.setLayoutParams(params);
                iconsLayout.addView(icon);
            }
        }

        row.addView(iconsLayout);
        layoutWeakness.addView(row);
    }

    // CARGAR MOVIMIENTOS
    private void loadMoves(JSONArray movesArray) throws JSONException {

        layoutMoves.removeAllViews();

        // Comprobar duplicados
        Set<String> uniqueMoves = new HashSet<>();

        for (int i = 0; i < movesArray.length(); i++) {

            String moveName = movesArray
                .getJSONObject(i)
                .getJSONObject("move")
                .getString("name");

            // Si no existe se añade
            if (!uniqueMoves.contains(moveName)) {
                uniqueMoves.add(moveName);
                addMoveRow(moveName);
            }
        }
    }

    // AÑADIR FILA POR MOVIMIENTO
    private void addMoveRow(String moveName) {

        String url = "https://pokeapi.co/api/v2/move/" + moveName;

        StringRequest request = new StringRequest(
            Request.Method.GET,
            url,
            response -> {

                if (!isAdded()) return;

                try {
                    JSONObject json = new JSONObject(response);

                    // Obtener tipo del movimiento
                    String type = json
                        .getJSONObject("type")
                        .getString("name");

                    // Obtener categoria del movimiento
                    String category = json
                        .getJSONObject("damage_class")
                        .getString("name");

                    createMoveRow(moveName, type, category); // Crear la fila

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {}
        );

        queue.add(request);
    }

    // FORMATEAR NOMBRE
    private String formatMoveName(String name) {

        name = name.replace("-", " "); // Eliminar guiones

        if (name.length() == 0) return name; // Evitar error en caso de estar vacio

        return name.substring(0, 1).toUpperCase() + name.substring(1); // Primera letra mayuscula
    }

    // CREAR LAS FILAS
    private void createMoveRow(String moveName, String type, String category) {

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);

        // Icono del tipo
        ImageView typeIcon = new ImageView(getContext());
        typeIcon.setImageResource(TypeUtils.getTypeIcon(type));

        LinearLayout.LayoutParams typeParams =
            new LinearLayout.LayoutParams(dp(84), dp(24));
        typeParams.setMargins(0, 0, dp(8), 0);

        typeIcon.setLayoutParams(typeParams);

        row.addView(typeIcon);

        // Icono de la categoria
        ImageView categoryIcon = new ImageView(getContext());
        categoryIcon.setImageResource(getCategoryIcon(category));

        LinearLayout.LayoutParams catParams =
            new LinearLayout.LayoutParams(dp(40), dp(24));
        catParams.setMargins(0, 0, dp(8), 0);

        categoryIcon.setLayoutParams(catParams);

        row.addView(categoryIcon);

        // Nombre del movimiento
        TextView moveText = new TextView(getContext());
        moveText.setText(formatMoveName(moveName));
        moveText.setTextSize(16);

        row.addView(moveText);

        layoutMoves.addView(row);
    }

    // ASIGNAR ICONOS A LAS CATEGORIAS
    private int getCategoryIcon(String category) {
        switch (category) {
            case "physical": return R.drawable.fisico;
            case "special": return R.drawable.especial;
            case "status": return R.drawable.estado;
            default: return R.drawable.estado;
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }


}