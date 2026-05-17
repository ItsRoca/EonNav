package com.example.eonnav.pokemon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaknessResistancesCalculator {

    // CALCULAR DAÑO DE CADA TIPO ATACANTE
    public static Map<String, Double> calculate(List<JSONObject> typeDataList)
            throws JSONException {

        Map<String, Double> damageMap = new HashMap<>();

        // Lista de los tipos
        String[] allTypes = {
                "normal","fire","water","electric","grass","ice",
                "fighting","poison","ground","flying","psychic",
                "bug","rock","ghost","dragon","dark","steel","fairy"
        };

        // Inicializar todos a efectividad neutra
        for (String type : allTypes) {
            damageMap.put(type, 1.0);
        }

        // Aplica efectividades y resistencias especificas de cada tipo
        for (JSONObject typeData : typeDataList) {
            applyTypeEffect(typeData, damageMap);
        }

        return damageMap;
    }

    // APLICAR EFECTIVIDADES SOBRE LOS TIPOS DEL POKEMON
    private static void applyTypeEffect(JSONObject typeData, Map<String, Double> map)
            throws JSONException {

        JSONObject relations = typeData.getJSONObject("damage_relations");

        JSONArray doubleFrom = relations.getJSONArray("double_damage_from");
        for (int i = 0; i < doubleFrom.length(); i++) {
            String t = doubleFrom.getJSONObject(i).getString("name");
            map.put(t, map.get(t) * 2);
        }

        JSONArray halfFrom = relations.getJSONArray("half_damage_from");
        for (int i = 0; i < halfFrom.length(); i++) {
            String t = halfFrom.getJSONObject(i).getString("name");
            map.put(t, map.get(t) * 0.5);
        }

        JSONArray noFrom = relations.getJSONArray("no_damage_from");
        for (int i = 0; i < noFrom.length(); i++) {
            String t = noFrom.getJSONObject(i).getString("name");
            map.put(t, 0.0);
        }
    }

    // CLASIFICAR TIPOS SEGUN EFECTIVIDAD
    public static WeaknessResistancesResult classify(Map<String, Double> map) {

        WeaknessResistancesResult result = new WeaknessResistancesResult();

        for (Map.Entry<String, Double> entry : map.entrySet()) {

            String type = entry.getKey();
            double value = entry.getValue();

            if (value == 4.0) result.x4.add(type);
            else if (value == 2.0) result.x2.add(type);
            else if (value == 1.0) result.x1.add(type);
            else if (value == 0.5) result.x05.add(type);
            else if (value == 0.25) result.x025.add(type);
            else if (value == 0.0) result.x0.add(type);
        }

        return result;
    }

}
