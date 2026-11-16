package br.com.pokemon.autenticacao;

/**
 * Falha no caso de uso "Autenticar", com o motivo que define a resposta HTTP.
 */
public class FalhaAutenticacaoException extends RuntimeException {

    public enum Motivo {
        CREDENCIAIS_INVALIDAS("Credenciais inválidas"),
        RESPOSTA_INVALIDA("Resposta inválida do serviço de Jogadores"),
        SERVICO_INDISPONIVEL("Serviço de Jogadores indisponível");

        private final String mensagem;

        Motivo(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    private final Motivo motivo;

    public FalhaAutenticacaoException(Motivo motivo) {
        super(motivo.getMensagem());
        this.motivo = motivo;
    }

    public FalhaAutenticacaoException(Motivo motivo, Throwable causa) {
        super(motivo.getMensagem(), causa);
        this.motivo = motivo;
    }

    public Motivo getMotivo() {
        return motivo;
    }
}
