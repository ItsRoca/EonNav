    package com.example.eonnav.pokedex.detail;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.graphics.drawable.Drawable;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.eonnav.R;
    import com.example.eonnav.utils.TypeUtils;
    import com.google.android.flexbox.FlexboxLayout;
    import com.squareup.picasso.Picasso;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.List;

    public class DetailDescriptionFragment extends Fragment {

        public DetailDescriptionFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_detaildescription, container, false);

            TextView textName   = view.findViewById(R.id.textName);
            TextView textNumber = view.findViewById(R.id.textNumber);
            TextView textDesc   = view.findViewById(R.id.textDescription);
            ImageView image     = view.findViewById(R.id.imagePokemon);
            ImageView buttonFav = view.findViewById(R.id.buttonFav);
            LinearLayout layoutTypes = view.findViewById(R.id.layoutTypes);
            TextView textHeight     = view.findViewById(R.id.textHeight);
            TextView textWeight     = view.findViewById(R.id.textWeight);
            TextView textEggGroups  = view.findViewById(R.id.textEggGroups);
            LinearLayout layoutAbilities = view.findViewById(R.id.layoutAbilities);
            LinearLayout evoChainLayout = view.findViewById(R.id.evoChainContainer);
            FlexboxLayout formsLayout = view.findViewById(R.id.formsContainer);
            TextView textEvoTitle = view.findViewById(R.id.textEvoTitle);
            TextView textFormsTitle = view.findViewById(R.id.textFormsTitle);



            if (getArguments() != null) {
                String name    = getArguments().getString("name");
                String nameKey = name.toLowerCase();
                ArrayList <String> types = getArguments().getStringArrayList("types");
                String height = getArguments().getString("height");
                String weight = getArguments().getString("weight");
                String eggGroups = getArguments().getString("eggGroups");
                ArrayList<String> abilities = getArguments().getStringArrayList("abilities");

                String evolutionChainUrl = getArguments().getString("evolutionChainUrl");
                String varietiesStr = getArguments().getString("varieties");

                textName.setText(getArguments().getString("name"));
                textNumber.setText(getArguments().getString("number"));
                textDesc.setText(getArguments().getString("description"));
                Picasso.get().load(getArguments().getString("imageUrl")).into(image);

                // Mostrar tipo/s
                if (types != null) {
                    for (String type : types) {
                        ImageView icon = new ImageView(requireContext());

                        Drawable drawable = ContextCompat.getDrawable(
                                requireContext(),
                                TypeUtils.getTypeIcon(type)
                        );

                        if (drawable != null) {
                            float density = getResources().getDisplayMetrics().density;
                            int h = (int) (24 * density);

                            int w = drawable.getIntrinsicWidth();
                            int ih = drawable.getIntrinsicHeight();
                            int width = (int) (h * ((float) w / ih));

                            drawable.setBounds(0, 0, width, h);
                            icon.setImageDrawable(drawable);
                        }

                        layoutTypes.addView(icon);
                    }
                }

                // Mostrar Habilidades
                if (abilities != null) {
                    for (String ability : abilities) {
                        TextView tv = new TextView(requireContext());
                        tv.setText("• " + ability);
                        tv.setTextSize(14);
                        layoutAbilities.addView(tv);
                    }
                }

                // Mostrar altura, peso y grupo/s huevo
                textHeight.setText("Altura: " + height);
                textWeight.setText("Peso: " + weight);
                textEggGroups.setText("Grupo huevo: " + eggGroups);

                if (evolutionChainUrl != null) {
                    RequestQueue queue = Volley.newRequestQueue(requireContext());

                    StringRequest evoRequest = new StringRequest(Request.Method.GET,
                            evolutionChainUrl,
                            response -> {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    JSONObject chain = json.getJSONObject("chain");
                                    EvoNode root = buildEvolutionTree(chain); // construir árbol
                                    drawEvolutionTree(root, evoChainLayout, queue); // dibujar

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {}
                    );
                    queue.add(evoRequest);
                }

                // Mostrar Formas Alternativas
                if (varietiesStr != null) {
                    String[] varieties = varietiesStr.split(",");

                    if (varieties.length <= 1) {
                        textFormsTitle.setVisibility(View.GONE);
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(requireContext());

                        for (String form : varieties) {
                            ImageView img = new ImageView(requireContext());


                            FlexboxLayout.LayoutParams params =
                                    new FlexboxLayout.LayoutParams(320, 320);
                            params.setMargins(8, 0, 8, 0);
                            img.setLayoutParams(params);
                            formsLayout.addView(img);

                            StringRequest request = new StringRequest(Request.Method.GET,
                                    "https://pokeapi.co/api/v2/pokemon/" + form,
                                    response -> {
                                        try {
                                            JSONObject pokemonData = new JSONObject(response);
                                            String imgUrl = pokemonData
                                                    .getJSONObject("sprites")
                                                    .getJSONObject("other")
                                                    .getJSONObject("official-artwork")
                                                    .getString("front_default");
                                            Picasso.get().load(imgUrl).into(img);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    err -> {}
                            );
                            queue.add(request);
                        }
                    }
                }

                // Favorito
                SharedPreferences prefs = requireActivity()
                        .getSharedPreferences("favorites", Context.MODE_PRIVATE);

                buttonFav.setImageResource(prefs.getBoolean(nameKey, false)
                        ? R.drawable.filled_star
                        : R.drawable.empty_star);

                buttonFav.setOnClickListener(v -> {
                    boolean isFav = prefs.getBoolean(nameKey, false);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (isFav) {
                        editor.remove(nameKey);
                        buttonFav.setImageResource(R.drawable.empty_star);
                        Toast.makeText(requireContext(), name + " eliminado de favoritos",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putBoolean(nameKey, true);
                        buttonFav.setImageResource(R.drawable.filled_star);
                        Toast.makeText(requireContext(), name + " añadido a favoritos",
                                Toast.LENGTH_SHORT).show();
                    }
                    editor.apply();
                });
            }




            return view;
        }

        class EvoNode {
            String name;
            List<EvoNode> children = new ArrayList<>();

            EvoNode(String name) {
                this.name = name;
            }

        }
        // Recorre recursivamente la cadena evolutiva de la API
        private EvoNode buildEvolutionTree(JSONObject chain) {
            try {
                String name = chain.getJSONObject("species").getString("name");
                EvoNode node = new EvoNode (name);

               JSONArray evolvesTo = chain.getJSONArray("evolves_to");

                for (int i = 0; i < evolvesTo.length(); i++) {
                    node.children.add(buildEvolutionTree(evolvesTo.getJSONObject(i)));
                }

                return node;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void drawEvolutionTree(EvoNode root, LinearLayout container, RequestQueue queue) {
            container.removeAllViews();

            container.setOrientation(LinearLayout.HORIZONTAL);

            List<List<EvoNode>> levels = new ArrayList<>();
            buildLevels(root, 0, levels);

            for (List<EvoNode> level : levels) {
                LinearLayout column = new LinearLayout(requireContext());
                column.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams colParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                colParams.setMargins(24,0,24,0);
                column.setLayoutParams(colParams);

                for (EvoNode node : level) {
                    ImageView img = new ImageView(requireContext());

                    LinearLayout.LayoutParams p =
                            new LinearLayout.LayoutParams(220, 220);
                    p.setMargins(16, 16, 16, 16);
                    img.setLayoutParams(p);

                    column.addView(img);

                    loadPokemonImage(node.name, img, queue);
                }

                container.addView(column);
            }
        }

        private void buildLevels(EvoNode node, int depth, List<List<EvoNode>> levels) {
            if (node == null) return;

            if (levels.size() <= depth) {
                levels.add(new ArrayList<>());
            }

            levels.get(depth).add(node);

            for (EvoNode child : node.children) {
                buildLevels(child, depth + 1, levels);
            }
        }

        private void loadPokemonImage(String name, ImageView img, RequestQueue queue) {

            // EXCEPCIONES
            if (name.equals ("wormadam")) {
                name ="wormadam-plant";
            }

            StringRequest req = new StringRequest(Request.Method.GET,
                    "https://pokeapi.co/api/v2/pokemon/" + name,
                    r -> {
                        try {
                            JSONObject pj = new JSONObject(r);
                            String imgUrl = pj
                                    .getJSONObject("sprites")
                                    .getJSONObject("other")
                                    .getJSONObject("official-artwork")
                                    .optString("front_default", null);


                            // ✅ SI LA IMAGEN ES NULL → fallback
                            if (imgUrl == null || imgUrl.equals("null") || imgUrl.isEmpty()) {



                                // fallback general
                                img.setImageResource(R.drawable.icon_pokeball);
                                return;
                            }


                            Picasso.get().load(imgUrl).into(img);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {}
            );

            queue.add(req);
        }


    }
