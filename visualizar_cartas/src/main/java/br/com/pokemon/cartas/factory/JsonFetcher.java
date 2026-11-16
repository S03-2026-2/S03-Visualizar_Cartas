package br.com.pokemon.cartas.factory;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Busca um JSON em uma URL. Usado pelas fábricas que precisam de requisições
 * adicionais para montar a carta (tipos e habilidades).
 */
@FunctionalInterface
public interface JsonFetcher {

    JSONObject buscar(String url) throws IOException, InterruptedException;
}
