package com.example.eonnav.pokedex.detail;

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
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailDescriptionFragment extends Fragment {

    private RequestQueue queue;

    private static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String FAVORITES_URL = "http://192.168.42.126:8000/api/favorites/";


    public DetailDescriptionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detaildescription, container, false);
        queue = Volley.newRequestQueue(requireContext());

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

        buttonFav.setClickable(true);
        buttonFav.setFocusable(true);
        buttonFav.bringToFront();

        if (getArguments() != null) {
            String name = getArguments().getString("name");
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

            boolean[] isFav = {false};   // usamos array para poder cambiarlo dentro del lambda
            int[] favId = {-1};          // guardar ID del favorito

            String urlGet = "http://192.168.42.126:8000/api/favorites/?user_id=1";

            StringRequest getRequest = new StringRequest(
                Request.Method.GET,
                urlGet,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            String favName = obj.getString("pokemon_name");

                            if (favName.equalsIgnoreCase(name)) {
                                isFav[0] = true;
                                favId[0] = obj.getInt("id");
                                buttonFav.setImageResource(R.drawable.filled_star);
                                return;
                            }
                        }

                        isFav[0] = false;
                        buttonFav.setImageResource(R.drawable.empty_star);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    android.util.Log.d("FAV_GET", "Error GET");
                }
            );

            queue.add(getRequest);

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


                StringRequest evoRequest = new StringRequest(Request.Method.GET,
                        evolutionChainUrl,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONObject chain = json.getJSONObject("chain");
                                EvoNode root = buildEvolutionTree(chain); // Construir arbol
                                drawEvolutionTree(root, evoChainLayout, queue);

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

                    for (String form : varieties) {
                        ImageView img = new ImageView(requireContext());


                        FlexboxLayout.LayoutParams params =
                                new FlexboxLayout.LayoutParams(dp(100), dp(100));
                        params.setMargins(16, 0, 16, 0);
                        img.setLayoutParams(params);
                        formsLayout.addView(img);

                        StringRequest request = new StringRequest(Request.Method.GET,
                                POKEMON_URL + form,
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

            buttonFav.setOnClickListener(v -> {

                if (isFav[0]) {

                    // DELETE FAVORITES
                    String urlDelete = "http://192.168.42.126:8000/api/favorites/?id=" + favId[0];

                    StringRequest deleteRequest = new StringRequest(
                        Request.Method.DELETE,
                        urlDelete,
                        response -> {
                            buttonFav.setImageResource(R.drawable.empty_star);
                            isFav[0] = false;
                            favId[0] = -1;

                            Toast.makeText(requireContext(),
                                "Eliminado de favoritos",
                                Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            android.util.Log.d("FAV_DELETE", "Error DELETE");
                        }
                    );

                    queue.add(deleteRequest);

                } else {

                    // POST FAVORITES
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("pokemon_name", name);
                        jsonBody.put("user_id", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest postRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        FAVORITES_URL,
                        jsonBody,
                        response -> {
                            buttonFav.setImageResource(R.drawable.filled_star);
                            isFav[0] = true;

                            try {
                                favId[0] = response.getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(requireContext(),
                                "Añadido a favoritos",
                                Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            android.util.Log.d("FAV_POST", "Error POST");
                        }
                    );

                    queue.add(postRequest);
                }
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

    // Recorre la cadena evolutiva en la API
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

            LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

            colParams.setMargins(24,0,24,0);
            column.setLayoutParams(colParams);

            for (EvoNode node : level) {
                ImageView img = new ImageView(requireContext());

                LinearLayout.LayoutParams p =
                        new LinearLayout.LayoutParams(dp(100), dp(100));
                p.setMargins(16, 16, 16, 16);
                img.setLayoutParams(p);

                column.addView(img);

                loadPokemonImage(node.name, img, queue);
            }

            container.addView(column);
        }
    }

    // CONSTRUCCION NIVELES EN EL ARBOL
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

    private void loadPokemonImage(String name, ImageView image, RequestQueue queue) {

        String correctedName = FORM_EXCEPTIONS.getOrDefault(name, name);

        StringRequest req = new StringRequest(Request.Method.GET,
            POKEMON_URL + correctedName,
            r -> {
                try {
                    JSONObject pj = new JSONObject(r);
                    String imgUrl = pj
                        .getJSONObject("sprites")
                        .getJSONObject("other")
                        .getJSONObject("official-artwork")
                        .optString("front_default", null);

                    // Si la imagen es null se reemplaza
                    if (imgUrl == null || imgUrl.equals("null") || imgUrl.isEmpty()) {

                        // Fallback general
                        image.setImageResource(R.drawable.icon_pokeball);
                        return;
                    }

                    Picasso.get().load(imgUrl).into(image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {}
        );

        queue.add(req);
    }

    // EXCEPCIONES
    private static final Map<String, String> FORM_EXCEPTIONS = new HashMap<>();

    static {
        FORM_EXCEPTIONS.put("deoxys", "deoxys-normal");
        FORM_EXCEPTIONS.put("wormadam", "wormadam-plant");
        FORM_EXCEPTIONS.put("giratina", "giratina-altered");
        FORM_EXCEPTIONS.put("shaymin", "shaymin-land");
        FORM_EXCEPTIONS.put("basculin", "basculin-white-striped");
        FORM_EXCEPTIONS.put("darmanitan", "darmanitan-standard");
        FORM_EXCEPTIONS.put("frillish", "frillish-male");
        FORM_EXCEPTIONS.put("jellicent", "jellicent-male");
        FORM_EXCEPTIONS.put("tornadus", "tornadus-incarnate");
        FORM_EXCEPTIONS.put("thundurus", "thundurus-incarnate");
        FORM_EXCEPTIONS.put("landorus", "landorus-incarnate");
        FORM_EXCEPTIONS.put("keldeo", "keldeo-ordinary");
        FORM_EXCEPTIONS.put("meloetta", "meloetta-aria");
        FORM_EXCEPTIONS.put("pyroar", "pyroar-male");
        FORM_EXCEPTIONS.put("meowstic", "meowstic-male");
        FORM_EXCEPTIONS.put("aegislash", "aegislash-shield");
        FORM_EXCEPTIONS.put("pumpkaboo", "pumpkaboo-average");
        FORM_EXCEPTIONS.put("gourgeist", "gourgeist-average");
        FORM_EXCEPTIONS.put("zygarde", "zygarde-50");
        FORM_EXCEPTIONS.put("oricorio", "oricorio-baile");
        FORM_EXCEPTIONS.put("lycanroc", "lycanroc-midday");
        FORM_EXCEPTIONS.put("wishiwashi", "wishiwashi-solo");
        FORM_EXCEPTIONS.put("minior", "minior-red-meteor");
        FORM_EXCEPTIONS.put("mimikyu", "mimikyu-disguised");
        FORM_EXCEPTIONS.put("toxtricity", "toxtricity-amped");
        FORM_EXCEPTIONS.put("eiscue", "eiscue-ice");
        FORM_EXCEPTIONS.put("indeedee", "indeedee-male");
        FORM_EXCEPTIONS.put("morpeko", "morpeko-full-belly");
        FORM_EXCEPTIONS.put("urshifu", "urshifu-single-strike");
        FORM_EXCEPTIONS.put("basculegion", "basculegion-male");
        FORM_EXCEPTIONS.put("enamorus", "enamorus-incarnate");
        FORM_EXCEPTIONS.put("oinkologne", "oinkologne-male");
        FORM_EXCEPTIONS.put("maushold", "maushold-family-of-four");
        FORM_EXCEPTIONS.put("squawkabilly", "squawkabilly-green-plumage");
        FORM_EXCEPTIONS.put("palafin", "palafin-zero");
        FORM_EXCEPTIONS.put("tatsugiri", "tatsugiri-curly");
        FORM_EXCEPTIONS.put("dudunsparce", "dudunsparce-two-segment");
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

}
