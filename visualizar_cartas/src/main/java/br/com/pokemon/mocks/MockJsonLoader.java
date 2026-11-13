package br.com.pokemon.mocks;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Lê arquivos JSON de demonstração empacotados no classpath.
 */
@Component
public class MockJsonLoader {

    private final ObjectMapper mapper;

    public MockJsonLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T read(String path, TypeReference<T> type) throws IOException {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return mapper.readValue(input, type);
        }
    }
}
