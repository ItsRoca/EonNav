package com.example.eonnav.utils;

import android.graphics.Color;
import com.example.eonnav.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeUtils {

    private static final Map<String, Integer> TYPE_ICONS = new HashMap<>();

    static {
        TYPE_ICONS.put("fire", R.drawable.tipo_fuego);
        TYPE_ICONS.put("water", R.drawable.tipo_agua);
        TYPE_ICONS.put("grass", R.drawable.tipo_planta);
        TYPE_ICONS.put("electric", R.drawable.tipo_electrico);
        TYPE_ICONS.put("ice", R.drawable.tipo_hielo);
        TYPE_ICONS.put("normal", R.drawable.tipo_normal);
        TYPE_ICONS.put("fighting", R.drawable.tipo_lucha);
        TYPE_ICONS.put("poison", R.drawable.tipo_veneno);
        TYPE_ICONS.put("ground", R.drawable.tipo_tierra);
        TYPE_ICONS.put("flying", R.drawable.tipo_volador);
        TYPE_ICONS.put("psychic", R.drawable.tipo_psiquico);
        TYPE_ICONS.put("bug", R.drawable.tipo_bicho);
        TYPE_ICONS.put("rock", R.drawable.tipo_roca);
        TYPE_ICONS.put("ghost", R.drawable.tipo_fantasma);
        TYPE_ICONS.put("dragon", R.drawable.tipo_dragon);
        TYPE_ICONS.put("dark", R.drawable.tipo_siniestro);
        TYPE_ICONS.put("steel", R.drawable.tipo_acero);
        TYPE_ICONS.put("fairy", R.drawable.tipo_hada);
        TYPE_ICONS.put("stellar", R.drawable.tipo_astral);
    }

    private static final List<String> TYPES = Arrays.asList(
        "fire", "water", "grass", "electric", "ice",
        "fighting", "poison", "ground", "flying",
        "psychic", "bug", "rock", "ghost", "dragon",
        "dark", "steel", "fairy"
    );

    public static int getTypeColor(String type) {
        switch (type) {
            case "fire": return Color.parseColor("#F08030");
            case "water": return Color.parseColor("#6890F0");
            case "grass": return Color.parseColor("#78C850");
            case "electric": return Color.parseColor("#F8D030");
            case "ice": return Color.parseColor("#98D8D8");
            case "fighting": return Color.parseColor("#C03028");
            case "poison": return Color.parseColor("#A040A0");
            case "ground": return Color.parseColor("#E0C068");
            case "flying": return Color.parseColor("#A890F0");
            case "psychic": return Color.parseColor("#F85888");
            case "bug": return Color.parseColor("#A8B820");
            case "rock": return Color.parseColor("#B8A038");
            case "ghost": return Color.parseColor("#705898");
            case "dragon": return Color.parseColor("#7038F8");
            case "dark": return Color.parseColor("#705848");
            case "steel": return Color.parseColor("#B8B8D0");
            case "fairy": return Color.parseColor("#EE99AC");
            case "normal": return Color.parseColor("#A8A878");
            default: return Color.GRAY;
        }
    }

    public static int darkenColor(int color) {
        float factor = 0.7f;
        return Color.rgb(
            (int)(Color.red(color) * factor),
            (int)(Color.green(color) * factor),
            (int)(Color.blue(color) * factor)
        );
    }

    public static int getContrastColor(int color) {
        double luminance = (0.299 * Color.red(color) +
            0.587 * Color.green(color) +
            0.114 * Color.blue(color)) / 255;

        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static List<String> getAllTypes() {
        return TYPES;
    }

    public static int getTypeIcon(String type) {
        if (type == null) return R.drawable.tipo_desconocido;
        return TYPE_ICONS.getOrDefault(type.toLowerCase(), R.drawable.tipo_desconocido);
    }
}
