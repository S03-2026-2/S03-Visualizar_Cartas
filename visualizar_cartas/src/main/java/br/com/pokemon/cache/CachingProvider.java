package br.com.pokemon.cache;

import br.com.pokemon.interfaces.Provider;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachingProvider<T, K> implements Provider<T, K> {

    private final Provider<T, K> delegate;
    private final Map<K, T> cache = new ConcurrentHashMap<>();

    public CachingProvider(Provider<T, K> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get(K chave) throws IOException, InterruptedException {
        T emCache = cache.get(chave);
        if (emCache != null) {
            return emCache;
        }

        T valor = delegate.get(chave);
        if (valor != null) {
            cache.put(chave, valor);
        }
        return valor;
    }
}