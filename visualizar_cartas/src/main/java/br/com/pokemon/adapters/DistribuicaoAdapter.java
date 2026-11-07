package br.com.pokemon.adapters;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DistribuicaoAdapter implements Provider<List<Carta>, Integer> {

    @Override
    public List<Carta> get(Integer usuarioId) throws IOException, InterruptedException {
        // TODO: consultar o endpoint do sistema de distribuição de cartas quando
        // disponível para obter as cartas do usuário informado.
        // Retorna uma lista vazia enquanto o contrato do serviço externo não existe.
        return new ArrayList<>();
    }
}
