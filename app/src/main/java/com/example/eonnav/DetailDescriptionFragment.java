    package com.example.eonnav;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.fragment.app.Fragment;

    import com.squareup.picasso.Picasso;

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

            if (getArguments() != null) {
                String name    = getArguments().getString("name");
                String nameKey = name.toLowerCase();

                textName.setText(getArguments().getString("name"));
                textNumber.setText(getArguments().getString("number"));
                textDesc.setText(getArguments().getString("description"));
                Picasso.get().load(getArguments().getString("imageUrl")).into(image);

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
    }
