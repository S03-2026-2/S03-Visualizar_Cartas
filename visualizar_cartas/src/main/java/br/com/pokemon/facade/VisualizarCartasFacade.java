package br.com.pokemon.facade;

import br.com.pokemon.autenticacao.Autenticacao;
import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import br.com.pokemon.user.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Fachada dos casos de uso de visualização de cartas.
 */
@Service
public class VisualizarCartasFacade {

    private final Autenticacao autenticacao;
    private final Provider<List<Carta>, Integer> distribuicaoProvider;
    private final Provider<Carta, String> pokeApiProvider;

    public VisualizarCartasFacade(Autenticacao autenticacao,
                                  Provider<List<Carta>, Integer> distribuicaoProvider,
                                  Provider<Carta, String> pokeApiProvider) {
        this.autenticacao = Objects.requireNonNull(autenticacao, "Faça a autenticação");
        this.distribuicaoProvider = Objects.requireNonNull(
                distribuicaoProvider, "Insira o provider de distribuicao");
        this.pokeApiProvider = Objects.requireNonNull(
                pokeApiProvider, "Insira o provider da PokeAPI");
    }

    /**
     * Inicia a sessão do usuário.
     *
     * @param email e-mail informado
     * @param senha senha informada
     * @return usuário autenticado
     */
    public User iniciarSessao(String email, String senha) {
        return autenticacao.iniciarSessao(email, senha);
    }

    /**
     * Lista as cartas distribuídas para o usuário.
     *
     * @param usuarioId identificador do usuário
     * @param ordenar   indica se as cartas devem ser ordenadas pelo nome
     * @return cartas do usuário
     */
    public List<Carta> listarCartas(int usuarioId, boolean ordenar)
            throws IOException, InterruptedException {
        List<Carta> distribuidas = distribuicaoProvider.get(usuarioId);
        List<Carta> cartas = distribuidas == null ? new ArrayList<>() : new ArrayList<>(distribuidas);

        if (ordenar) {
            cartas.sort(Comparator.comparing(Carta::getNome));
        }

        return cartas;
    }

    /**
     * Detalha uma carta consultando a PokéAPI.
     *
     * @param id identificador da carta
     * @return carta detalhada
     */
    public Carta detalharCarta(int id) throws IOException, InterruptedException {
        return pokeApiProvider.get(String.valueOf(id));
    }
}
