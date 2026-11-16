package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.cartas.factory.GitCartaFactory;
import br.com.pokemon.interfaces.Provider;
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
import java.util.regex.Pattern;

/**
 * Espelho da PokéAPI servido pelo repositório GitHub da PokeAPI.
 *
 * <p>Usa apenas {@code pokemon/{id}/index.json}; tipos e habilidades vêm dos
 * campos embutidos, sem requisições adicionais. A geração da habilidade fica
 * vazia nesse adapter.</p>
 */

public class PokeApiGitAdapter implements Provider<Carta, String> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();
    private final CartaFactory cartaFactory = new GitCartaFactory();
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

        JSONObject json = buscarJson(baseUrl + "/pokemon/" + id + "/index.json");
        Carta carta = cartaFactory.criar(json);

        return carta;
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
}
