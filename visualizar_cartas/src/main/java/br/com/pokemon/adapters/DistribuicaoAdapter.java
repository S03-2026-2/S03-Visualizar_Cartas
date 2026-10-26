package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;

import java.util.List;

public class DistribuicaoAdapter implements Provider {

    @Override
    public void get() {
        // Lógica para buscar dados do serviço de distribuição de cartas
    }

    public List<Carta> listarCartas(List<Carta> cartasUsuario) {
        // Lógica para listar as cartas do usuário a partir do serviço de distribuição
        return cartasUsuario;
    }

    public Carta detalharCartas(int id) {
        // Lógica para detalhar uma carta específica
        return null;
    }
}

