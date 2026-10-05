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
        carta.id = Id;
        return carta;
    }

    public int getId(){

        return this.id;
    }

    public int getExperience(){

        return this.experience;
    }

    public String getNome(){

        return this.nome;
    }

    public String getUrlCry(){

        return this.urlCry;
    }    

    public float getAltura(){

        return this.altura;
    }

    public float getPeso(){

        return this.peso;
    }
    public Habilidade[] getHabilidade(){

        return this.habilidades;
    }
    public Tipo[] getTipo(){

        return this.tipos;
    }
    public Sprite[] getSprite(){

        return this.sprites;
    }
}
