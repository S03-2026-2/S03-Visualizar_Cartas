package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Sprite;
import br.com.pokemon.cartas.Tipo;
import br.com.pokemon.interfaces.Provider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Espelho da PokéAPI servido pelo repositório GitHub da PokeAPI.
 *
 * <p>Usa apenas {@code pokemon/{id}/index.json}; tipos e habilidades vêm dos
 * campos embutidos, sem requisições adicionais. A geração da habilidade fica
 * vazia nesse adapter.</p>
 */
@Profile("mock")
@Component
public class PokeApiGitAdapter implements Provider<Carta, String> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");
    private static final Pattern URL_PATTERN = Pattern.compile(".*/(\\d+)/?");

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();
    private final Map<String, Carta> cache = new ConcurrentHashMap<>();
    private final String baseUrl;

    public PokeApiGitAdapter(@Value("${app.pokeapi.git.base-url}") String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
    }

    @Override
    public Carta get(String id) throws IOException, InterruptedException {
        if (id == null || !ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("O id da carta deve ser numérico: " + id);
        }

        Carta emCache = cache.get(id);
        if (emCache != null) {
            return emCache;
        }

        JSONObject json = buscarJson(baseUrl + "/pokemon/" + id + "/index.json");

        Carta carta = new Carta(
                json.getInt("id"),
                json.optInt("base_experience", 0),
                json.getString("name"),
                buscarUrlCry(json),
                (float) json.optDouble("height", 0),
                (float) json.optDouble("weight", 0),
                buscarHabilidades(json),
                buscarTipos(json),
                buscarSprites(json)
        );

        cache.put(id, carta);
        return carta;
    }

    private Tipo[] buscarTipos(JSONObject pokemonJson) {
        if (!pokemonJson.has("types")) {
            return new Tipo[0];
        }

        JSONArray typesArray = pokemonJson.getJSONArray("types");
        List<Tipo> tipos = new ArrayList<>();
        for (int i = 0; i < typesArray.length(); i++) {
            JSONObject type = typesArray.getJSONObject(i).optJSONObject("type");
            if (type == null) {
                continue;
            }

            String url = type.optString("url", "");
            Tipo tipo = new Tipo(extrairId(url), type.optString("name", ""));
            tipo.setUrl(url);
            tipos.add(tipo);
        }

        return tipos.toArray(new Tipo[0]);
    }

    private Habilidade[] buscarHabilidades(JSONObject pokemonJson) {
        if (!pokemonJson.has("abilities")) {
            return new Habilidade[0];
        }

        JSONArray abilitiesArray = pokemonJson.getJSONArray("abilities");
        List<Habilidade> habilidades = new ArrayList<>();
        for (int i = 0; i < abilitiesArray.length(); i++) {
            JSONObject ability = abilitiesArray.getJSONObject(i).optJSONObject("ability");
            if (ability == null) {
                continue;
            }

            String url = ability.optString("url", "");
            habilidades.add(new Habilidade(extrairId(url), ability.optString("name", ""), ""));
        }

        return habilidades.toArray(new Habilidade[0]);
    }

    private String buscarUrlCry(JSONObject pokemonJson) {
        JSONObject cries = pokemonJson.optJSONObject("cries");
        if (cries == null) {
            return "";
        }

        String url = cries.optString("latest", "");
        return url.isBlank() ? cries.optString("legacy", "") : url;
    }

    private Sprite[] buscarSprites(JSONObject pokemonJson) {
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

    private JSONObject buscarJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(TIMEOUT)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Erro ao buscar " + url + ": HTTP " + response.statusCode());
        }

        return new JSONObject(response.body());
    }

    private int extrairId(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}
