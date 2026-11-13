package br.com.pokemon.interfaces;

import br.com.pokemon.user.User;

import java.util.Optional;

/**
 * Porta para a autenticação de jogadores.
 */
public interface AutenticacaoProvider {

    Optional<User> autenticar(String email, String senha);
}
