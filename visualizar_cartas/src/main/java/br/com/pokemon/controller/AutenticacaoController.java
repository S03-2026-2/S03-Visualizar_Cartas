package br.com.pokemon.controller;

import br.com.pokemon.autenticacao.FalhaAutenticacaoException;
import br.com.pokemon.facade.VisualizarCartasFacade;
import br.com.pokemon.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller responsável pelo caso de uso "Autenticar".
 *
 * A autenticação é delegada à {@link VisualizarCartasFacade}, que a realiza
 * junto ao sistema externo de Jogadores.
 */
@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final VisualizarCartasFacade facade;

    public AutenticacaoController(VisualizarCartasFacade facade) {
        this.facade = facade;
    }

    /**
     * Autentica um usuário a partir do e-mail e da senha informados.
     *
     * @param email e-mail do usuário
     * @param senha senha do usuário
     * @return usuário autenticado
     */
    @PostMapping("/login")
    public User autenticar(@RequestParam String email, @RequestParam String senha) {
        try {
            return facade.iniciarSessao(email, senha);
        } catch (FalhaAutenticacaoException erro) {
            throw new ResponseStatusException(status(erro.getMotivo()), erro.getMessage(), erro);
        }
    }

    private static HttpStatus status(FalhaAutenticacaoException.Motivo motivo) {
        return switch (motivo) {
            case CREDENCIAIS_INVALIDAS -> HttpStatus.UNAUTHORIZED;
            case RESPOSTA_INVALIDA, SERVICO_INDISPONIVEL -> HttpStatus.BAD_GATEWAY;
        };
    }
}
