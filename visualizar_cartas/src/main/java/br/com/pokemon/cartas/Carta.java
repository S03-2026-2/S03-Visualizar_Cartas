package br.com.pokemon.cartas;

import java.util.Objects;

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

    private Carta(int id, int experience, String nome, String urlCry, float altura, float peso, Habilidade[] habilidades, Tipo[] tipos, Sprite[] sprites) {
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

    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {

        private int id;
        private int experience;
        private String nome;
        private String urlCry = "";
        private float altura;
        private float peso;
        private Habilidade[] habilidades = new Habilidade[0];
        private Tipo[] tipos = new Tipo[0];
        private Sprite[] sprites = new Sprite[0];

        private Builder() {
        }

        public Builder id(int id) { this.id = id; return this; }
        public Builder experience(int experience) { this.experience = experience; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder urlCry(String urlCry) {
            this.urlCry = Objects.requireNonNullElse(urlCry, "");
            return this;
        }

        public Builder altura(float altura) { this.altura = altura; return this; }

        public Builder peso(float peso) { this.peso = peso; return this; }

        public Builder habilidades(Habilidade[] habilidades) {
            this.habilidades = Objects.requireNonNullElse(habilidades, new Habilidade[0]);
            return this;
        }

        public Builder tipos(Tipo[] tipos) {
            this.tipos = Objects.requireNonNullElse(tipos, new Tipo[0]);
            return this;
        }

        public Builder sprites(Sprite[] sprites) {
            this.sprites = Objects.requireNonNullElse(sprites, new Sprite[0]);
            return this;
        }

        public Carta build() {
            if (nome == null || nome.isBlank()) {
                throw new IllegalStateException("A carta precisa de um nome.");
            }
            if (tipos.length < 1 || tipos.length > 2) {
                throw new IllegalStateException("A carta deve ter um ou dois tipos.");
            }
            return new Carta(id, experience, nome, urlCry, altura, peso,
                    habilidades, tipos, sprites);
        }
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
