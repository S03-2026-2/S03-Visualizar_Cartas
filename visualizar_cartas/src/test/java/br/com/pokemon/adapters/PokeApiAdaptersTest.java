package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Sprite;
import br.com.pokemon.cartas.factory.CartaFactory;
import br.com.pokemon.cartas.factory.GitCartaFactory;
import br.com.pokemon.cartas.factory.PokeApiCartaFactory;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa o método template de {@link AbstractPokeApiAdapter} e os passos
 * variáveis de cada adapter, servindo JSON no formato da PokéAPI localmente.
 */
class PokeApiAdaptersTest {

    private HttpServer servidor;
    private String base;
    private final Map<String, String> respostas = new ConcurrentHashMap<>();
    private final AtomicInteger requisicoes = new AtomicInteger();

    @BeforeEach
    void iniciarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", troca -> {
            requisicoes.incrementAndGet();
            String corpo = respostas.get(troca.getRequestURI().getPath());
            int status = corpo == null ? 404 : 200;
            byte[] bytes = (corpo == null ? "Not Found" : corpo).getBytes(StandardCharsets.UTF_8);
            troca.sendResponseHeaders(status, bytes.length);
            try (OutputStream saida = troca.getResponseBody()) {
                saida.write(bytes);
            }
        });
        servidor.start();
        base = "http://127.0.0.1:" + servidor.getAddress().getPort();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    // ---------- PokeApiGitAdapter (profile mock) ----------

    @Test
    void gitAdapterBuscaOJsonEEntregaAFabrica() throws Exception {
        respostas.put("/pokemon/25/index.json", pikachu(base));
        PokeApiGitAdapter adapter = new PokeApiGitAdapter(base + "/");

        Carta carta = adapter.get("25");

        assertInstanceOf(GitCartaFactory.class, adapter.fabrica());
        assertEquals(25, carta.getId());
        assertEquals("pikachu", carta.getNome());
        assertEquals(112, carta.getExperience());
        assertEquals(4f, carta.getAltura());
        assertEquals(60f, carta.getPeso());
        assertEquals("electric", carta.getTipos()[0].getNome());
        assertEquals(List.of("static", "lightning-rod"), nomes(carta.getHabilidades()));
        assertEquals("", carta.getHabilidades()[0].getGeracao());
        assertArrayEquals(new String[]{"front_default", "other.home.front_default"},
                Arrays.stream(carta.getSprites()).map(Sprite::getNome).toArray());
        // A fábrica do git não faz requisições extras.
        assertEquals(1, requisicoes.get());
    }

    @Test
    void gitAdapterMantemOCache() throws Exception {
        respostas.put("/pokemon/25/index.json", pikachu(base));
        PokeApiGitAdapter adapter = new PokeApiGitAdapter(base);

        Carta primeira = adapter.get("25");
        Carta segunda = adapter.get("25");

        assertSame(primeira, segunda);
        assertEquals(1, requisicoes.get());
    }

    @Test
    void gitAdapterValidaIdAntesDeBuscar() {
        PokeApiGitAdapter adapter = new PokeApiGitAdapter(base);

        assertThrows(IllegalArgumentException.class, () -> adapter.get("pikachu"));
        assertThrows(IllegalArgumentException.class, () -> adapter.get(null));
        assertEquals(0, requisicoes.get());
    }

    @Test
    void erroHttpEhPropagadoComAMesmaMensagem() {
        PokeApiGitAdapter adapter = new PokeApiGitAdapter(base);

        IOException erro = assertThrows(IOException.class, () -> adapter.get("99999"));
        assertEquals("Erro ao buscar " + base + "/pokemon/99999/index.json: HTTP 404", erro.getMessage());
    }

    // ---------- PokeAPIAdapter (profile real) ----------

    @Test
    void pokeApiAdapterUsaAUrlOficialEAFabricaDaPokeApi() {
        PokeAPIAdapter adapter = new PokeAPIAdapter();

        assertEquals("https://pokeapi.co/api/v2/pokemon/pikachu", adapter.urlDoPokemon("pikachu"));
        assertInstanceOf(PokeApiCartaFactory.class, adapter.fabrica());
    }

    @Test
    void templateUsaOBuscarJsonComumNasRequisicoesExtrasDaFabrica() throws Exception {
        respostas.put("/pokemon/pikachu", pikachu(base));
        respostas.put("/type/13/", "{\"name\":\"electric\"}");
        respostas.put("/ability/9/", "{\"name\":\"static\",\"generation\":{\"name\":\"generation-iii\"}}");
        respostas.put("/ability/31/", "{\"name\":\"lightning-rod\",\"generation\":{\"name\":\"generation-iii\"}}");

        // Mesma fábrica do PokeAPIAdapter, mas apontando para o servidor local.
        AbstractPokeApiAdapter adapter = new AbstractPokeApiAdapter(HttpClient.newHttpClient(), null) {
            private final CartaFactory cartaFactory = new PokeApiCartaFactory(this::buscarJson);

            @Override
            protected String urlDoPokemon(String nome) {
                return base + "/pokemon/" + nome;
            }

            @Override
            protected CartaFactory fabrica() {
                return cartaFactory;
            }
        };

        Carta carta = adapter.get("pikachu");

        assertEquals(25, carta.getId());
        assertEquals(13, carta.getTipos()[0].getId());
        assertEquals("generation-iii", carta.getHabilidades()[0].getGeracao());
        // 1 Pokémon + 1 tipo + 2 habilidades.
        assertEquals(4, requisicoes.get());
    }

    @Test
    void erroNaRequisicaoExtraDaFabricaEhPropagado() {
        respostas.put("/pokemon/pikachu", pikachu(base));
        AbstractPokeApiAdapter adapter = new AbstractPokeApiAdapter(HttpClient.newHttpClient(), null) {
            private final CartaFactory cartaFactory = new PokeApiCartaFactory(this::buscarJson);

            @Override
            protected String urlDoPokemon(String nome) {
                return base + "/pokemon/" + nome;
            }

            @Override
            protected CartaFactory fabrica() {
                return cartaFactory;
            }
        };

        IOException erro = assertThrows(IOException.class, () -> adapter.get("pikachu"));
        assertTrue(erro.getMessage().endsWith("HTTP 404"), erro.getMessage());
    }

    private static List<String> nomes(Habilidade[] habilidades) {
        return Arrays.stream(habilidades).map(Habilidade::getNome).toList();
    }

    private static String pikachu(String base) {
        return new JSONObject("""
                {
                  "id": 25,
                  "name": "pikachu",
                  "base_experience": 112,
                  "height": 4,
                  "weight": 60,
                  "cries": {"latest": "https://cries/latest/25.ogg", "legacy": "https://cries/legacy/25.ogg"},
                  "types": [
                    {"slot": 1, "type": {"name": "electric", "url": "BASE/type/13/"}}
                  ],
                  "abilities": [
                    {"ability": {"name": "static", "url": "BASE/ability/9/"}, "is_hidden": false, "slot": 1},
                    {"ability": {"name": "lightning-rod", "url": "BASE/ability/31/"}, "is_hidden": true, "slot": 3}
                  ],
                  "sprites": {
                    "front_default": "https://sprites/25.png",
                    "back_default": null,
                    "other": {"home": {"front_default": "https://sprites/home/25.png", "front_shiny": null}}
                  }
                }
                """.replace("BASE", base)).toString();
    }
}
