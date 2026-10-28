package br.com.pokemon.cartas;

public class Habilidade {

    private int id;
    private String nome;
    private String geracao;

    public Habilidade() {
    }

    public Habilidade(int id, String nome, String geracao) {
        this.id = id;
        this.nome = nome;
        this.geracao = geracao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGeracao() {
        return geracao;
    }

    public void setGeracao(String geracao) {
        this.geracao = geracao;
    }

    public Habilidade getHabilidade() {
        return this;
    }
}
