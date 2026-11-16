package br.com.pokemon.cartas.factory;

import br.com.pokemon.cartas.Habilidade;
import br.com.pokemon.cartas.Tipo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica para a PokéAPI real: consulta cada tipo e cada habilidade em
 * requisições adicionais para obter o nome e a geração.
 */
public class PokeApiCartaFactory extends CartaFactory {

    private final JsonFetcher fetcher;

    public PokeApiCartaFactory(JsonFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    protected Tipo[] criarTipos(JSONObject pokemonJson) throws IOException, InterruptedException {
        if (!pokemonJson.has("types")) {
            return new Tipo[0];
        }

        JSONArray typesArray = pokemonJson.getJSONArray("types");
        if (typesArray.length() < 1 || typesArray.length() > 2) {
            throw new IllegalArgumentException("A carta deve ter um ou dois tipos.");
        }

        List<Tipo> tipos = new ArrayList<>();
        for (int i = 0; i < typesArray.length(); i++) {
            JSONObject typeEntry = typesArray.getJSONObject(i);
            int slot = typeEntry.getInt("slot");
            if (slot < 1 || slot > 2) {
                throw new IllegalArgumentException("Slot de tipo inválido ou repetido.");
            }

            JSONObject type = typeEntry.getJSONObject("type");
            String url = type.getString("url");
            JSONObject typeJson = fetcher.buscar(url);

            int typeId = extrairId(url);
            String nome = typeJson.optString("name", type.optString("name", ""));
            Tipo t = new Tipo(typeId, nome);
            t.setUrl(url);
            tipos.add(t);
        }

        return tipos.toArray(new Tipo[0]);
    }

    @Override
    protected Habilidade[] criarHabilidades(JSONObject pokemonJson) throws IOException, InterruptedException {
        if (!pokemonJson.has("abilities")) {
            return new Habilidade[0];
        }

        JSONArray abilitiesArray = pokemonJson.getJSONArray("abilities");
        List<Habilidade> habilidades = new ArrayList<>();

        for (int i = 0; i < abilitiesArray.length(); i++) {
            JSONObject abilityEntry = abilitiesArray.getJSONObject(i);
            if (abilityEntry.has("ability")) {
                JSONObject ability = abilityEntry.getJSONObject("ability");
                String url = ability.getString("url");
                JSONObject abilityJson = fetcher.buscar(url);

                int abilityId = extrairId(url);
                String nome = abilityJson.optString("name", ability.optString("name", ""));
                String geracao = "";
                if (abilityJson.has("generation")) {
                    JSONObject generation = abilityJson.optJSONObject("generation");
                    if (generation != null) {
                        geracao = generation.optString("name", "");
                    }
                }

                habilidades.add(new Habilidade(abilityId, nome, geracao));
            }
        }

        return habilidades.toArray(new Habilidade[0]);
    }
}
