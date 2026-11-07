package br.com.pokemon.controller;

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
@RequestMapping("/autenticacao")
public class AutenticacaoController {

    /**
     * Autentica um usuário a partir do e-mail e da senha informados.
     *
     * @param email e-mail do usuário
     * @param senha senha do usuário
     * @return {@code true} quando as credenciais forem válidas; {@code false}
     *         caso contrário
     */
    @PostMapping("/login")
    public boolean autenticar(@RequestParam String email, @RequestParam String senha) {
        // TODO: consultar o endpoint do sistema de Jogadores quando disponível.
        // O contrato (URL, método HTTP e formato da resposta) ainda não foi definido.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "Serviço externo de autenticação ainda não disponível."
        );
    }
}
