package br.com.pokemon.cartas;

public class Carta{

    private int id, experience;
    private String nome, urlCry;
    private float altura, peso;

    public static Carta getCarta(int Id){

        Carta carta = new Carta();
        carta.id = id;
        
        return carta;
    }

    public int getId(){

        return this.id;
    }

    public int getExperience(){

        return this.experience
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
}
