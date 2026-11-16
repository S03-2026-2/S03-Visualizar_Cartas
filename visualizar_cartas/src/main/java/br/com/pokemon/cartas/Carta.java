package br.com.pokemon.cartas;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Carta {

    private int id;
    private int experience;
    private String nome;
    private String urlCry;
    private float altura;
    private float peso;
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

    public Carta() {
    }

    public int getId() {
        return id;
    }

    public int getExperience() {
        return experience;
    }

    public String getNome() {
        return nome;
    }

    public String getUrlCry() {
        return urlCry;
    }

    public float getAltura() {
        return altura;
    }

    public float getPeso() {
        return peso;
    }

    public Habilidade[] getHabilidades() {
        return habilidades;
    }

    public Tipo[] getTipos() {
        return tipos;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    @JsonIgnore
    public Carta getCarta() {
        return this;
    }
}
