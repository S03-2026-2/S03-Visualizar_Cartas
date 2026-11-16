package br.com.pokemon.adapters;

import br.com.pokemon.autenticacao.FalhaAutenticacaoException;
import br.com.pokemon.interfaces.AutenticacaoProvider;
import br.com.pokemon.user.User;
import org.json.JSONException;
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
import java.util.Optional;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.RESPOSTA_INVALIDA;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.SERVICO_INDISPONIVEL;

/**
 * Autenticação real no sistema de Jogadores ({@code S03-2026-2/Jogadores}).
 *
 * <p>Contrato: {@code POST /auth/login} com {@code {"email", "senha"}} em JSON.
 * Responde 200 com {@code {"token", "tipo", "expiraEm", "jogadorId"}} ou 401
 * quando as credenciais são inválidas.</p>
 *
 * <p>O login do Jogadores não devolve nome nem ranking: o e-mail é usado como
 * nome de exibição e o ranking fica 0 até existir um endpoint para esses dados.</p>
 */
@Profile("real")
@Component
public class JogadoresAdapter implements AutenticacaoProvider {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();
    private final URI urlLogin;

    public JogadoresAdapter(@Value("${app.jogadores.base-url}") String baseUrl) {
        String base = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.urlLogin = URI.create(base + "/auth/login");
    }

    @Override
    public Optional<User> autenticar(String email, String senha) {
        String corpo = new JSONObject()
                .put("email", email)
                .put("senha", senha)
                .toString();

        HttpRequest request = HttpRequest.newBuilder(urlLogin)
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();

        HttpResponse<String> response = enviar(request);

        if (response.statusCode() == 401) {
            return Optional.empty();
        }
        if (response.statusCode() != 200) {
            throw new FalhaAutenticacaoException(SERVICO_INDISPONIVEL,
                    new IOException("POST " + urlLogin + ": HTTP " + response.statusCode()));
        }

        return Optional.of(converter(response.body(), email));
    }

    private HttpResponse<String> enviar(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new FalhaAutenticacaoException(SERVICO_INDISPONIVEL, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FalhaAutenticacaoException(SERVICO_INDISPONIVEL, e);
        }
    }

    private User converter(String corpo, String email) {
        try {
            int jogadorId = new JSONObject(corpo).getInt("jogadorId");
            if (jogadorId <= 0) {
                throw new FalhaAutenticacaoException(RESPOSTA_INVALIDA);
            }
            return new User(jogadorId, email, 0);
        } catch (JSONException e) {
            throw new FalhaAutenticacaoException(RESPOSTA_INVALIDA, e);
        }
    }
}
