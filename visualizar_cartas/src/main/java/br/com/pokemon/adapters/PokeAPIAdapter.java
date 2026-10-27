package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Sprite;
import br.com.pokemon.cartas.Tipo;
import br.com.pokemon.interfaces.Provider;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PokeAPIAdapter implements Provider {

    public static Carta getPokemon(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Erro ao buscar pokemon " + id + ": HTTP " + response.statusCode());
        }

        JSONObject json = new JSONObject(response.body());

        List<Tipo> tipos = Tipo.getTipos(json);
        List<Sprite> sprites = Sprite.getSprites(json);

        // Nota: Habilidades, experience, cry podem ser extraídos conforme necessário
        return new Carta(
                json.getInt("id"),
                json.optInt("base_experience", 0),
                json.getString("name"),
                json.optString("cries", json.optJSONObject("cries") != null ? 
                        json.getJSONObject("cries").optString("latest", "") : ""),
                (float) json.getDouble("height"),
                (float) json.getDouble("weight"),
                new Habilidade[0],
                tipos.toArray(new Tipo[0]),
                sprites.toArray(new Sprite[0])
        );
    }

    @Override
    public void get() {
        // Implementação conforme interface Provider
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
