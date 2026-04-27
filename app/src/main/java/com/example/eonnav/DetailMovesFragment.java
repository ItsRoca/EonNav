package com.example.eonnav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailMovesFragment extends Fragment {

    public DetailMovesFragment() {}

    public static DetailMovesFragment newInstance(String moves) {
        DetailMovesFragment fragment = new DetailMovesFragment();
        Bundle args = new Bundle();
        args.putString("moves", moves);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detaildescription, container, false);

        TextView textMoves = view.findViewById(R.id.textDescription);

        if (getArguments() != null) {
            textMoves.setText(getArguments().getString("moves"));
        }

        return view;
    }
}