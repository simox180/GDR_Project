package it.unicam.cs.mpgc.rpg123058.Configurazione;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDanno;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurni;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.Oggetto;
import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class CatalogoIncontri {

    /**
     * Costruisce una mappa associando ogni ID di un incontro a una funzione capace di creare quel combattimento.
     * Eseguendo questo metodo però la funzione che crea gli incontri non viene eseguita, verrà eseguita
     * successivamente in Partita con eseguiCombattimento() con .apply
     *
     * @param oggetti assegnati come bottino
     * @param creaGestore funzione che crea un gestoreTurni per ogni incontro
     * @param calcolatoreDanno usato per gli incontri
     * @param strategiaNemico il tipo di strategia adottata dai nemici (in questo caso è unica)
     * @return la mappa con gli ID degli incontri e la funzione che li crea con Giocatore e i nemici
     */
    public Map<String, Function<Giocatore, Combattimento>> creaCatalogo(
            Map<String, Oggetto> oggetti,
            Function<List<Personaggio>, GestoreTurni> creaGestore,
            CalcolatoreDanno calcolatoreDanno,
            StrategiaNemico strategiaNemico
    ) {
        if(oggetti == null || creaGestore == null || calcolatoreDanno == null || strategiaNemico == null) throw new NullPointerException("Parametri nulli non ammessi");

        Oggetto spada = Objects.requireNonNull(oggetti.get("spada_nanica"), "Spada nanica assente dal catalogo");

        Oggetto martello = Objects.requireNonNull(oggetti.get("martello_guerra_nanico"), "Martello nanico assente dal catalogo");

        Map<String, Function<Giocatore, Combattimento>> incontri = new HashMap<>();

        //inserimento/creazione dei 3 incontri nella mappa
        incontri.put("orchi_galleria", giocatore -> {

            Nemico esploratore = new Nemico("Orco esploratore", 20, 6, 1, null);

            Nemico guerriero = new Nemico("Orco guerriero", 25, 7, 2, spada);

            return new Combattimento(giocatore, List.of(esploratore, guerriero), creaGestore, calcolatoreDanno, strategiaNemico);
        });

        incontri.put("orchi_inseguitori", giocatore -> {
            Nemico inseguitore = new Nemico(
                    "Orco inseguitore", 28, 8, 2, null);

            Nemico veterano = new Nemico(
                    "Orco veterano", 35, 9, 3, martello);

            return new Combattimento(
                    giocatore,
                    List.of(inseguitore, veterano),
                    creaGestore,
                    calcolatoreDanno,
                    strategiaNemico
            );
        });

        incontri.put("capitano_orchi", giocatore -> {
            Nemico capitano = new Nemico(
                    "Capitano degli Orchi", 60, 12, 4, null);

            return new Combattimento(
                    giocatore,
                    List.of(capitano),
                    creaGestore,
                    calcolatoreDanno,
                    strategiaNemico
            );
        });

        return Map.copyOf(incontri);
    }
}
