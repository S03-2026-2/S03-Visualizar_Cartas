package br.com.pokemon.facade;

import br.com.pokemon.autenticacao.Autenticacao;
import br.com.pokemon.autenticacao.FalhaAutenticacaoException;
import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import br.com.pokemon.user.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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


        @return
        @throws 
     
    public User iniciarSessao(String email, String senha) {
        return autenticacao.iniciarSessao(email, senha);
    }


     @param usuarioId
     @param ordenar
     @return nova

    public List<Carta> listarCartas(int usuarioId, boolean ordenar)
            throws IOException, InterruptedException {
        List<Carta> distribuidas = distribuicaoProvider.get(usuarioId);
        List<Carta> cartas = distribuidas == null ? new ArrayList<>() : new ArrayList<>(distribuidas);

        if (ordenar) {
            cartas.sort(Comparator.comparing(Carta::getNome));
        }

        return cartas;
    }


      Caso de uso "Detalhar carta".
     
      @param

    public Carta detalharCarta(int id) throws IOException, InterruptedException {
        return pokeApiProvider.get(String.valueOf(id));
    }
}
