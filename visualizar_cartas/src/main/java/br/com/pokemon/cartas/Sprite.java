package br.com.pokemon.cartas;

public class Sprite {

    private final String nome;
    private final String url;

    public Sprite(String nome, String url) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                "O nome do sprite é obrigatório."
            );
        }

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                "A URL do sprite é obrigatória."
            );
        }

        this.nome = nome.trim();
        this.url = url.trim();
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }
}