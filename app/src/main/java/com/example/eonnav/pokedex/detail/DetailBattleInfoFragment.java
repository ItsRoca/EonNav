package com.example.eonnav.pokedex.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.example.eonnav.R;

public class DetailBattleInfoFragment extends Fragment {

    private TextView textName;
    private TextView textNumber;

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

        if (data != null) {

            String name = data.getString("name");
            String number = data.getString("number");

            textName.setText(name);
            textNumber.setText(number);
        }

        return view;

    }

}