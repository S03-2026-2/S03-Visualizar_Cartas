package br.com.pokemon.cartas;

import java.util.Comparator;
import java.util.List;

public class VisualizarCartas {

    private void listarCartas(List<Carta> cartasUsuario) {
        // Lógica para listar cartas
    }

    private Carta detalharCartas(int id) {
        // Lógica para detalhar carta
        return null;
    }

    private void ordenarCartas(List<Carta> cartas) {
        if (cartas != null) {
            cartas.sort(Comparator.comparing(Carta::getNome));
        }
        listarCartas(cartas);
    }
}
