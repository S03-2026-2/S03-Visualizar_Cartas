package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.cartas.factory.PokeApiCartaFactory;
import br.com.pokemon.interfaces.Provider;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeAPIAdapter implements Provider<Carta, String> {

    private static final HttpClient client = HttpClient.newHttpClient();

    private final CartaFactory cartaFactory = new PokeApiCartaFactory(this::buscarJson);

    @Override
    public Carta get(String nome) throws IOException, InterruptedException {
        JSONObject json = buscarJson("https://pokeapi.co/api/v2/pokemon/" + nome);
        return cartaFactory.criar(json);
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
