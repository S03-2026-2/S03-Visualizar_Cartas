package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Sprite;
import br.com.pokemon.cartas.Tipo;
import br.com.pokemon.interfaces.Provider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PokeAPIAdapter implements Provider<Carta, String> {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Pattern URL_PATTERN = Pattern.compile(".*/(\\d+)/?");

    @Override
    public Carta get(String nome) throws IOException, InterruptedException {
        JSONObject json = buscarJson("https://pokeapi.co/api/v2/pokemon/" + nome);

        Tipo[] tipos = buscarTipos(json);
        Sprite[] sprites = buscarSprites(json);
        Habilidade[] habilidades = buscarHabilidades(json);

        String urlCry = "";
        if (json.has("cries")) {
            JSONObject cries = json.optJSONObject("cries");
            if (cries != null) {
                urlCry = cries.optString("latest", cries.optString("legacy", ""));
            }
        }

        return new Carta(
                json.getInt("id"),
                json.optInt("base_experience", 0),
                json.getString("name"),
                urlCry,
                (float) json.getDouble("height"),
                (float) json.getDouble("weight"),
                habilidades,
                tipos,
                sprites
        );
    }

    private Tipo[] buscarTipos(JSONObject pokemonJson) throws IOException, InterruptedException {
        if (!pokemonJson.has("types")) {
            return new Tipo[0];
        }

        JSONArray typesArray = pokemonJson.getJSONArray("types");
        if (typesArray.length() < 1 || typesArray.length() > 2) {
            throw new IllegalArgumentException("A carta deve ter um ou dois tipos.");
        }

        List<Tipo> tipos = new ArrayList<>();
        for (int i = 0; i < typesArray.length(); i++) {
            JSONObject typeEntry = typesArray.getJSONObject(i);
            int slot = typeEntry.getInt("slot");
            if (slot < 1 || slot > 2) {
                throw new IllegalArgumentException("Slot de tipo inválido ou repetido.");
            }

            JSONObject type = typeEntry.getJSONObject("type");
            String url = type.getString("url");
            JSONObject typeJson = buscarJson(url);

            int typeId = extrairId(url);
            String nome = typeJson.optString("name", type.optString("name", ""));
            Tipo t = new Tipo(typeId, nome);
            t.setUrl(url);
            tipos.add(t);
        }

        return tipos.toArray(new Tipo[0]);
    }

    private Sprite[] buscarSprites(JSONObject pokemonJson) {
        if (!pokemonJson.has("sprites")) {
            return new Sprite[0];
        }

        List<Sprite> sprites = new ArrayList<>();
        extrairSprites(pokemonJson.getJSONObject("sprites"), "", sprites);
        return sprites.toArray(new Sprite[0]);
    }

    private void extrairSprites(JSONObject objeto, String prefixo, List<Sprite> sprites) {
        for (String chave : new java.util.TreeSet<>(objeto.keySet())) {
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

    private Habilidade[] buscarHabilidades(JSONObject pokemonJson) throws IOException, InterruptedException {
        if (!pokemonJson.has("abilities")) {
            return new Habilidade[0];
        }

        JSONArray abilitiesArray = pokemonJson.getJSONArray("abilities");
        List<Habilidade> habilidades = new ArrayList<>();

        for (int i = 0; i < abilitiesArray.length(); i++) {
            JSONObject abilityEntry = abilitiesArray.getJSONObject(i);
            if (abilityEntry.has("ability")) {
                JSONObject ability = abilityEntry.getJSONObject("ability");
                String url = ability.getString("url");
                JSONObject abilityJson = buscarJson(url);

                int abilityId = extrairId(url);
                String nome = abilityJson.optString("name", ability.optString("name", ""));
                String geracao = "";
                if (abilityJson.has("generation")) {
                    JSONObject generation = abilityJson.optJSONObject("generation");
                    if (generation != null) {
                        geracao = generation.optString("name", "");
                    }
                }

                habilidades.add(new Habilidade(abilityId, nome, geracao));
            }
        }

        return habilidades.toArray(new Habilidade[0]);
    }

    private JSONObject buscarJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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

/*
package br.com.pokemon.cartas;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class Habilidade {

    private int id;
    private String nome;
    private String geracao;

    public static Habilidade getHabilidade(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/ability/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Erro ao buscar habilidade " + id + ": HTTP " + response.statusCode());
        }

        JSONObject json = new JSONObject(response.body());

        Habilidade habilidade = new Habilidade();
        habilidade.id = json.getInt("id");
        habilidade.nome = json.getString("name");
        habilidade.geracao = json.getJSONObject("generation").getString("name");

        return habilidade;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getGeracao() {
        return geracao;
    }
}
*/
