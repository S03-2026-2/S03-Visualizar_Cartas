package br.com.pokemon.cartas;

public class Carta{

    private int id, experience;
    private String nome, urlCry;
    private float altura, peso;
    private Habilidade[] habilidades;
    private Tipo[] tipos;
    private Sprite[] sprites;

    public Carta(int id, int experience, String nome, String urlCry, float altura, float peso, Habilidade[] habilidades, Tipo[] tipos, Sprite[] sprites) {
        this.id = id;
        this.experience = experience;
        this.nome = nome;
        this.urlCry = urlCry;
        this.altura = altura;
        this.peso = peso;
        this.habilidades = habilidades;
        this.tipos = tipos;
        this.sprites = sprites;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
