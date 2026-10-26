package br.com.pokemon.cartas;

public class Carta{

    private int id, experience;
    private String nome, urlCry;
    private float altura, peso;
    private Habilidade[] habilidades;
    private Tipo[] tipos;
    private Sprite[] sprites;

    public static Carta getCarta(int Id){

        Carta carta = new Carta();
        carta.id = this.id
        carta.nome = this.nome;
        carta.urlCry = this.urlCry;
        carta.altura = this.altura;
        carta.peso = this.peso;
        carta.habilidades = this.habilidades;
        carta.tipos = this.tipos;
        carta.sprites = this.sprites;
        return carta;
    }
}
