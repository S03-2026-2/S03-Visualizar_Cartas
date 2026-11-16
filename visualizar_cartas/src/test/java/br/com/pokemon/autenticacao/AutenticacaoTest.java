package br.com.pokemon.autenticacao;

import br.com.pokemon.interfaces.AutenticacaoProvider;
import br.com.pokemon.user.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.CREDENCIAIS_INVALIDAS;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.RESPOSTA_INVALIDA;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.SERVICO_INDISPONIVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutenticacaoTest {

    private static final User ASH = new User(1, "Ash", 1);

    private final AutenticacaoProvider aceitaAsh = (email, senha) ->
            "ash@pokemon.com".equals(email) && "pikachu".equals(senha)
                    ? Optional.of(ASH)
                    : Optional.empty();

    @Test
    void credenciaisValidasDevolvemUsuario() {
        Autenticacao autenticacao = new Autenticacao(aceitaAsh);

        assertSame(ASH, autenticacao.iniciarSessao("ash@pokemon.com", "pikachu"));
        assertTrue(autenticacao.autenticar("ash@pokemon.com", "pikachu"));
    }

    @Test
    void emailComEspacosEhNormalizadoMasSenhaNao() {
        Autenticacao autenticacao = new Autenticacao(aceitaAsh);

        assertTrue(autenticacao.autenticar("  ash@pokemon.com ", "pikachu"));
        assertFalse(autenticacao.autenticar("ash@pokemon.com", " pikachu"));
    }

    @Test
    void credenciaisInvalidasViramFalseOuExcecao() {
        Autenticacao autenticacao = new Autenticacao(aceitaAsh);

        assertFalse(autenticacao.autenticar("ash@pokemon.com", "errada"));
        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> autenticacao.iniciarSessao("ash@pokemon.com", "errada"));
        assertEquals(CREDENCIAIS_INVALIDAS, erro.getMotivo());
    }

    @Test
    void entradaVaziaNaoConsultaOServico() {
        AtomicInteger chamadas = new AtomicInteger();
        Autenticacao autenticacao = new Autenticacao((email, senha) -> {
            chamadas.incrementAndGet();
            return Optional.of(ASH);
        });

        assertFalse(autenticacao.autenticar(null, "pikachu"));
        assertFalse(autenticacao.autenticar("   ", "pikachu"));
        assertFalse(autenticacao.autenticar("ash@pokemon.com", null));
        assertFalse(autenticacao.autenticar("ash@pokemon.com", ""));
        assertEquals(0, chamadas.get());
    }

    @Test
    void falhaDoServicoEhPropagadaNoAutenticar() {
        Autenticacao autenticacao = new Autenticacao((email, senha) -> {
            throw new FalhaAutenticacaoException(SERVICO_INDISPONIVEL);
        });

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> autenticacao.autenticar("ash@pokemon.com", "pikachu"));
        assertEquals(SERVICO_INDISPONIVEL, erro.getMotivo());
    }

    @Test
    void respostaNulaDoServicoEhInvalida() {
        Autenticacao autenticacao = new Autenticacao((email, senha) -> null);

        FalhaAutenticacaoException erro = assertThrows(FalhaAutenticacaoException.class,
                () -> autenticacao.autenticar("ash@pokemon.com", "pikachu"));
        assertEquals(RESPOSTA_INVALIDA, erro.getMotivo());
    }
}
