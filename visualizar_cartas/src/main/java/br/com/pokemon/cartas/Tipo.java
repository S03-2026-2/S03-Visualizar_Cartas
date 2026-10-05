package br.com.pokemon.cartas;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tipo {

    private static final Pattern URL_TIPO = Pattern.compile(".*/type/(\\d+)/?");

    private final int id;
    private final String nome;

    public Tipo(int id, String nome) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do tipo deve ser positivo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo é obrigatório.");
        }
        this.id = id;
        this.nome = nome.trim();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    // Recebe o JSON completo retornado por /pokemon/{id}/.
    public static List<Tipo> getTipos(JSONObject pokemon) {
        Objects.requireNonNull(pokemon, "O JSON do Pokémon é obrigatório.");
        JSONArray dados = pokemon.getJSONArray("types");

        if (dados.length() < 1 || dados.length() > 2) {
            throw new IllegalArgumentException("A carta deve ter um ou dois tipos.");
        }

        Map<Integer, Tipo> tiposPorSlot = new TreeMap<>();
        for (int i = 0; i < dados.length(); i++) {
            JSONObject entrada = dados.getJSONObject(i);
            int slot = entrada.getInt("slot");
            if (slot < 1 || slot > 2 || tiposPorSlot.containsKey(slot)) {
                throw new IllegalArgumentException("Slot de tipo inválido ou repetido.");
            }

            JSONObject tipo = entrada.getJSONObject("type");
            Matcher resultado = URL_TIPO.matcher(tipo.getString("url"));
            if (!resultado.matches()) {
                throw new IllegalArgumentException("URL de tipo inválida.");
            }

            int id = Integer.parseInt(resultado.group(1));
            tiposPorSlot.put(slot, new Tipo(id, tipo.getString("name")));
        }

        return List.copyOf(tiposPorSlot.values());
    }
}

