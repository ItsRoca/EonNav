package com.example.eonnav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailBattleInfoFragment extends Fragment {

    public DetailBattleInfoFragment() {}

    public static DetailBattleInfoFragment newInstance(String description) {
        DetailBattleInfoFragment fragment = new DetailBattleInfoFragment();
        Bundle args = new Bundle();
        args.putString("desc", description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detaildescription, container, false);

        TextView textDesc = view.findViewById(R.id.textDescription);

        if (getArguments() != null) {
            textDesc.setText(getArguments().getString("desc"));
        }

        return view;
    }
}