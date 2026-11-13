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
        List<Carta> cartas = new ArrayList<>();
        if (ids == null) {
            return cartas;
        }

        for (Integer id : ids) {
            Carta carta = cartasProvider.get(String.valueOf(id));
            if (carta != null) {
                cartas.add(carta);
            }
        }

        return cartas;
    }
}
