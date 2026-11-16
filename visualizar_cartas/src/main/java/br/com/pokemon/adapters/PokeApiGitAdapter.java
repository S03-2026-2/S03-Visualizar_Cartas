package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.cartas.factory.GitCartaFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Espelho da PokéAPI servido pelo repositório GitHub da PokeAPI.
 *
 * <p>Usa apenas {@code pokemon/{id}/index.json}; tipos e habilidades vêm dos
 * campos embutidos, sem requisições adicionais. A geração da habilidade fica
 * vazia nesse adapter. O algoritmo de busca fica em
 * {@link AbstractPokeApiAdapter}.</p>
 */
@Profile("mock")
@Component
public class PokeApiGitAdapter extends AbstractPokeApiAdapter {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    private final CartaFactory cartaFactory = new GitCartaFactory();
    private final Map<String, Carta> cache = new ConcurrentHashMap<>();
    private final String baseUrl;

    public PokeApiGitAdapter(@Value("${app.pokeapi.git.base-url}") String baseUrl) {
        super(HttpClient.newBuilder().connectTimeout(TIMEOUT).build(), TIMEOUT);
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

        Carta carta = buscarCarta(id);

        cache.put(id, carta);
        return carta;
    }

    @Override
    protected String urlDoPokemon(String id) {
        return baseUrl + "/pokemon/" + id + "/index.json";
    }

    @Override
    protected CartaFactory fabrica() {
        return cartaFactory;
    }
}
