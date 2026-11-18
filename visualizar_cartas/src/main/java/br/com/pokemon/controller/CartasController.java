package br.com.pokemon.controller;

import br.com.pokemon.cartas.Carta;
import br.com.pokemon.facade.VisualizarCartasFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Controller responsável pelos casos de uso de visualização de cartas.
 *
 * Apenas traduz HTTP: toda a orquestração é delegada à
 * {@link VisualizarCartasFacade}.
 */
@RestController
@RequestMapping("/api/cartas")
public class CartasController {

    private final VisualizarCartasFacade facade;

    public CartasController(VisualizarCartasFacade facade) {
        this.facade = facade;
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
        return facade.listarCartas(usuarioId, ordenar);
    }

    /**
     * Detalha uma carta consultando as informações na PokéAPI.
     *
     * @param id identificador da carta na PokéAPI
     * @return informações detalhadas da carta
     */
    @GetMapping("/{id}")
    public Carta detalharCartas(@PathVariable int id) throws IOException, InterruptedException {
        return facade.detalharCarta(id);
    }
}
