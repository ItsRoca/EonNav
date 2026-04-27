package com.example.eonnav;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailFamilyFragment extends Fragment {

    public DetailFamilyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailfamily, container, false);

        LinearLayout evoChainContainer = view.findViewById(R.id.evoChainContainer);
        LinearLayout formsContainer    = view.findViewById(R.id.formsContainer);
        TextView textFormsTitle        = view.findViewById(R.id.textFormsTitle);

        if (getArguments() == null) return view;

        String evolutionChainUrl = getArguments().getString("evolutionChainUrl");
        String varietiesStr      = getArguments().getString("varieties");

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // ── Cadena evolutiva ──────────────────────────────────────────
        StringRequest evoRequest = new StringRequest(Request.Method.GET, evolutionChainUrl,
                response -> {
                    try {
                        JSONObject json  = new JSONObject(response);
                        JSONObject chain = json.getJSONObject("chain");

                        // Recorre la cadena y recoge los nombres en orden
                        List<String> evoNames = new ArrayList<>();
                        collectEvolutions(chain, evoNames);

                        // Por cada nombre pide la imagen y añade una vista
                        for (int i = 0; i < evoNames.size(); i++) {
                            String evoName = evoNames.get(i);

                            // Flecha entre eslabones (excepto el primero)
                            if (i > 0) {
                                TextView arrow = new TextView(requireContext());
                                arrow.setText("→");
                                arrow.setTextSize(24);
                                arrow.setGravity(Gravity.CENTER_VERTICAL);
                                LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                arrowParams.setMargins(8, 0, 8, 0);
                                arrow.setLayoutParams(arrowParams);
                                evoChainContainer.addView(arrow);
                            }

                            // Contenedor vertical: imagen + nombre
                            LinearLayout itemLayout = new LinearLayout(requireContext());
                            itemLayout.setOrientation(LinearLayout.VERTICAL);
                            itemLayout.setGravity(Gravity.CENTER);

                            ImageView img = new ImageView(requireContext());
                            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(120, 120);
                            img.setLayoutParams(imgParams);

                            TextView nameText = new TextView(requireContext());
                            nameText.setText(evoName.substring(0, 1).toUpperCase() + evoName.substring(1));
                            nameText.setGravity(Gravity.CENTER);
                            nameText.setTextSize(12);

                            itemLayout.addView(img);
                            itemLayout.addView(nameText);
                            evoChainContainer.addView(itemLayout);

                            // Pide la imagen a /pokemon/{name}
                            StringRequest imgRequest = new StringRequest(Request.Method.GET,
                                    "https://pokeapi.co/api/v2/pokemon/" + evoName,
                                    imgResponse -> {
                                        try {
                                            JSONObject pokemonJson = new JSONObject(imgResponse);
                                            String imageUrl = pokemonJson
                                                    .getJSONObject("sprites")
                                                    .getJSONObject("other")
                                                    .getJSONObject("official-artwork")
                                                    .getString("front_default");
                                            Picasso.get().load(imageUrl).into(img);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> {}
                            );
                            queue.add(imgRequest);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );
        queue.add(evoRequest);

        // ── Formas alternativas ───────────────────────────────────────
        if (varietiesStr != null) {
            String[] varieties = varietiesStr.split(",");

            // Si solo hay una forma (la normal) ocultamos el título
            if (varieties.length <= 1) {
                textFormsTitle.setVisibility(View.GONE);
            } else {
                for (String formName : varieties) {
                    LinearLayout itemLayout = new LinearLayout(requireContext());
                    itemLayout.setOrientation(LinearLayout.VERTICAL);
                    itemLayout.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    itemParams.setMargins(12, 0, 12, 0);
                    itemLayout.setLayoutParams(itemParams);

                    ImageView img = new ImageView(requireContext());
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(120, 120);
                    img.setLayoutParams(imgParams);

                    TextView nameText = new TextView(requireContext());
                    nameText.setText(formName.substring(0, 1).toUpperCase() + formName.substring(1));
                    nameText.setGravity(Gravity.CENTER);
                    nameText.setTextSize(12);

                    itemLayout.addView(img);
                    itemLayout.addView(nameText);
                    formsContainer.addView(itemLayout);

                    // Imagen de cada forma
                    StringRequest formRequest = new StringRequest(Request.Method.GET,
                            "https://pokeapi.co/api/v2/pokemon/" + formName,
                            formResponse -> {
                                try {
                                    JSONObject pokemonJson = new JSONObject(formResponse);
                                    String imageUrl = pokemonJson
                                            .getJSONObject("sprites")
                                            .getJSONObject("other")
                                            .getJSONObject("official-artwork")
                                            .getString("front_default");
                                    Picasso.get().load(imageUrl).into(img);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {}
                    );
                    queue.add(formRequest);
                }
            }
        }

        return view;
    }

    // Recorre recursivamente la cadena evolutiva de la API
    private void collectEvolutions(JSONObject chain, List <String> names) {
        try {
            names.add(chain.getJSONObject("species").getString("name"));
            org.json.JSONArray evolvesTo = chain.getJSONArray("evolves_to");
            if (evolvesTo.length() > 0) {
                collectEvolutions(evolvesTo.getJSONObject(0), names);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}