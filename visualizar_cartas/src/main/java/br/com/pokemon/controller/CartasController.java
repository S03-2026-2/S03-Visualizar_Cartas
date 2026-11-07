package br.com.pokemon.controller;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.interfaces.Provider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller responsável pelos casos de uso de visualização de cartas.
 *
 * A lista de cartas do usuário é fornecida pelo sistema externo de distribuição
 * de cartas. A consulta ao endpoint desse sistema será implementada quando o
 * contrato estiver disponível.
 */
@RestController
@RequestMapping("/cartas")
public class CartasController {

    private final Provider<List<Carta>, Integer> distribuicaoProvider;
    private final Provider<Carta, String> pokeApiProvider;

    public CartasController(Provider<List<Carta>, Integer> distribuicaoProvider,
                            Provider<Carta, String> pokeApiProvider) {
        this.distribuicaoProvider = distribuicaoProvider;
        this.pokeApiProvider = pokeApiProvider;
    }

    /**
     * Lista as cartas do usuário, ordenadas pelo nome quando solicitado.
     *
     * @param usuarioId identificador do usuário
     * @param ordenar   indica se as cartas devem ser ordenadas pelo nome
     * @return cartas do usuário
     */
    @GetMapping
    public List<Carta> listarCartas(@RequestParam int usuarioId,
                                    @RequestParam(defaultValue = "true") boolean ordenar)
            throws IOException, InterruptedException {
        List<Carta> cartasUsuario = new ArrayList<>(distribuicaoProvider.get(usuarioId));

        if (ordenar) {
            ordenarCartas(cartasUsuario);
        }

        return cartasUsuario;
    }

    /**
     * Detalha uma carta consultando as informações na PokéAPI.
     *
     * @param id identificador da carta na PokéAPI
     * @return informações detalhadas da carta
     */
    @GetMapping("/{id}")
    public Carta detalharCartas(@PathVariable int id) throws IOException, InterruptedException {
        return pokeApiProvider.get(String.valueOf(id));
    }

    private void ordenarCartas(List<Carta> cartas) {
        if (cartas != null) {
            cartas.sort(Comparator.comparing(Carta::getNome));
        }
    }
}
