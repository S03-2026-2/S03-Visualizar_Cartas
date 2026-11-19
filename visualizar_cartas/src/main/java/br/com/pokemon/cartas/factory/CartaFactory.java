package br.com.pokemon.cartas.factory;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Sprite;
import br.com.pokemon.cartas.Tipo;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cria uma {@link Carta} a partir do JSON de um Pokémon da PokéAPI.
 *
 * <p>Padrão Factory Method: {@link #criar(JSONObject)} monta a carta e delega às
 * subclasses a criação dos {@link Tipo} e das {@link Habilidade}, que variam
 * conforme a origem dos dados (PokéAPI real ou espelho no GitHub).</p>
 */
public abstract class CartaFactory {

    private static final Pattern URL_PATTERN = Pattern.compile(".*/(\\d+)/?");

    public Carta criar(JSONObject json) throws IOException, InterruptedException {
        return Carta.builder()
                .id(json.getInt("id"))
                .experience(json.optInt("base_experience", 0))
                .nome(json.getString("name"))
                .urlCry(criarUrlCry(json))
                .altura((float) json.getDouble("height"))
                .peso((float) json.getDouble("weight"))
                .habilidades(criarHabilidades(json))
                .tipos(criarTipos(json))
                .sprites(criarSprites(json))
                .build();
    }

    protected abstract Tipo[] criarTipos(JSONObject pokemonJson) throws IOException, InterruptedException;

    protected abstract Habilidade[] criarHabilidades(JSONObject pokemonJson) throws IOException, InterruptedException;

    protected int extrairId(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private String criarUrlCry(JSONObject pokemonJson) {
        JSONObject cries = pokemonJson.optJSONObject("cries");
        if (cries == null) {
            return "";
        }

        String url = cries.optString("latest", "");
        return url.isBlank() ? cries.optString("legacy", "") : url;
    }

    private Sprite[] criarSprites(JSONObject pokemonJson) {
        JSONObject spritesJson = pokemonJson.optJSONObject("sprites");
        if (spritesJson == null) {
            return new Sprite[0];
        }

        List<Sprite> sprites = new ArrayList<>();
        extrairSprites(spritesJson, "", sprites);
        return sprites.toArray(new Sprite[0]);
    }

    private void extrairSprites(JSONObject objeto, String prefixo, List<Sprite> sprites) {
        for (String chave : new TreeSet<>(objeto.keySet())) {
            Object valor = objeto.get(chave);
            String nome = prefixo.isEmpty() ? chave : prefixo + "." + chave;

            if (valor instanceof JSONObject grupo) {
                extrairSprites(grupo, nome, sprites);
            } else if (valor instanceof String url && !url.isBlank()) {
                Sprite sprite = new Sprite();
                sprite.setNome(nome);
                sprite.setUrl(url);
                sprites.add(sprite);
            }
        }
    }
}
