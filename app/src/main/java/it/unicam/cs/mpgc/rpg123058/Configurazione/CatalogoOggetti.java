package it.unicam.cs.mpgc.rpg123058.Configurazione;

import it.unicam.cs.mpgc.rpg123058.Dominio.Arma;
import it.unicam.cs.mpgc.rpg123058.Dominio.Armatura;
import it.unicam.cs.mpgc.rpg123058.Dominio.ConsumabileCurativo;
import it.unicam.cs.mpgc.rpg123058.Dominio.Oggetto;

import java.util.HashMap;
import java.util.Map;

public class CatalogoOggetti {

    /**
     * Tramite un mappa associa gli ID JSON a oggetti Consumabile/Arma/Armatura del Modello di Dominio java
     *
     * @return la mappa con tutti gli Oggetti associati ai loro ID
     */
    public Map<String, Oggetto> creaCatalogo() {
        Map<String, Oggetto> oggetti = new HashMap<>();

        oggetti.put(
                "acqua",
                new ConsumabileCurativo("Acqua", 15)
        );

        oggetti.put(
                "pane",
                new ConsumabileCurativo("Pane", 30)
        );

        oggetti.put(
                "ascia_nanica_danneggiata",
                new Arma("Ascia nanica danneggiata", 3)
        );

        oggetti.put(
                "spada_nanica",
                new Arma("Spada nanica", 5)
        );

        oggetti.put(
                "martello_guerra_nanico",
                new Arma("Martello da guerra nanico", 8)
        );

        oggetti.put(
                "scudo_nanico",
                new Armatura("Scudo nanico", 2)
        );

        oggetti.put(
                "corazza_maglia_nanica",
                new Armatura("Corazza di maglia nanica", 4)
        );

        return Map.copyOf(oggetti);
    }
}
