package br.com.pokemon.autenticacao;

import br.com.pokemon.interfaces.IAutenticacao;
import java.util.Objects;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.CREDENCIAIS_INVALIDAS;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.RESPOSTA_INVALIDA;

/**
 * Coordena o caso de uso de autenticação, sem armazenar credenciais ou sessões.
 * A comunicação externa pertence à implementação de IAutenticacao.
 */
public final class Autenticacao {

    private final IAutenticacao servicoJogadores;

    public Autenticacao(IAutenticacao servicoJogadores) {
        this.servicoJogadores = Objects.requireNonNull(
                servicoJogadores, "O serviço de autenticação é obrigatório.");
    }

    /**
     * Mantém a assinatura booleana prevista no diagrama do visualizador.
     * Falhas de comunicação ou resposta inválida são propagadas, não viram false.
     * Use iniciarSessao quando o chamador precisar também dos dados da sessão.
     */
    public boolean autenticar(String email, String senha) {
        try {
            iniciarSessao(email, senha);
            return true;
        } catch (FalhaAutenticacaoException erro) {
            if (erro.getMotivo() == CREDENCIAIS_INVALIDAS) {
                return false;
            }
            throw erro;
        }
    }

    /**
     * Executa uma única autenticação e devolve a sessão para o contexto do usuário.
     * Não chame autenticar antes deste método: isso duplicaria a consulta.
     */
    public Sessao iniciarSessao(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new FalhaAutenticacaoException(CREDENCIAIS_INVALIDAS);
        }

        // Preserva a senha exatamente como recebida, inclusive espaços.
        Sessao sessao = servicoJogadores.autenticar(email.trim(), senha);
        if (sessao == null) {
            throw new FalhaAutenticacaoException(RESPOSTA_INVALIDA);
        }

        return sessao;
    }
}
