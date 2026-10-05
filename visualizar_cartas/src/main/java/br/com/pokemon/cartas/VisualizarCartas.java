package br.com.pokemon.cartas;

import br.com.pokemon.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VisualizarCartas {

    private static void listarCartas(List<Carta> cartasUsuario) {

        for(int i=0; i<cartasUsuario.size(); i++){
            // Lógica para mostrar cada carta
            // da lista utilizando o Vaadin
        }
    }

    private static void detalharCartas(int id) {

        // Lógica para mostrar uma carta detalhada
        // usando o framework vaadin

    }

    private static void ordenarCartas(List<Carta> cartasUsuario) {
        cartasUsuario.sort(Comparator.comparing(Carta::getNome));

        listarCartas(cartasUsuario);
    }

    private static Carta buscarCartasPokeAPI(int id) {
        return null;
    }
}
