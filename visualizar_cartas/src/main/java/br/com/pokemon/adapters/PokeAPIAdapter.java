package br.com.pokemon.adapters;

import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.cartas.factory.PokeApiCartaFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

/**
 * PokéAPI oficial. O algoritmo de busca fica em {@link AbstractPokeApiAdapter};
 * aqui ficam apenas a URL e a fábrica, que faz requisições adicionais para os
 * tipos e as habilidades.
 */
@Profile("real")
@Component
public class PokeAPIAdapter extends AbstractPokeApiAdapter {

    private static final String URL_POKEMON = "https://pokeapi.co/api/v2/pokemon/";

    private final CartaFactory cartaFactory = new PokeApiCartaFactory(this::buscarJson);

    public PokeAPIAdapter() {
        super(HttpClient.newHttpClient(), null);
    }

    @Override
    protected String urlDoPokemon(String nome) {
        return URL_POKEMON + nome;
    }

    @Override
    protected CartaFactory fabrica() {
        return cartaFactory;
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
