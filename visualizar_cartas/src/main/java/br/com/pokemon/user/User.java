package br.com.pokemon.user;

import br.com.pokemon.cartas.Carta;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String nome;
    private int ranking;
    private List<Carta> Cartas = new ArrayList<Carta>();

    public List<Carta> getCartas() {
        return Cartas;
    }
}
