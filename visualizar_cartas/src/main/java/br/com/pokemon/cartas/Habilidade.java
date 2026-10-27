package br.com.pokemon.cartas;

public class Habilidade {

    private int id;
    private String nome;
    private String geracao;

    public Habilidade(int id, String nome, String geracao) {
        this.id = id;
        this.nome = nome;
        this.geracao = geracao;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getGeracao() {
        return geracao;
    }
}
