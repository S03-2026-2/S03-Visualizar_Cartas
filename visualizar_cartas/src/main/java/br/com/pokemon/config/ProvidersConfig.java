package br.com.pokemon.config;

import br.com.pokemon.adapters.PokeAPIAdapter;
import br.com.pokemon.adapters.PokeApiGitAdapter;
import br.com.pokemon.cache.CachingProvider;
import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProvidersConfig {

    @Bean
    @Profile("mock")
    Provider<Carta, String> cartasProviderMock(
            @Value("${app.pokeapi.git.base-url}") String baseUrl) {
        return new CachingProvider<>(new PokeApiGitAdapter(baseUrl));
    }

    @Bean
    @Profile("real")
    Provider<Carta, String> cartasProviderReal() {
        return new CachingProvider<>(new PokeAPIAdapter());
    }
}