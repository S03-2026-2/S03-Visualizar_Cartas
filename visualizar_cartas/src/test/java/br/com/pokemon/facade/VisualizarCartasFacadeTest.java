package br.com.pokemon.facade;

import br.com.pokemon.autenticacao.Autenticacao;
import br.com.pokemon.autenticacao.FalhaAutenticacaoException;
import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.AutenticacaoProvider;
import br.com.pokemon.interfaces.Provider;
import br.com.pokemon.user.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.CREDENCIAIS_INVALIDAS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisualizarCartasFacadeTest {

    private static final User ASH = new User(1, "Ash", 1);

    private final AutenticacaoProvider aceitaAsh = (email, senha) ->
            "ash@pokemon.com".equals(email) && "pikachu".equals(senha)
                    ? Optional.of(ASH)
                    : Optional.empty();

    private static Carta carta(int id, String nome) {
        return new Carta(id, 0, nome, "", 0f, 0f, null, null, null);
    }

    private VisualizarCartasFacade facade(Provider<List<Carta>, Integer> distribuicao,
                                          Provider<Carta, String> pokeApi) {
        return new VisualizarCartasFacade(new Autenticacao(aceitaAsh), distribuicao, pokeApi);
    }

    @Test
    void iniciarSessaoDelegaParaAutenticacao() {
        VisualizarCartasFacade facade = facade(id -> List.of(), chave -> null);

        assertSame(ASH, facade.iniciarSessao("ash@pokemon.com", "pikachu"));

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> facade.iniciarSessao("ash@pokemon.com", "errada"));
        assertEquals(CREDENCIAIS_INVALIDAS, erro.getMotivo());
    }

    @Test
    void listarCartasOrdenaPeloNomeQuandoSolicitado() throws Exception {
        AtomicReference<Integer> usuarioConsultado = new AtomicReference<>();
        VisualizarCartasFacade facade = facade(id -> {
            usuarioConsultado.set(id);
            return List.of(carta(25, "pikachu"), carta(1, "bulbasaur"), carta(4, "charmander"));
        }, chave -> null);

        List<Carta> cartas = facade.listarCartas(7, true);

        assertEquals(7, usuarioConsultado.get());
        assertEquals(List.of("bulbasaur", "charmander", "pikachu"),
                cartas.stream().map(Carta::getNome).toList());
    }

    @Test
    void listarCartasPreservaAOrdemQuandoNaoSolicitado() throws Exception {
        VisualizarCartasFacade facade = facade(
                id -> List.of(carta(25, "pikachu"), carta(1, "bulbasaur")), chave -> null);

        assertEquals(List.of("pikachu", "bulbasaur"),
                facade.listarCartas(1, false).stream().map(Carta::getNome).toList());
    }

    @Test
    void listarCartasNaoAlteraAListaDoProvedor() throws Exception {
        List<Carta> original = new ArrayList<>(List.of(carta(25, "pikachu"), carta(1, "bulbasaur")));
        VisualizarCartasFacade facade = facade(id -> original, chave -> null);

        List<Carta> resultado = facade.listarCartas(1, true);

        assertNotSame(original, resultado);
        assertEquals("pikachu", original.get(0).getNome());
    }

    @Test
    void listarCartasTratalistaNulaComoVazia() throws Exception {
        VisualizarCartasFacade facade = facade(id -> null, chave -> null);

        assertTrue(facade.listarCartas(1, true).isEmpty());
    }

    @Test
    void listarCartasPropagaFalhaDoProvedor() {
        VisualizarCartasFacade facade = facade(id -> {
            throw new IOException("distribuição fora do ar");
        }, chave -> null);

        assertThrows(IOException.class, () -> facade.listarCartas(1, true));
    }

    @Test
    void detalharCartaConsultaAPokeApiPeloId() throws Exception {
        AtomicReference<String> chaveConsultada = new AtomicReference<>();
        Carta pikachu = carta(25, "pikachu");
        VisualizarCartasFacade facade = facade(id -> List.of(), chave -> {
            chaveConsultada.set(chave);
            return pikachu;
        });

        assertSame(pikachu, facade.detalharCarta(25));
        assertEquals("25", chaveConsultada.get());
    }
}
