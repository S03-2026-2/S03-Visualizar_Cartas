package br.com.pokemon.cartas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import org.json.JSONObject;

public class Sprite {

    private final String nome;
    private final String url;

    public Sprite(String nome, String url) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do sprite é obrigatório.");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("A URL do sprite é obrigatória.");
        }
        this.nome = nome.trim();
        this.url = url.trim();
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    // Recebe o JSON completo retornado por /pokemon/{id}/.
    public static List<Sprite> getSprites(JSONObject pokemon) {
        Objects.requireNonNull(pokemon, "O JSON do Pokémon é obrigatório.");

        List<Sprite> sprites = new ArrayList<>();
        extrairSprites(pokemon.getJSONObject("sprites"), "", sprites);
        return List.copyOf(sprites);
    }

    private static void extrairSprites(
            JSONObject objeto, String prefixo, List<Sprite> sprites) {

        for (String chave : new TreeSet<>(objeto.keySet())) {
            Object valor = objeto.get(chave);
            String nome = prefixo.isEmpty() ? chave : prefixo + "." + chave;

            if (valor instanceof JSONObject grupo) {
                extrairSprites(grupo, nome, sprites);
            } else if (valor instanceof String url && !url.isBlank()) {
                sprites.add(new Sprite(nome, url));
            }
            // Valores null representam imagens indisponíveis e são ignorados.
        }
    }
}
