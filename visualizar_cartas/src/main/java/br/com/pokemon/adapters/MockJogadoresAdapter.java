package br.com.pokemon.adapters;

import br.com.pokemon.interfaces.AutenticacaoProvider;
import br.com.pokemon.mocks.MockJsonLoader;
import br.com.pokemon.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

/**
 * Autenticação de demonstração baseada em {@code mocks/usuarios.json}.
 */
@Profile("mock")
@Component
public class MockJogadoresAdapter implements AutenticacaoProvider {

    private static final TypeReference<List<UsuarioMock>> USUARIOS =
            new TypeReference<>() {
            };

    private final MockJsonLoader loader;

    public MockJogadoresAdapter(MockJsonLoader loader) {
        this.loader = loader;
    }

    @Override
    public Optional<User> autenticar(String email, String senha) {
        List<UsuarioMock> usuarios;
        try {
            usuarios = loader.read("mocks/usuarios.json", USUARIOS);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler usuários de demonstração.", e);
        }

        return usuarios.stream()
                .filter(usuario -> usuario.email().equals(email) && usuario.senha().equals(senha))
                .findFirst()
                .map(usuario -> new User(usuario.id(), usuario.nome(), usuario.ranking()));
    }

    private record UsuarioMock(int id, String nome, String email, String senha, int ranking) {
    }
}
