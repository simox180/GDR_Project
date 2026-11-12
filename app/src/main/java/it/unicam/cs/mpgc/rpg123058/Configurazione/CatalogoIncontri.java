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

//il catalogo è necessario per creare gli oggetti ognuno con uno stato diverso e mutabile, come per esempio i nemici

/**
 * Il catalogo è necessario per creare gli oggetti ognuno con uno stato diverso e mutabile, come per esempio i nemici,
 * evitando di usare lo stesso nemico danneggiato (se volessi usare gli stessi nemici dovrei avere un metodo che rifulla la vita)
 */
public class CatalogoIncontri {

    /**
     * Costruisce una mappa associando ogni ID di un incontro a una funzione capace di creare quel combattimento.
     * Eseguendo questo metodo però la funzione che crea gli incontri non viene eseguita, verrà eseguita
     * successivamente in Partita con eseguiCombattimento() con .apply solo quando viene selezionata una SceltaCombattimento
     *
     * @param oggetti assegnati come bottino
     * @param creaGestore funzione che crea un gestoreTurni per ogni incontro
     * @param calcolatoreDanno usato per gli incontri
     * @param strategiaNemico il tipo di strategia adottata dai nemici (in questo caso è unica)
     * @return la mappa con gli ID degli incontri associata alla funzione che li crea usando Giocatore
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

        //creazione degli incontri tramite una mappa associando il loro ID a una funzione che crea un Combattimento specifico
        //incontro 1 (Combattimento con 2 orchi di cui 1 droppa la spada)
        incontri.put("orchi_galleria", giocatore -> {

            Nemico esploratore = new Nemico("Orco esploratore", 20, 6, 1, null);

            Nemico guerriero = new Nemico("Orco guerriero", 25, 7, 2, spada);

            return new Combattimento(giocatore, List.of(esploratore, guerriero), creaGestore, calcolatoreDanno, strategiaNemico);
        });

        //incontro 2 (Combattimento con 2 orchi di cui uno droppa un martello)
        incontri.put("orchi_inseguitori", giocatore -> {
            Nemico inseguitore = new Nemico("Orco inseguitore", 28, 8, 2, null);
            Nemico veterano = new Nemico("Orco veterano", 35, 9, 3, martello);

            return new Combattimento(
                    giocatore,
                    List.of(inseguitore, veterano),
                    creaGestore,
                    calcolatoreDanno,
                    strategiaNemico
            );
        });

        //incontro 3 (Combattimento finale con il capitano degli orchi)
        incontri.put("capitano_orchi", giocatore -> {
            Nemico capitano = new Nemico("Capitano degli Orchi", 60, 12, 4, null);

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
