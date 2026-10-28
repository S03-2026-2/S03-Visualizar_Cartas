package br.com.pokemon.cartas;

public class Tipo {

    private int id;
    private String nome;
    private String url;

    public Tipo() {
    }

    public Tipo(int id, String nome) {
        this.id = id;
        this.nome = nome;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Tipo getTipo() {
        return this;
    }
}

