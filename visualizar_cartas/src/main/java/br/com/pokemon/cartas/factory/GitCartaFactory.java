package br.com.pokemon.cartas.factory;

import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Tipo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica para o espelho da PokéAPI no GitHub: tipos e habilidades vêm dos
 * campos embutidos no JSON do Pokémon, sem requisições adicionais. A geração
 * da habilidade fica vazia.
 */
public class GitCartaFactory extends CartaFactory {

    @Override
    protected Tipo[] criarTipos(JSONObject pokemonJson) {
        if (!pokemonJson.has("types")) {
            return new Tipo[0];
        }

        JSONArray typesArray = pokemonJson.getJSONArray("types");
        List<Tipo> tipos = new ArrayList<>();
        for (int i = 0; i < typesArray.length(); i++) {
            JSONObject type = typesArray.getJSONObject(i).optJSONObject("type");
            if (type == null) {
                continue;
            }

            String url = type.optString("url", "");
            Tipo tipo = new Tipo(extrairId(url), type.optString("name", ""));
            tipo.setUrl(url);
            tipos.add(tipo);
        }

        return tipos.toArray(new Tipo[0]);
    }

    @Override
    protected Habilidade[] criarHabilidades(JSONObject pokemonJson) {
        if (!pokemonJson.has("abilities")) {
            return new Habilidade[0];
        }

        JSONArray abilitiesArray = pokemonJson.getJSONArray("abilities");
        List<Habilidade> habilidades = new ArrayList<>();
        for (int i = 0; i < abilitiesArray.length(); i++) {
            JSONObject ability = abilitiesArray.getJSONObject(i).optJSONObject("ability");
            if (ability == null) {
                continue;
            }

            String url = ability.optString("url", "");
            habilidades.add(new Habilidade(extrairId(url), ability.optString("name", ""), ""));
        }

        return habilidades.toArray(new Habilidade[0]);
    }
}
