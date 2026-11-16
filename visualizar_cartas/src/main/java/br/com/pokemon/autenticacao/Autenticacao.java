package br.com.pokemon.autenticacao;

import br.com.pokemon.interfaces.AutenticacaoProvider;
import br.com.pokemon.user.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.CREDENCIAIS_INVALIDAS;
import static br.com.pokemon.autenticacao.FalhaAutenticacaoException.Motivo.RESPOSTA_INVALIDA;

/**
 * Coordena o caso de uso de autenticação, sem armazenar credenciais ou sessões.
 * A verificação das credenciais pertence ao sistema de Jogadores, acessado pela
 * implementação de {@link AutenticacaoProvider} do profile ativo.
 */
@Service
public class Autenticacao {

    private final AutenticacaoProvider servicoJogadores;

    public Autenticacao(AutenticacaoProvider servicoJogadores) {
        this.servicoJogadores = Objects.requireNonNull(
                servicoJogadores, "O serviço de autenticação é obrigatório.");
    }

    /**
     * Mantém a assinatura booleana prevista no diagrama do visualizador.
     * Falhas de comunicação ou resposta inválida são propagadas, não viram false.
     * Use iniciarSessao quando o chamador precisar também dos dados do usuário.
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
     * Executa uma única autenticação e devolve o usuário autenticado.
     * Não chame autenticar antes deste método: isso duplicaria a consulta.
     *
     * @throws FalhaAutenticacaoException com o motivo da falha
     */
    public User iniciarSessao(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new FalhaAutenticacaoException(CREDENCIAIS_INVALIDAS);
        }

        // Preserva a senha exatamente como recebida, inclusive espaços.
        Optional<User> usuario = servicoJogadores.autenticar(email.trim(), senha);
        if (usuario == null) {
            throw new FalhaAutenticacaoException(RESPOSTA_INVALIDA);
        }

        return usuario.orElseThrow(
                () -> new FalhaAutenticacaoException(CREDENCIAIS_INVALIDAS));
    }
}
