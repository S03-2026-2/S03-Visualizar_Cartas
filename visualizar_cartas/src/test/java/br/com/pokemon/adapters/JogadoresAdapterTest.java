package br.com.pokemon.adapters;

import br.com.pokemon.autenticacao.FalhaAutenticacaoException;
import br.com.pokemon.user.User;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.RESPOSTA_INVALIDA;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.SERVICO_INDISPONIVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simula o endpoint {@code POST /auth/login} do sistema de Jogadores.
 */
class JogadoresAdapterTest {

    private HttpServer servidor;

    @AfterEach
    void pararServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void loginAceitoDevolveUsuarioComIdDoJogador() throws IOException {
        AtomicReference<String> corpoRecebido = new AtomicReference<>();
        AtomicReference<String> contentType = new AtomicReference<>();
        JogadoresAdapter adapter = iniciar(200,
                "{\"token\":\"abc\",\"tipo\":\"Bearer\",\"expiraEm\":\"2026-11-16T20:00:00\",\"jogadorId\":7}",
                corpoRecebido, contentType);

        Optional<User> usuario = adapter.autenticar("ash@pokemon.com", "pikachu");

        assertTrue(usuario.isPresent());
        assertEquals(7, usuario.get().getId());
        assertEquals("ash@pokemon.com", usuario.get().getNome());
        assertEquals("application/json", contentType.get());

        JSONObject corpo = new JSONObject(corpoRecebido.get());
        assertEquals("ash@pokemon.com", corpo.getString("email"));
        assertEquals("pikachu", corpo.getString("senha"));
    }

    @Test
    void status401SignificaCredenciaisInvalidas() throws IOException {
        JogadoresAdapter adapter = iniciar(401, "{\"mensagem\":\"E-mail ou senha incorretos\"}",
                new AtomicReference<>(), new AtomicReference<>());

        assertTrue(adapter.autenticar("ash@pokemon.com", "errada").isEmpty());
    }

    @Test
    void erroDoServidorViraServicoIndisponivel() throws IOException {
        JogadoresAdapter adapter = iniciar(500, "{}",
                new AtomicReference<>(), new AtomicReference<>());

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> adapter.autenticar("ash@pokemon.com", "pikachu"));
        assertEquals(SERVICO_INDISPONIVEL, erro.getMotivo());
    }

    @Test
    void respostaSemJogadorIdEhInvalida() throws IOException {
        JogadoresAdapter adapter = iniciar(200, "{\"token\":\"abc\"}",
                new AtomicReference<>(), new AtomicReference<>());

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> adapter.autenticar("ash@pokemon.com", "pikachu"));
        assertEquals(RESPOSTA_INVALIDA, erro.getMotivo());
    }

    @Test
    void servicoForaDoArViraServicoIndisponivel() throws IOException {
        // Reserva uma porta livre e a libera, para que a conexão seja recusada.
        HttpServer temporario = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        int porta = temporario.getAddress().getPort();
        temporario.stop(0);

        JogadoresAdapter adapter = new JogadoresAdapter("http://127.0.0.1:" + porta + "/");

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> adapter.autenticar("ash@pokemon.com", "pikachu"));
        assertEquals(SERVICO_INDISPONIVEL, erro.getMotivo());
    }

    private JogadoresAdapter iniciar(int status, String resposta,
                                     AtomicReference<String> corpoRecebido,
                                     AtomicReference<String> contentType) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/auth/login", troca -> {
            corpoRecebido.set(new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            contentType.set(troca.getRequestHeaders().getFirst("Content-Type"));

            byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
            troca.getResponseHeaders().set("Content-Type", "application/json");
            troca.sendResponseHeaders(status, bytes.length);
            try (OutputStream saida = troca.getResponseBody()) {
                saida.write(bytes);
            }
        });
        servidor.start();

        return new JogadoresAdapter("http://127.0.0.1:" + servidor.getAddress().getPort());
    }
}
