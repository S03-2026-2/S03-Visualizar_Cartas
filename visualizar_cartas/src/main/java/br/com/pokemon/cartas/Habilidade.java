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

    public Habilidade getHabilidade(int id){
        Habilidade habilidade = new Habilidade(this.id, this.nome, this.geracao);
        return habilidade;
    }

}
