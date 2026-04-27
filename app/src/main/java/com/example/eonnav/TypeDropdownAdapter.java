package com.example.eonnav;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.eonnav.utils.TypeUtils;

import java.util.List;

public class TypeDropdownAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;

    public TypeDropdownAdapter(@NonNull Context context, @NonNull List<String> types) {
        super(context, 0, types);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_type_dropdown, parent, false);
        }

        String type = getItem(position);

        ImageView icon = view.findViewById(R.id.typeIcon);

        Drawable drawable = ContextCompat.getDrawable(
                getContext(),
                TypeUtils.getTypeIcon(type)
        );

        if (drawable != null) {
            // Escalado proporcional por altura (20dp)
            float density = getContext().getResources().getDisplayMetrics().density;
            int targetHeight = (int) (20 * density);

            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            float ratio = (float) w / h;

            int targetWidth = (int) (targetHeight * ratio);
            drawable.setBounds(0, 0, targetWidth, targetHeight);
            icon.setImageDrawable(drawable);
        }

        return view;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public CharSequence convertResultToString(Object resultValue) {

        return "";
    }

}