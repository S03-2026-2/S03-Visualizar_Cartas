package br.com.pokemon.controller;

import br.com.pokemon.interfaces.AutenticacaoProvider;
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
 * A autenticação é realizada pelo sistema externo de Jogadores. A chamada ao
 * endpoint desse sistema será implementada quando o contrato estiver disponível.
 */
@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticacaoProvider autenticacaoProvider;

    public AutenticacaoController(AutenticacaoProvider autenticacaoProvider) {
        this.autenticacaoProvider = autenticacaoProvider;
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
        return autenticacaoProvider.autenticar(email, senha)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas"
                ));
    }
}
