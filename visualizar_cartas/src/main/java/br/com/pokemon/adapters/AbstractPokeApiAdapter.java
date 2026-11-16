package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.interfaces.Provider;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * Template Method para os adapters que obtêm uma {@link Carta} de uma fonte no
 * formato da PokéAPI.
 *
 * <p>{@link #buscarCarta(String)} é o método template: fixa o algoritmo
 * (montar a URL, buscar o JSON e entregá-lo à fábrica) e delega às subclasses
 * apenas os passos que variam entre as fontes:</p>
 * <ul>
 *     <li>{@link #urlDoPokemon(String)}: onde fica o JSON do Pokémon;</li>
 *     <li>{@link #fabrica()}: qual {@link CartaFactory} monta a carta
 *     (Factory Method chamado de dentro do Template Method).</li>
 * </ul>
 *
 * <p>O acesso HTTP ({@link #buscarJson(String)}) é comum às subclasses e também
 * serve às fábricas que precisam de requisições adicionais.</p>
 */
public abstract class AbstractPokeApiAdapter implements Provider<Carta, String> {

    private final HttpClient client;
    private final Duration timeoutRequisicao;

    /**
     * @param client            cliente HTTP usado em todas as requisições
     * @param timeoutRequisicao timeout de cada requisição, ou {@code null} para não limitar
     */
    protected AbstractPokeApiAdapter(HttpClient client, Duration timeoutRequisicao) {
        this.client = Objects.requireNonNull(client, "O cliente HTTP é obrigatório.");
        this.timeoutRequisicao = timeoutRequisicao;
    }

    @Override
    public Carta get(String chave) throws IOException, InterruptedException {
        return buscarCarta(chave);
    }

    /**
     * Método template: o algoritmo é fixo e não pode ser sobrescrito.
     */
    protected final Carta buscarCarta(String chave) throws IOException, InterruptedException {
        JSONObject json = buscarJson(urlDoPokemon(chave));
        return fabrica().criar(json);
    }

    /** Passo variável: URL do JSON do Pokémon identificado pela chave. */
    protected abstract String urlDoPokemon(String chave);

    /** Passo variável: fábrica que converte o JSON da fonte em {@link Carta}. */
    protected abstract CartaFactory fabrica();

    protected final JSONObject buscarJson(String url) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if (timeoutRequisicao != null) {
            request.timeout(timeoutRequisicao);
        }

        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Erro ao buscar " + url + ": HTTP " + response.statusCode());
        }

        return new JSONObject(response.body());
    }
}
