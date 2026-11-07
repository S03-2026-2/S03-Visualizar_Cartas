package br.com.pokemon.cartas;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Sprite {

    private String nome;
    private String url;

    public Sprite() {
    }

    public Sprite(String nome, String url) {
        this.nome = nome;
        this.url = url;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonIgnore
    public Sprite getSprite() {
        return this;
    }
}
