package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import br.com.pokemon.mocks.MockJsonLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

/**
 * Distribuição de demonstração: lê os IDs de cada usuário em
 * {@code mocks/distribuicao.json} e busca cada carta no espelho da PokéAPI.
 */
@Profile("mock")
@Component
public class MockDistribuicaoAdapter implements Provider<List<Carta>, Integer> {

    private static final TypeReference<Map<String, List<Integer>>> DISTRIBUICAO =
            new TypeReference<>() {
            };

    private final MockJsonLoader loader;
    private final Provider<Carta, String> cartasProvider;

    public MockDistribuicaoAdapter(MockJsonLoader loader, Provider<Carta, String> cartasProvider) {
        this.loader = loader;
        this.cartasProvider = cartasProvider;
    }

    @Override
    public List<Carta> get(Integer usuarioId) throws IOException, InterruptedException {
        Map<String, List<Integer>> distribuicao =
                loader.read("mocks/distribuicao.json", DISTRIBUICAO);

        List<Integer> ids = distribuicao.get(String.valueOf(usuarioId));
        if (ids == null) {
            return new ArrayList<>();
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Carta>> futures = ids.stream()
                    .map(id -> CompletableFuture.supplyAsync(() -> buscarCarta(id), executor))
                    .toList();

            try {
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            } catch (CompletionException e) {
                Throwable original = desembrulhar(e.getCause());
                if (original instanceof IOException ioException) {
                    throw ioException;
                }
                if (original instanceof InterruptedException interruptedException) {
                    throw interruptedException;
                }
                if (original instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (original instanceof Error error) {
                    throw error;
                }
                throw new IOException("Falha inesperada ao buscar carta", original);
            }

            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    private Carta buscarCarta(Integer id) {
        try {
            return cartasProvider.get(String.valueOf(id));
        } catch (IOException | InterruptedException e) {
            throw new CompletionException(e);
        }
    }

    private Throwable desembrulhar(Throwable cause) {
        Throwable original = cause;
        while (original instanceof CompletionException && original.getCause() != null) {
            original = original.getCause();
        }
        return original;
    }
}
