package com.example.eonnav.utils;

import com.example.eonnav.R;
import java.util.HashMap;
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

    public static int getTypeIcon(String type) {
        if (type == null) return R.drawable.tipo_desconocido;
        return TYPE_ICONS.getOrDefault(type.toLowerCase(), R.drawable.tipo_desconocido);
    }
}
