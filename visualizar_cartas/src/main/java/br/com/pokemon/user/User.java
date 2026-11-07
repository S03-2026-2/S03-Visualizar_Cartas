package br.com.pokemon.user;

import br.com.pokemon.cartas.Carta;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String nome;
    private int ranking;
    private List<Carta> cartas = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getRanking() {
        return ranking;
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public Carta getCarta(int index) {
        if (cartas != null && !cartas.isEmpty() && index >= 0 && index < cartas.size()) {
            return cartas.get(index);
        }
        return null;
    }
}
