package br.com.pokemon.interfaces;

import java.io.IOException;

/**
 * Contrato genérico para provedores de dados externos.
 *
 * @param <T> tipo do dado retornado pelo provedor
 * @param <K> tipo da chave utilizada na consulta
 */
public interface Provider<T, K> {

    T get(K chave) throws IOException, InterruptedException;
}
